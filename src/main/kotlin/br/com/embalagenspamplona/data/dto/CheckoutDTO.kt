package br.com.embalagenspamplona.data.dto

import br.com.embalagenspamplona.data.enums.OrderStatus
import java.math.BigDecimal
import java.time.ZonedDateTime

data class CheckoutRequest(
    val shippingAddressId: Long? = null,
    val shippingAddress: String? = null
)

data class CheckoutResponse(
    val orderId: Long,
    val status: OrderStatus,
    val items: List<CheckoutItemResponse>,
    val totalPrice: BigDecimal,
    val subTotalPrice: BigDecimal,
    val orderFee: BigDecimal,
    val shippingAddress: String,
    val createdAt: ZonedDateTime,
    val stripeClientSecret: String? = null,
    val stripeSessionId: String? = null
)

data class CheckoutItemResponse(
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val totalPrice: BigDecimal
)
