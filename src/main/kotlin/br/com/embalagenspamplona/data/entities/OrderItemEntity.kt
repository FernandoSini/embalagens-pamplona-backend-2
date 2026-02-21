package br.com.embalagenspamplona.data.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Entity
@Table(name = "order_items")
data class OrderItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    val order: OrderEntity,

    @OneToOne(mappedBy = "orderItem")
    val product: ProductEntity , // ID do produto (ex: "pp-101")

    @Column(nullable = false, length = 150)
    val name: String="", // Nome do produto

    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal = BigDecimal.ZERO, // Preço unitário

    @Column(nullable = false)
    val quantitySelected: Long = 0, // Quantidade

    @Column(length = 50)
    val pack: String? = null, // Embalagem (ex: "C/100", "C/1000")

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now()


)
