package br.com.embalagenspamplona.loja.data.entities

import br.com.embalagenspamplona.loja.config.IdPrefix
import br.com.embalagenspamplona.loja.data.enums.ProductCategory
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Entity
@Table(name = "products")
 class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    val id: Long = 0L,

    @Column(nullable = false, length = 150)
    val name: String = "",

    @Column(length = 1000)
    val description: String = "",

    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal = BigDecimal("0.00"),

    /*@Column(name = "promotional_price", precision = 10, scale = 2, nullable = true)
    val promoPrice: BigDecimal? = null,*/
    @OneToOne(mappedBy = "product")
    val promotion: PromotionEntity? = null,

    @Column(length = 50)
    val pack: String = "", // Embalagem: C/100un, C/1000un, etc.

    @Column(name = "quantity")
    val quantity: Int = 0,

    @Column(length = 50)
    val sku: String = "",

    @OneToMany(mappedBy = "product")
    val images: MutableList<ImageEntity> = mutableListOf(),

    @ManyToOne
    @JoinColumn(
        name = "product_category", referencedColumnName = "category_id",
        foreignKey = ForeignKey(name = "fk_product_category_id")
    )
    var categoryEntity: CategoryEntity = CategoryEntity(),


    @OneToOne(mappedBy = "product")
    val orderItem: OrderItemEntity? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now(),

    @Column(name = "updated_at", nullable = true)
    val updatedAt: ZonedDateTime? = null
) {
    fun copy(
        id: Long = this.id,
        name: String = this.name,
        description: String = this.description,
        price: BigDecimal = this.price,
        promotion: PromotionEntity? = this.promotion,
        pack: String = this.pack,
        quantity: Int = this.quantity,
        sku: String = this.sku,
        images: MutableList<ImageEntity> = this.images,
        categoryEntity: CategoryEntity = this.categoryEntity,
        orderItem: OrderItemEntity? = this.orderItem,
        createdAt: ZonedDateTime = this.createdAt,
        updatedAt: ZonedDateTime? = this.updatedAt
    ) = ProductEntity(
        id = id, name = name, description = description, price = price,
        promotion = promotion, pack = pack, quantity = quantity, sku = sku,
        images = images, categoryEntity = categoryEntity, orderItem = orderItem,
        createdAt = createdAt, updatedAt = updatedAt
    )
}
