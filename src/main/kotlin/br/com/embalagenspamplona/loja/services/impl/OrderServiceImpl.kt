package br.com.embalagenspamplona.loja.services.impl

import br.com.embalagenspamplona.loja.adapters.Mapper
import br.com.embalagenspamplona.loja.data.dto.*
import br.com.embalagenspamplona.loja.data.entities.OrderEntity
import br.com.embalagenspamplona.loja.data.entities.OrderItemEntity
import br.com.embalagenspamplona.loja.data.entities.UserEntity
import br.com.embalagenspamplona.loja.data.enums.OrderStatus
import br.com.embalagenspamplona.loja.exceptions.InternalServerException
import br.com.embalagenspamplona.loja.repository.datasource.local.OrderRepository
import br.com.embalagenspamplona.loja.repository.datasource.local.ProductRepository
import br.com.embalagenspamplona.loja.services.OrderService
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Service
@Transactional
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository
) : OrderService {

    override fun findById(id: Long): OrderDTO {
        return orderRepository.findByIdWithItems(id)
            ?.toDTO()
            ?: throw EntityNotFoundException("Pedido não encontrado: $id")
    }


    override fun findByClientEmail(clientEmail: String, page: Int, size: Int): PagedResponse<OrderDTO> {
        val pageRequest = PageRequest.of(page, size)
        val orderPage = orderRepository.findByUserEmail(clientEmail, pageRequest)

        return PagedResponse(
            content = orderPage.content.map { it.toDTO() },
            totalElements = orderPage.totalElements,
            totalPages = orderPage.totalPages,
            currentPage = orderPage.number,
            size = orderPage.size,
            hasNext = orderPage.hasNext(),
            hasPrevious = orderPage.hasPrevious()
        )
    }


    override fun updateOrderStatus(orderId: Long, orderStatus: OrderStatus) {
        orderRepository.updateOrderStatus(orderId, orderStatus)
    }

    override fun findAll(filter: OrderFilterRequest): PagedResponse<OrderDTO> {
        val pageRequest = PageRequest.of(filter.page, filter.size)

        val orderPage = when {
            filter.status != null -> orderRepository.findByStatus(filter.status, pageRequest)
            filter.startDate != null && filter.endDate != null ->
                orderRepository.findByDateRange(filter.startDate, filter.endDate, pageRequest)

            else -> orderRepository.findAll(pageRequest)
        }

        return PagedResponse(
            content = orderPage.content.map { it.toDTO() },
            totalElements = orderPage.totalElements,
            totalPages = orderPage.totalPages,
            currentPage = orderPage.number,
            size = orderPage.size,
            hasNext = orderPage.hasNext(),
            hasPrevious = orderPage.hasPrevious()
        )
    }


   /*
    comentado porque no checkout ja faz a criacao do pedido
   override fun createOrder(order: OrderDTO): OrderDTO? {

        // Criar o pedido
        val orderEntity = OrderEntity(
            date = ZonedDateTime.now(),
            shippingAddress = order.shippingAddress.toString(),
            status = OrderStatus.REQUESTED,
            createdAt = ZonedDateTime.now(),
        )

        val savedOrder = orderRepository.save(orderEntity)

        // Criar os itens do pedido
        var total = BigDecimal.ZERO
        val subTotal = order.subTotalPrice
        val feePrice = order.feePrice
        val items = order.items.map { itemRequest ->
            val product = productRepository.findById(itemRequest.id)
                .orElseThrow { EntityNotFoundException("Produto não encontrado: ${itemRequest.id}") }



            if (product.quantity < itemRequest.quantity) {
                throw IllegalArgumentException("Estoque insuficiente para ${product.name}. Disponível: ${product.quantity}")
            }

            val price = product.promotion?.price ?: product.price
            val itemTotal = price.multiply(BigDecimal(itemRequest.quantity))
            total = total.add(itemTotal)
            total = total.add(feePrice)

            // Atualizar estoque do produto
            productRepository.save(
                product.copy(
                    quantity = (product.quantity - itemRequest.quantity).toInt(),
                    updatedAt = ZonedDateTime.now()
                )
            )

            OrderItemEntity(
                order = savedOrder,
                product = product,
                name = product.name,
                price = price,
                quantitySelected = itemRequest.quantity,
                pack = itemRequest.pack
            )
        }

        savedOrder.items.addAll(items)

        // Atualizar o total do pedido
        val updatedOrder = savedOrder.copy(totalPrice = total, subTotal = subTotal, feePrice = feePrice)
        val finalOrder = orderRepository.save(updatedOrder)

        try {
            val dto = Mapper().mapTo(finalOrder::class.java, OrderDTO::class.java)
            if (dto != null) {
                return dto
            } else {
                return null
            }
        } catch (e: Exception) {
            throw InternalServerException(Exception("Erro ao converter dados de pedido ${e.message}"))

        }
        // return findById(finalOrder.id!!)
        // return findById(finalOrder.id!!)
    }*/

    override fun updateStatus(id: Long, request: UpdateOrderStatusRequest): OrderDTO {
        val order = orderRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Pedido não encontrado: $id") }

        val updatedOrder = order.copy(
            status = request.status,

            updatedAt = ZonedDateTime.now()
        )

        return orderRepository.save(updatedOrder).toDTO()
    }

    override fun cancelOrder(id: Long, reason: String?): OrderDTO {
        val order = orderRepository.findByIdWithItems(id)
            ?: throw EntityNotFoundException("Pedido não encontrado: $id")

        if (order.status == OrderStatus.SHIPPED || order.status == OrderStatus.DELIVERED) {
            throw IllegalArgumentException("Não é possível cancelar um pedido já enviado ou entregue")
        }

        // Restaurar estoque dos produtos
        order.items.forEach { item ->
            if(item.product!=null){
            val product = productRepository.findById(item.product.id).orElse(null)
            if (product != null) {
                productRepository.save(
                    product.copy(
                        quantity = product.quantity + item.quantitySelected.toInt(),
                        updatedAt = ZonedDateTime.now()
                    )
                )
            }
        }
        }

        val cancelledOrder = order.copy(
            status = OrderStatus.CANCELLED,

            updatedAt = ZonedDateTime.now()
        )

        return orderRepository.save(cancelledOrder).toDTO()
    }

    override fun delete(id: Long) {
        if (!orderRepository.existsById(id)) {
            throw EntityNotFoundException("Pedido não encontrado: $id")
        }
        orderRepository.deleteById(id)
    }

    private fun OrderEntity.toDTO(): OrderDTO {
        return OrderDTO(
            id = this.id,
            date = this.date,
            orderNumber = this.orderNumber,
            user = this.user?.toDTO() ?: UserDTO(name = "", email = ""),
            items = this.items.map { it.toDTO() },
            total = this.totalPrice,
            subTotalPrice = this.subTotal,
            feePrice = this.feePrice,
            status = this.status,
            stripePaymentIntentID = this.stripePaymentIntentId,
            shippingAddress = this.shippingAddress,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    private fun UserEntity.toDTO(): UserDTO {
        return UserDTO(
            id = this.id,
            name = this.name.toString(),
            email = this.email.toString(),
            phone = this.phone,
            cpfCnpj = this.cpfCnpj,
            birthday = this.birthday
        )
    }

    private fun OrderItemEntity.toDTO(): OrderItemDTO {
        return OrderItemDTO(
            id = this.id,
            name = this.name,
            description = this.product?.description,
            price = this.price,
            quantity = this.quantitySelected,
            sku = this.product?.sku,
            createdAt = this.createdAt,
            pack = this.pack
        )
    }

}
