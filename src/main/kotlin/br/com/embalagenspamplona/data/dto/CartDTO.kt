package br.com.embalagenspamplona.data.dto

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class CartDTO(
    val id: Long? = null,
    val customerId: Long? = null,
    val items: List<CartItemDTO> = emptyList(),
    val couponCode: String? = null,
    val subtotal: BigDecimal = BigDecimal.ZERO,
    val discountAmount: BigDecimal = BigDecimal.ZERO,
    val total: BigDecimal = BigDecimal.ZERO,
    val itemCount: Int = 0
)

data class CartItemDTO(
    val id: Long? = null,
    val name: String ="",
    val productId: Long= 0L,
    val description: String? = null,
    val price: BigDecimal= BigDecimal("0.00"),
    val promotion: PromotionDTO,
    val quantity: Long = 0L,
    val sku: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
   /* val imageUrl: String? = null,
    val additionalImages: List<String> = emptyList(),*/
    val pack: String? = null,
)

data class AddToCartRequest(
    val productId: String,
    val quantity: Int = 1
)

data class UpdateCartItemRequest(
    val productId: Long,
    val quantity: Int
)

data class ApplyCouponRequest(
    val couponCode: String
)

data class CartSummaryDTO(
    val subtotal: BigDecimal,
    val discountAmount: BigDecimal,
    val shippingAmount: BigDecimal,
    val total: BigDecimal,
    val itemCount: Int,
    val couponCode: String? = null
)
