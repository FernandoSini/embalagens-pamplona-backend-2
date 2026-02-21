package br.com.embalagenspamplona.data.entities

import br.com.embalagenspamplona.data.enums.PaymentMethod
import br.com.embalagenspamplona.data.enums.PaymentStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "payments")
data class PaymentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: Long? = 0L,

   /* @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    val order: OrderEntity? = null,*/

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val method: PaymentMethod,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: PaymentStatus = PaymentStatus.PENDING,

    @Column(nullable = false, precision = 10, scale = 2)
    val amount: BigDecimal,

    @Column(name = "transaction_id", length = 100)
    val transactionId: String? = null,

    @Column(name = "external_reference", length = 200)
    val externalReference: String? = null,

    @Column(name = "pix_code", length = 500)
    val pixCode: String? = null,

    @Column(name = "pix_qr_code")
    val pixQrCode: String? = null,

    @Column(name = "boleto_url")
    val boletoUrl: String? = null,

    @Column(name = "boleto_barcode", length = 100)
    val boletoBarcode: String? = null,

    @Column(name = "boleto_due_date")
    val boletoDueDate: ZonedDateTime? = null,

    @Column(name = "card_last_digits", length = 4)
    val cardLastDigits: String? = null,

    @Column(name = "card_brand", length = 50)
    val cardBrand: String? = null,

    @Column(name = "installments")
    val installments: Int = 1,

    @Column(name = "paid_at")
    val paidAt: ZonedDateTime? = null,

    @Column(name = "cancelled_at")
    val cancelledAt: ZonedDateTime? = null,

    @Column(name = "refunded_at")
    val refundedAt: ZonedDateTime? = null,

    @Column(name = "refund_amount", precision = 10, scale = 2)
    val refundAmount: BigDecimal? = null,

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    val gatewayResponse: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: ZonedDateTime = ZonedDateTime.now()
)
