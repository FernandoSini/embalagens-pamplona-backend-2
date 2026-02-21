package br.com.embalagenspamplona.data.dto

import br.com.embalagenspamplona.data.enums.PaymentMethod
import br.com.embalagenspamplona.data.enums.PaymentStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class PaymentDTO(
    val id: Long? = null,
    val orderId: Long? = null,
    val method: PaymentMethod,
    val status: PaymentStatus,
    val amount: BigDecimal,
    val transactionId: String? = null,
    val pixCode: String? = null,
    val pixQrCode: String? = null,
    val boletoUrl: String? = null,
    val boletoBarcode: String? = null,
    val boletoDueDate: LocalDateTime? = null,
    val cardLastDigits: String? = null,
    val cardBrand: String? = null,
    val installments: Int = 1,
    val paidAt: LocalDateTime? = null,
    val createdAt: LocalDateTime? = null
)

data class ProcessPaymentRequest(
    val orderId: Long,
    val method: PaymentMethod,
    val amount: BigDecimal,
    val installments: Int = 1,
    val cardToken: String? = null,
    val cardHolderName: String? = null,
    val cardHolderDocument: String? = null
)

data class PaymentWebhookRequest(
    val transactionId: String,
    val status: String,
    val externalReference: String? = null,
    val paidAt: LocalDateTime? = null,
    val gatewayResponse: String? = null
)

data class RefundRequest(
    val paymentId: Long,
    val amount: BigDecimal? = null,
    val reason: String? = null
)

data class PixPaymentResponse(
    val paymentId: Long,
    val pixCode: String,
    val pixQrCode: String,
    val expiresAt: LocalDateTime
)

data class BoletoPaymentResponse(
    val paymentId: Long,
    val boletoUrl: String,
    val boletoBarcode: String,
    val dueDate: LocalDateTime
)

data class CardPaymentResponse(
    val paymentId: Long,
    val status: PaymentStatus,
    val transactionId: String?,
    val cardLastDigits: String?,
    val cardBrand: String?
)
