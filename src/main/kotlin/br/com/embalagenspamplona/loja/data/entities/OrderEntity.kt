package br.com.embalagenspamplona.loja.data.entities

import br.com.embalagenspamplona.loja.data.enums.OrderStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZonedDateTime
import kotlin.random.Random

@Entity
@Table(name = "orders")
class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    val id: Long = 0L,

    @Column(nullable = true)
    val date: ZonedDateTime = ZonedDateTime.now(),

    /* @Column(name = "client_name", nullable = false, length = 150)
     val clientName: String,

     @Column(name = "client_email", nullable = false, length = 150)
     val clientEmail: String,

     @Column(name = "client_phone", length = 20)
     val clientPhone: String? = null,*/
    @ManyToOne
    @JoinColumn(
        name = "user_id",
        referencedColumnName = "user_id",
        foreignKey = ForeignKey(name = "fk_order_user_id"),
        nullable = false
    )
    val user: UserEntity? = null,

    @Column(name = "order_number")
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

    @Column(name = "stripe_payment_intent")
    val stripePaymentIntentId: String = "",

    @OneToOne
    @JoinColumn(name = "cart_order", referencedColumnName = "cart_id", foreignKey = ForeignKey(name = "fk_cart_id"))
    val cart: CartEntity? = null,

    @Column(name = "shipping_address", nullable = false)
    val shippingAddress: String = "",

    /* @OneToOne(mappedBy = "order")
     val paymentEntity: PaymentEntity? = null,*/


    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now(),


    @Column(name = "updated_at")
    val updatedAt: ZonedDateTime? = null

) {
    fun copy(
        id: Long = this.id,
        date: ZonedDateTime = this.date,
        user: UserEntity? = this.user,
        orderNumber: Long = this.orderNumber,
        items: MutableList<OrderItemEntity> = this.items,
        totalPrice: BigDecimal = this.totalPrice,
        subTotal: BigDecimal = this.subTotal,
        feePrice: BigDecimal = this.feePrice,
        status: OrderStatus = this.status,
        stripePaymentIntentId: String = this.stripePaymentIntentId,
        cart: CartEntity? = this.cart,
        shippingAddress: String = this.shippingAddress,
        createdAt: ZonedDateTime = this.createdAt,
        updatedAt: ZonedDateTime? = this.updatedAt
    ) = OrderEntity(
        id = id, date = date, user = user, orderNumber = orderNumber,
        items = items, totalPrice = totalPrice, subTotal = subTotal,
        feePrice = feePrice, status = status, stripePaymentIntentId = stripePaymentIntentId,
        cart = cart, shippingAddress = shippingAddress,
        createdAt = createdAt, updatedAt = updatedAt
    )
}
