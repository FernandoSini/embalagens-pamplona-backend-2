package br.com.embalagenspamplona.loja.services.impl

import br.com.embalagenspamplona.loja.adapters.Mapper
import br.com.embalagenspamplona.loja.data.dto.*
import br.com.embalagenspamplona.loja.data.entities.CartEntity
import br.com.embalagenspamplona.loja.data.entities.CartItemEntity
import br.com.embalagenspamplona.loja.data.entities.OrderEntity
import br.com.embalagenspamplona.loja.data.entities.PromotionEntity
import br.com.embalagenspamplona.loja.data.entities.UserEntity
import br.com.embalagenspamplona.loja.repository.datasource.local.*
import br.com.embalagenspamplona.loja.services.CartService
import br.com.embalagenspamplona.loja.services.CouponService
import br.com.embalagenspamplona.loja.services.UserService
import jakarta.persistence.EntityNotFoundException
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZonedDateTime


@Service
@Transactional
class CartServiceImpl(
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository,
    private val userService: UserService,
    private val couponService: CouponService,
    private val cartRepository: CartRepository
) : CartService {

    private val mapper = Mapper()

    override fun getCart(customerId: Long): CartDTO {
        val cart = getOrCreateCart(customerId)
        return cart.toDTO()
    }

    override fun findByUserId(userId: Long): CartDTO {
        val cartEntity = cartRepository.findByUserEntityId(userId)
            .orElseThrow { EntityNotFoundException("Carrinho não encontrado para esse usuário") }
        val cart = mapper.mapTo(cartEntity::class.java, CartDTO::class.java)
        if (cart != null) {
            return cart;
        }
        return CartDTO()

    }

    override fun addItem(customerId: Long, request: CartItemDTO): CartDTO {
        val cart = getOrCreateCart(customerId)
        val product = productRepository.findById(request.productId)
            .orElseThrow { EntityNotFoundException("Produto não encontrado: ${request.productId}") }

        if (product.quantity <= 0) {
            throw IllegalArgumentException("Produto não está disponível")
        }

        if (product.quantity < request.quantity) {
            throw IllegalArgumentException("Estoque insuficiente. Disponível: ${product.quantity}")
        }

        val existingItem = cartItemRepository.findByCartIdAndProductId(cart.id, product.id!!)

        if (existingItem.isPresent) {
            val item = existingItem.get()
            val newQuantity = item.quantity + request.quantity

            if (product.quantity < newQuantity) {
                throw IllegalArgumentException("Estoque insuficiente. Disponível: ${product.quantity}")
            }

            cartItemRepository.save(
                item.copy(
                    quantity = newQuantity.toInt(),
                    updatedAt = ZonedDateTime.now()
                )
            )
        } else {
            val unitPrice = product.promotion?.price ?: product.price
            val cartItem = CartItemEntity(
                cart = cart,
                product = product,
                quantity = request.quantity.toInt(),
                unitPrice = unitPrice
            )
            cartItemRepository.save(cartItem)
        }

        return getCart(customerId)
    }

    override fun updateCart(customerId: Long, items: List<CartItemDTO>): CartDTO {
        val cart = getOrCreateCart(customerId)

        // Mapeia itens existentes no carrinho por productId
        val existingItemsByProductId = cart.items.associateBy { it.product?.id.toString() }

        // IDs dos produtos que estão vindo na requisição
        val incomingProductIds = items.map { it.id }.toSet()

        // Restaura estoque e remove itens que não estão mais na requisição
        existingItemsByProductId.forEach { (productId, cartItem) ->
            if (productId.toLong() !in incomingProductIds) {
                val product = cartItem.product!!
                productRepository.save(product.copy(quantity = product.quantity + cartItem.quantity))
                cartItemRepository.delete(cartItem)
            }
        }

        // Processa cada item da requisição
        items.forEach { itemDTO ->
            if (itemDTO.quantity <= 0) return@forEach

            val product = productRepository.findById(itemDTO.id!!)
                .orElseThrow { EntityNotFoundException("Produto não encontrado: ${itemDTO.id}") }

            val existingItem = existingItemsByProductId[itemDTO.id.toString()]

            if (existingItem != null) {
                // Item já existe no carrinho - calcula a diferença
                val diff = itemDTO.quantity - existingItem.quantity

                if (diff > 0 && product.quantity < diff) {
                    throw IllegalArgumentException(
                        "Estoque insuficiente para '${product.name}'. Disponível: ${product.quantity}"
                    )
                }

                // Atualiza o estoque do produto (decrementa se diff > 0, incrementa se diff < 0)
                productRepository.save(product.copy(quantity = product.quantity - diff.toInt()))

                // Atualiza o item do carrinho
                cartItemRepository.save(
                    existingItem.copy(
                        quantity = itemDTO.quantity.toInt(),
                        updatedAt = ZonedDateTime.now()
                    )
                )
            } else {
                // Item novo - valida estoque
                if (product.quantity < itemDTO.quantity) {
                    throw IllegalArgumentException(
                        "Estoque insuficiente para '${product.name}'. Disponível: ${product.quantity}"
                    )
                }

                // Decrementa o estoque do produto
                productRepository.save(product.copy(quantity = product.quantity - itemDTO.quantity.toInt()))

                // Cria o item no carrinho
                val unitPrice = itemDTO.price
                val cartItem = CartItemEntity(
                    cart = cart,
                    product = product,
                    quantity = itemDTO.quantity.toInt(),
                    unitPrice = unitPrice
                )
                cartItemRepository.save(cartItem)
            }
        }

        return getCart(customerId)
    }

    override fun updateItemQuantity(customerId: Long, itemId: Long, request: UpdateCartItemRequest): CartDTO {
        val cart = cartRepository.findByUserEntityId(customerId)
            .orElseThrow { EntityNotFoundException("Carrinho não encontrado") }

        val item = cartItemRepository.findById(itemId)
            .orElseThrow { EntityNotFoundException("Item não encontrado: $itemId") }

        if (item.cart?.id != cart.id) {
            throw IllegalArgumentException("Item não pertence a este carrinho")
        }

        val product = item.product!!
        if (product.quantity < request.quantity) {
            throw IllegalArgumentException("Estoque insuficiente. Disponível: ${product.quantity}")
        }

        if (request.quantity <= 0) {
            cartItemRepository.delete(item)
        } else {
            cartItemRepository.save(
                item.copy(
                    quantity = request.quantity,
                    updatedAt = ZonedDateTime.now()
                )
            )
        }

        return getCart(customerId)
    }

    override fun removeItem(customerId: Long, itemId: Long): CartDTO {
        val cart = cartRepository.findByUserEntityId(customerId)
            .orElseThrow { EntityNotFoundException("Carrinho não encontrado") }

        val item = cartItemRepository.findById(itemId)
            .orElseThrow { EntityNotFoundException("Item não encontrado: $itemId") }

        if (item.cart?.id != cart.id) {
            throw IllegalArgumentException("Item não pertence a este carrinho")
        }

        cartItemRepository.delete(item)
        return getCart(customerId)
    }

    override fun clearCart(customerId: Long) {
        val cart = cartRepository.findByUserEntityId(customerId)
            .orElseThrow { EntityNotFoundException("Carrinho não encontrado") }

        // Restaura as quantidades dos produtos no estoque antes de limpar o carrinho
        cart.items.forEach { item ->
            val product = item.product
                ?: throw EntityNotFoundException("Produto não encontrado para o item do carrinho: ${item.id}")
            productRepository.save(
                product.copy(quantity = product.quantity + item.quantity)
            )
        }

        cartItemRepository.deleteAllByCartId(cart.id)
        cartRepository.save(
            cart.copy(
                discountAmount = BigDecimal.ZERO,
                updatedAt = LocalDateTime.now()
            )
        )
    }

    override fun applyCoupon(customerId: Long, request: ApplyCouponRequest): CartDTO {
        val cart = getOrCreateCart(customerId)

        if (!couponService.validateCoupon(request.couponCode, cart.subtotal)) {
            throw IllegalArgumentException("Cupom inválido ou não aplicável")
        }

        val discount = couponService.calculateDiscount(request.couponCode, cart.subtotal)

        cartRepository.save(
            cart.copy(
                //couponCode = request.couponCode,
                // discountAmount = discount,
                updatedAt = LocalDateTime.now()
            )
        )

        return getCart(customerId)
    }

    override fun removeCoupon(customerId: Long): CartDTO {
        val cart = cartRepository.findByUserEntityId(customerId)
            .orElseThrow { EntityNotFoundException("Carrinho não encontrado") }

        cartRepository.save(
            cart.copy(
                //discountAmount = BigDecimal.ZERO,
                updatedAt = LocalDateTime.now()
            )
        )

        return getCart(customerId)
    }

    override fun getCartSummary(customerId: Long): CartSummaryDTO {
        val cart = getOrCreateCart(customerId)

        return CartSummaryDTO(
            subtotal = cart.subtotal,
            discountAmount = cart.discountAmount,
            shippingAmount = BigDecimal.ZERO, // Calcular frete separadamente
            total = cart.total,
            itemCount = cart.itemCount,
            // couponCode = cart.couponCode
        )
    }

    override fun validateCart(customerId: Long): List<String> {
        val cart =
            cartRepository.findByUserEntityId(customerId).orElseThrow { EntityNotFoundException("Cart not found") }
                ?: return listOf("Carrinho vazio")

        val errors = mutableListOf<String>()

        if (cart.items.isEmpty()) {
            errors.add("Carrinho vazio")
            return errors
        }

        cart.items.forEach { item ->
            val product = item.product!!
            /* if (!product.active) {
                 errors.add("Produto '${product.name}' não está mais disponível")
             }*/
            if (product.quantity < item.quantity) {
                errors.add("Produto '${product.name}' tem apenas ${product.quantity} unidades em estoque")
            }
        }

        return errors
    }

    private fun getOrCreateCart(customerId: Long): CartEntity {
        return cartRepository.findByUserEntityIdWithItems(customerId)
            ?: run {
                val customer = userService.findById(customerId)
                if (customer == null) {
                    throw EntityNotFoundException("Cliente não encontrado: $customerId")
                }


                val customerEntity = Mapper().mapTo(customer::class.java, UserEntity::class.java)
                cartRepository.save(
                    CartEntity(
                        userEntity = customerEntity,
                        order = OrderEntity(user = customerEntity!!)
                    )
                )
            }
    }

    private fun CartEntity.toDTO(): CartDTO {
        return CartDTO(
            id = this.id,
            customerId = this.userEntity?.id,
            items = this.items.map { it.toDTO() },
            //couponCode = this.couponCode,
            subtotal = this.subtotal,
            discountAmount = this.discountAmount,
            total = this.total,
            itemCount = this.itemCount
        )
    }

    private fun CartItemEntity.toDTO(): CartItemDTO {
        return CartItemDTO(
            id = this.id,
            productId = this.product?.id ?: 0L,
            name = this.product?.name ?: "",
            promotion = PromotionDTO(
                this.product?.promotion?.id,
                code = this.product?.promotion?.code,
                description = this.product?.promotion?.description,
                price = this.product?.promotion?.price ?: BigDecimal("0.00"),
            ),
            quantity = this.quantity.toLong(),
            price = this.unitPrice,

            )
    }
}
