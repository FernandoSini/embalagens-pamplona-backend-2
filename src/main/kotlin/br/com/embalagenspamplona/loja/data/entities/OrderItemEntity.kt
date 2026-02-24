package br.com.embalagenspamplona.loja.data.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Entity
@Table(name = "order_items")
class OrderItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "order_items_order",
        referencedColumnName = "order_id",
        foreignKey = ForeignKey(name = "fk_order_item_order_id")
    )
    val order: OrderEntity? = null,

    @OneToOne
    @JoinColumn(
        name = "product_order",
        referencedColumnName = "product_id",
        foreignKey = ForeignKey(name = "fk_product_id_order")
    )
    val product: ProductEntity? = null, // ID do produto (ex: "pp-101")

    @Column(nullable = false, length = 150)
    val name: String = "", // Nome do produto

    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal = BigDecimal.ZERO, // Preço unitário

    @Column(nullable = false)
    val quantitySelected: Long = 0, // Quantidade

    @Column(length = 50)
    val pack: String? = null, // Embalagem (ex: "C/100", "C/1000")

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now()


) {
    fun copy(
        id: Long? = this.id,
        order: OrderEntity? = this.order,
        product: ProductEntity? = this.product,
        name: String = this.name,
        price: BigDecimal = this.price,
        quantitySelected: Long = this.quantitySelected,
        pack: String? = this.pack,
        createdAt: ZonedDateTime = this.createdAt
    ) = OrderItemEntity(
        id = id, order = order, product = product,
        name = name, price = price, quantitySelected = quantitySelected,
        pack = pack, createdAt = createdAt
    )
}
