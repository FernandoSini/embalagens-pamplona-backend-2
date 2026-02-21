package br.com.embalagenspamplona.data.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
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
data class ImageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name="url")
    val url: String = "",

    @Temporal(TemporalType.TIMESTAMP)
    val createdAt: ZonedDateTime = ZonedDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "product_id")
    val product: ProductEntity,

    )