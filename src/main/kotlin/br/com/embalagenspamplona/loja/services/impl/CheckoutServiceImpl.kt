package br.com.embalagenspamplona.loja.services.impl

import br.com.embalagenspamplona.loja.data.dto.CheckoutItemResponse
import br.com.embalagenspamplona.loja.data.dto.CheckoutResponse
import br.com.embalagenspamplona.loja.data.dto.OrderRequest
import br.com.embalagenspamplona.loja.data.entities.AddressEntity
import br.com.embalagenspamplona.loja.data.entities.OrderEntity
import br.com.embalagenspamplona.loja.data.entities.OrderItemEntity
import br.com.embalagenspamplona.loja.data.enums.OrderStatus
import br.com.embalagenspamplona.loja.exceptions.InternalServerException
import br.com.embalagenspamplona.loja.exceptions.NotFoundException
import br.com.embalagenspamplona.loja.repository.datasource.local.AddressRepository
import br.com.embalagenspamplona.loja.repository.datasource.local.CartItemRepository
import br.com.embalagenspamplona.loja.repository.datasource.local.CartRepository
import br.com.embalagenspamplona.loja.repository.datasource.local.OrderRepository
import br.com.embalagenspamplona.loja.repository.datasource.local.ProductRepository
import br.com.embalagenspamplona.loja.repository.datasource.local.UserRepository
import br.com.embalagenspamplona.loja.services.CheckoutService
import com.stripe.exception.StripeException
import com.stripe.model.checkout.Session
import com.stripe.param.checkout.SessionCreateParams
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Service
@Transactional
class CheckoutServiceImpl(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val cartItemRepository: CartItemRepository,
    private val userRepository: UserRepository,
    private val addressRepository: AddressRepository
) : CheckoutService {

    private val logger = LoggerFactory.getLogger(CheckoutServiceImpl::class.java)

    override fun checkout(request: OrderRequest): CheckoutResponse {
        // 1. Buscar usuário
        val user = userRepository.findById(request.userId)
            .orElseThrow { EntityNotFoundException("Usuário não encontrado: ${request.userId}") }

        if (request.orderItems.isEmpty()) {
            throw IllegalArgumentException("O pedido não contém itens")
        }

        // 2. Resolver endereço de entrega
        val shippingAddress = listOfNotNull(
            request.paymentData.address,
            request.paymentData.neighborhood,
            request.paymentData.complementNumber,
            request.paymentData.unityBlock,
            request.paymentData.city,
            request.paymentData.state,
            request.paymentData.zipCode
        ).filter { it.isNotBlank() }.joinToString(", ")

        val addressEntity = AddressEntity(
            customer = user,
            state = request.paymentData.state.toString(),
            city = request.paymentData.city.toString(),
            reference = request.paymentData.reference.toString(),
            street = request.paymentData.address.toString(),
            number = request.paymentData.complementNumber.toString(),
            complementNumber = request.paymentData.unityBlock.toString(),
            neighborhood = request.paymentData.neighborhood.toString(),
            zipCode = request.paymentData.zipCode.toString()
        )

        val currUser = userRepository.findById(user.id!!)
            .orElseThrow { NotFoundException(Exception("User not found to add address")) }
        currUser.addresses.add(addressEntity)
        userRepository.save(currUser)
        addressRepository.save(addressEntity)

        if (shippingAddress.isBlank()) {
            throw IllegalArgumentException("Endereço de entrega é obrigatório")
        }

        // 3. Validar estoque e preparar itens
        val validatedItems = request.orderItems.map { itemDTO ->
            val productId = itemDTO.id
                ?: throw IllegalArgumentException("ID do produto é obrigatório")

            val product = productRepository.findById(productId)
                .orElseThrow { EntityNotFoundException("Produto não encontrado: $productId") }

            if (product.quantity < itemDTO.quantity) {
                throw IllegalArgumentException(
                    "Estoque insuficiente para '${product.name}'. Disponível: ${product.quantity}"
                )
            }

            Pair(product, itemDTO)
        }

        // 4. Criar o pedido
        val order = orderRepository.save(
            OrderEntity(
                user = user,
                totalPrice = request.totalPrice,
                subTotal = request.subTotal,
                feePrice = request.orderFee ?: BigDecimal.ZERO,
                status = OrderStatus.REQUESTED,
                shippingAddress = shippingAddress
            )
        )

        // 5. Converter itens do request em itens do pedido e atualizar estoque
        validatedItems.forEach { (product, itemDTO) ->
            order.items.add(
                OrderItemEntity(
                    order = order,
                    product = product,
                    name = itemDTO.name.ifBlank { product.name },
                    price = itemDTO.price,
                    quantitySelected = itemDTO.quantity,
                    pack = itemDTO.pack ?: product.pack
                )
            )

            // Decrementar estoque do produto
            productRepository.save(
                product.copy(
                    quantity = product.quantity - itemDTO.quantity.toInt(),
                    updatedAt = ZonedDateTime.now()
                )
            )
        }

        // Salvar pedido com os itens (cascade persiste os OrderItemEntity)
        val savedOrder = orderRepository.save(order)

        // 6. Limpar o carrinho (se existir)
        val cart = cartRepository.findByUserEntityId(request.userId)
        cart.ifPresent { cartItemRepository.deleteAllByCartId(it.id) }

        // 7. Retornar resposta do checkout
        return savedOrder.toCheckoutResponse()
    }

    override fun createStripeSession(orderId: Long): Map<String, String> {
        val order = orderRepository.findByIdWithItems(orderId)
            ?: throw EntityNotFoundException("Pedido não encontrado: $orderId")

        val params = SessionCreateParams.builder()
            .setUiMode(SessionCreateParams.UiMode.EMBEDDED)
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
            .setPaymentMethodOptions(
                SessionCreateParams.PaymentMethodOptions.builder().setCard(
                    SessionCreateParams.PaymentMethodOptions.Card.builder()
                        .setInstallments(
                            SessionCreateParams.PaymentMethodOptions.Card.Installments.builder()
                                .setEnabled(true)
                                .build()
                        )
                        .build()
                ).build()
            )
            .addPaymentMethodType(SessionCreateParams.PaymentMethodType.PIX)
            .setPaymentMethodOptions(
                SessionCreateParams.PaymentMethodOptions.builder().setPix(
                    SessionCreateParams.PaymentMethodOptions.Pix
                        .builder().setExpiresAfterSeconds(3600L).build()
                ).build()
            )
            .setReturnUrl("https://www.embalagenspamplona.com.br/checkout?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl("https://www.embalagenspamplona.com.br")
            .setClientReferenceId(order.id.toString())
            .putMetadata("order_number", order.orderNumber.toString())
            .setAutomaticTax(SessionCreateParams.AutomaticTax.builder().setEnabled(false).build())
            .setTaxIdCollection(
                SessionCreateParams.TaxIdCollection.builder()
                    .setEnabled(true)
                    .build()
            )
            .setExpiresAt(Instant.now().plus(2, ChronoUnit.HOURS).epochSecond)

        // Adicionar os itens do pedido como line items do Stripe
        order.items.forEach { item ->
            params.addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setQuantity(item.quantitySelected)
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("brl")
                            .setUnitAmountDecimal(
                                item.price.multiply(BigDecimal(100))
                            )
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData
                                    .builder().setName(item.name).build()
                            ).build()
                    ).build()
            )
        }

        val session = Session.create(params.build())

        // Salvar o ID do payment intent/session no pedido
        val stripeId = session.paymentIntent ?: session.id
        orderRepository.save(order.copy(stripePaymentIntentId = stripeId))

        logger.info("Stripe session criada para pedido $orderId: ${session.id}")

        return mapOf(
            "clientSecret" to (session.clientSecret ?: ""),
            "sessionId" to session.id
        )
    }

    override fun confirmPayment(sessionId: String): Map<String, Any> {
        if (sessionId.isEmpty()) {
            throw InternalServerException(Exception("Stripe session cant be null"))
        }
        val session = Session.retrieve(sessionId)
        return try {

            if (session.paymentStatus == "paid") {
                val orderId = session.clientReferenceId?.toLongOrNull()
                if (orderId != null) {
                    orderRepository.updateOrderStatus(orderId, OrderStatus.PAID)
                    logger.info("Pagamento confirmado para pedido $orderId")
                }

                mapOf(
                    "status" to "paid",
                    "orderId" to (session.clientReferenceId ?: ""),
                    "message" to "Pagamento confirmado com sucesso"
                )
            } else {
                val orderId = session.clientReferenceId?.toLongOrNull()
                if (orderId != null) {
                    orderRepository.updateOrderStatus(orderId = orderId, OrderStatus.PENDING)
                    mapOf(
                        "status" to session.paymentStatus,
                        "orderId" to (session.clientReferenceId ?: ""),
                        "message" to "Pagamento não confirmado: ${session.paymentStatus}"
                    )
                } else {
                    mapOf(
                        "status" to session.paymentStatus,
                        "orderId" to (session.clientReferenceId ?: ""),
                        "message" to "Status do pagamento: ${session.paymentStatus}"
                    )
                }
            }
        } catch (e: StripeException) {
            session.expire()
            logger.error("Erro Stripe ao confirmar pagamento da sessão $sessionId: ${e.message}")
            mapOf(
                "status" to "error",
                "message" to "Erro ao confirmar pagamento: ${e.message}"
            )
        }
    }

    private fun OrderEntity.toCheckoutResponse(): CheckoutResponse {
        return CheckoutResponse(
            orderId = this.id,
            status = this.status,
            items = this.items.map { item ->
                CheckoutItemResponse(
                    productId = item.product?.id ?: 0L,
                    productName = item.name,
                    quantity = item.quantitySelected.toInt(),
                    unitPrice = item.price,
                    totalPrice = item.price.multiply(BigDecimal(item.quantitySelected))
                )
            },
            totalPrice = this.totalPrice,
            subTotalPrice = this.subTotal,
            orderFee = this.feePrice,
            shippingAddress = this.shippingAddress,
            createdAt = this.createdAt
        )
    }
}
