package br.com.embalagenspamplona.loja.data.dto

import br.com.embalagenspamplona.loja.data.enums.OrderStatus
import br.com.embalagenspamplona.loja.data.enums.PaymentMethod
import org.springframework.cglib.core.Block
import org.springframework.data.redis.core.types.Expiration
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZonedDateTime

data class PaymentMethodDTO(
    val value: String,
    val label: String
)

data class DeliveryType(
    val id: Long = 1L,
    val name: String = "Retirada"
)

data class PaymentData(
    val userId: Long,
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String,
    val cardNumber: String,
    val cardExpiration: String,
    val cardCVV: String,
    val cardHolderName: String,
    val installments: Long = 0L,
    val deliveryType: DeliveryType,
    val cpfCnpj: String,
    val zipCode: String? = null,
    val address: String? = null,
    val paymentMethod: PaymentMethodDTO,
    val complementNumber: String? = null,
    val unityBlock: String? = null,
    val neighborhood: String? = null,
    val reference: String? = null,
    val city: String? = null,
    val state: String? = null,
)

data class OrderRequest(
    val date: ZonedDateTime? = ZonedDateTime.now(),
    val userId: Long,
    val orderItems: MutableList<OrderItemDTO> = mutableListOf(),
    val paymentData: PaymentData,
    val totalPrice: BigDecimal,
    val subTotal: BigDecimal,
    val orderFee: BigDecimal? = null,
)

data class OrderDTO(
    val id: Long? = null,
    val date: ZonedDateTime,
    val orderNumber: Long,
    /*  val clientName: String,
      val clientEmail: String,
      val clientPhone: String? = null,*/
    val user: UserDTO,
    val items: List<OrderItemDTO> = emptyList(),
    val total: BigDecimal = BigDecimal.ZERO,
    val subTotalPrice: BigDecimal = BigDecimal.ZERO,
    val feePrice: BigDecimal = BigDecimal.ZERO,
    val status: OrderStatus = OrderStatus.PENDING,
    val stripePaymentIntentID: String = "",
    val notes: String? = null,
    val shippingAddress: String? = null,
    val createdAt: ZonedDateTime? = null,
    val updatedAt: ZonedDateTime? = null
)

data class OrderItemDTO(
    val id: Long? = null,
    val name: String ="",
    val description: String? = null,
    val price: BigDecimal= BigDecimal("0.00"),
    val promotions: MutableList<PromotionDTO>? = null,
    val quantity: Long = 0L,
    val sku: String? = null,
    val createdAt: ZonedDateTime? = null,
    val updatedAt: ZonedDateTime? = null,
  /*  val imageUrl: String? = null,
    val additionalImages: List<String> = emptyList(),*/
    val pack: String? = null,
) {
    val totalPrice: BigDecimal
        get() = price.multiply(BigDecimal(quantity))
}

data class CreateOrderRequest(
    val clientName: String="",
    val clientEmail: String="",
    val clientPhone: String? = null,
    val items: List<CreateOrderItemRequest>,
    val notes: String? = null,
    val shippingAddress: String? = null
)

data class CreateOrderItemRequest(
    val productId: String, // ID do produto (ex: "pp-101")
    val quantity: Int,
    val pack: String? = null
)

data class UpdateOrderStatusRequest(
    val status: OrderStatus,
    val notes: String? = null
)

data class OrderFilterRequest(
    val clientEmail: String? = null,
    val status: OrderStatus? = null,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val page: Int = 0,
    val size: Int = 20
)
