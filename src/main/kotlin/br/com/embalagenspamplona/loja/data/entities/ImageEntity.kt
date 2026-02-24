package br.com.embalagenspamplona.loja.data.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import java.time.ZonedDateTime

@Entity
@Table(name = "image")
 class ImageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    val id: Long = 0L,

    @Column(name = "url")
    val url: String = "",


    val createdAt: ZonedDateTime = ZonedDateTime.now(),

    @ManyToOne
    @JoinColumn(
        name = "image_product_id",
        referencedColumnName = "product_id",
        foreignKey = ForeignKey(name = "fk_image_product_id")
    )
    val product: ProductEntity? = null,

    ) {
    fun copy(
        id: Long = this.id,
        url: String = this.url,
        createdAt: ZonedDateTime = this.createdAt,
        product: ProductEntity? = this.product
    ) = ImageEntity(
        id = id, url = url, createdAt = createdAt, product = product
    )
}