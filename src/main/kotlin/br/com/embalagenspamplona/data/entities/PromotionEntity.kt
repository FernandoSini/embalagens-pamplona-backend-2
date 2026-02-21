package br.com.embalagenspamplona.data.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "promotions")
data class PromotionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 10)
    val code: String?=null,

    @Column(length = 200)
    val description: String? = null,

    @OneToOne
    val product: ProductEntity?=null,

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    val price: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    val active: Boolean = true,

    @Column(name = "starts_at")
    val startsAt: ZonedDateTime? = null,

    @Column(name = "expires_at")
    val expiresAt: ZonedDateTime? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: ZonedDateTime = ZonedDateTime.now(),

    /* @Column(name = "discount_type", nullable = false, length = 20)
    val discountType: String, // PERCENTAGE, FIXED*/

    /* @Column(name = "usage_limit")
     val usageLimit: Int? = null,

     @Column(name = "usage_count", nullable = false)
     val usageCount: Int = 0,

     @Column(name = "usage_per_customer")
     val usagePerCustomer: Int = 1,*/
) {
    /*  fun isValid(): Boolean {
          val now = LocalDateTime.now()
          return active &&
                  (usageLimit == null || usageCount < usageLimit) &&
                  (startsAt == null || now.isAfter(startsAt)) &&
                  (expiresAt == null || now.isBefore(expiresAt))
      }*/
}
