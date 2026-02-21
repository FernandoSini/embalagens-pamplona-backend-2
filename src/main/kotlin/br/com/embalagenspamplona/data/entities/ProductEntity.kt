package br.com.embalagenspamplona.data.entities

import br.com.embalagenspamplona.config.IdPrefix
import br.com.embalagenspamplona.data.enums.ProductCategory
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Entity
@Table(name = "products")
data class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false, length = 150)
    val name: String = "",

    @Column(length = 1000)
    val description: String? = null,

    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal = BigDecimal("0.00"),

    /*@Column(name = "promotional_price", precision = 10, scale = 2, nullable = true)
    val promoPrice: BigDecimal? = null,*/
    @OneToOne(mappedBy = "product")
    val promotion: PromotionEntity? = null,

    @Column(length = 50)
    val pack: String? = null, // Embalagem: C/100un, C/1000un, etc.

    @Column(name="quantity")
    val quantity: Int = 0,

    @Column(length = 50)
    val sku: String? = null,

    @OneToMany(mappedBy = "product")
    val images: MutableList<ImageEntity> = mutableListOf(),

    @ManyToOne
    @JoinColumn(
        name = "category_id", referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_product_category_id")
    )
    var categoryEntity: CategoryEntity = CategoryEntity(),


    @OneToOne(mappedBy = "product")
    val orderItem: OrderItemEntity,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now(),

    @Column(name = "updated_at", nullable = true)
    val updatedAt: ZonedDateTime? = null
)
