package br.com.embalagenspamplona.data.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "coupons")
data class CouponEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, unique = true, length = 50)
    val code: String,

    @Column(length = 200)
    val description: String? = null,

    @Column(name = "discount_type", nullable = false, length = 20)
    val discountType: String, // PERCENTAGE, FIXED

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    val discountValue: BigDecimal,

    @Column(name = "min_order_value", precision = 10, scale = 2)
    val minOrderValue: BigDecimal? = null,

    @Column(name = "max_discount", precision = 10, scale = 2)
    val maxDiscount: BigDecimal? = null,

    @Column(name = "usage_limit")
    val usageLimit: Int? = null,

    @Column(name = "usage_count", nullable = false)
    val usageCount: Int = 0,

    @Column(name = "usage_per_customer")
    val usagePerCustomer: Int = 1,

    @Column(nullable = false)
    val active: Boolean = true,

    @Column(name = "starts_at")
    val startsAt: LocalDateTime? = null,

    @Column(name = "expires_at")
    val expiresAt: LocalDateTime? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun isValid(): Boolean {
        val now = LocalDateTime.now()
        return active &&
                (usageLimit == null || usageCount < usageLimit) &&
                (startsAt == null || now.isAfter(startsAt)) &&
                (expiresAt == null || now.isBefore(expiresAt))
    }
}
