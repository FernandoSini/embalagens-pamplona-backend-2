package br.com.embalagenspamplona.loja.data.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "cart_items")
 class CartItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    val cart: CartEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_item_product", referencedColumnName = "product_id", foreignKey = ForeignKey(name = "fk_cart_item_product_id"))
    val product: ProductEntity? = null,

    @Column(name = "quantity", nullable = false)
    val quantity: Int = 0,

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    val unitPrice: BigDecimal = BigDecimal.ZERO,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: ZonedDateTime = ZonedDateTime.now()
) {
    val totalPrice: BigDecimal
        get() = unitPrice.multiply(BigDecimal(quantity))

    fun copy(
        id: Long = this.id,
        cart: CartEntity? = this.cart,
        product: ProductEntity? = this.product,
        quantity: Int = this.quantity,
        unitPrice: BigDecimal = this.unitPrice,
        createdAt: ZonedDateTime = this.createdAt,
        updatedAt: ZonedDateTime = this.updatedAt
    ) = CartItemEntity(
        id = id, cart = cart, product = product,
        quantity = quantity, unitPrice = unitPrice,
        createdAt = createdAt, updatedAt = updatedAt
    )
}
