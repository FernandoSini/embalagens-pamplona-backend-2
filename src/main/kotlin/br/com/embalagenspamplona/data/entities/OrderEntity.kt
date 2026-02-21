package br.com.embalagenspamplona.data.entities

import br.com.embalagenspamplona.data.enums.OrderStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZonedDateTime
import kotlin.random.Random

@Entity
@Table(name = "orders")
data class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = true)
    val date: ZonedDateTime = ZonedDateTime.now(),

    /* @Column(name = "client_name", nullable = false, length = 150)
     val clientName: String,

     @Column(name = "client_email", nullable = false, length = 150)
     val clientEmail: String,

     @Column(name = "client_phone", length = 20)
     val clientPhone: String? = null,*/
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @Column(name="order_number")
    val orderNumber: Long = Random.nextLong(6),

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val items: MutableList<OrderItemEntity> = mutableListOf(),

    @Column(nullable = false, precision = 10, scale = 2)
    val totalPrice: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false, precision = 10, scale = 2)
    val subTotal: BigDecimal = BigDecimal.ZERO,

    //taxa de entrega
    @Column(nullable = false, precision = 10, scale = 2)
    val feePrice: BigDecimal = BigDecimal.ZERO,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: OrderStatus = OrderStatus.PENDING,

    @Column(name="stripe_payment_intent")
    val stripePaymentIntentId: String = "",

    @OneToOne(mappedBy = "order")
    val cart: CartEntity?=null,

    @Column(name = "shipping_address", nullable = false)
    val shippingAddress: String = "",

   /* @OneToOne(mappedBy = "order")
    val paymentEntity: PaymentEntity? = null,*/

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now(),

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    val updatedAt: ZonedDateTime? = null

)
