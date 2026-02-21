package br.com.embalagenspamplona.data.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "carts")
data class CartEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: Long = 0L,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val userEntity: UserEntity? = null,

    @OneToMany(mappedBy = "cart", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val items: MutableList<CartItemEntity> = mutableListOf(),

    @Column(name = "discount_amount", precision = 10, scale = 2)
    val discountAmount: BigDecimal = BigDecimal.ZERO,

    @OneToOne(mappedBy = "cart")
    val order: OrderEntity?=null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    val subtotal: BigDecimal
        get() = items.sumOf { it.totalPrice }

    val total: BigDecimal
        get() = subtotal.subtract(discountAmount).coerceAtLeast(BigDecimal.ZERO)

    val itemCount: Int
        get() = items.sumOf { it.quantity }
}
