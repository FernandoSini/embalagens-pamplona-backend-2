package br.com.embalagenspamplona.loja.data.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import java.time.ZonedDateTime

@Entity
@Table(name = "categories")
class CategoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    var id: Long? = null,

    @Column(name = "title")
    var title: String = "",

    @Column(name = "icon")
    var icon: String = "",


    @Column(name = "created_at")
    var createdAt: ZonedDateTime = ZonedDateTime.now(),


    @Column(name = "updated_at")
    var updatedAt: ZonedDateTime = ZonedDateTime.now(),

    @ManyToMany
    @JoinTable(
        name = "segments_categories",
        joinColumns = [JoinColumn(
            name = "id_category",
            referencedColumnName = "category_id",
            foreignKey = ForeignKey(name = "fk_category_id")
        )],
        inverseJoinColumns = [
            JoinColumn(
                name = "id_segment",
                referencedColumnName = "segment_id",
                foreignKey = ForeignKey(name = "fk_segment_id")
            )
        ]
    )
    val segments: MutableSet<SegmentEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "categoryEntity")
    val products: MutableList<ProductEntity> = mutableListOf<ProductEntity>(),

    ) {
    fun copy(
        id: Long? = this.id,
        title: String = this.title,
        icon: String = this.icon,
        createdAt: ZonedDateTime = this.createdAt,
        updatedAt: ZonedDateTime = this.updatedAt,
        segments: MutableSet<SegmentEntity> = this.segments,
        products: MutableList<ProductEntity> = this.products
    ) = CategoryEntity(
        id = id, title = title, icon = icon,
        createdAt = createdAt, updatedAt = updatedAt,
        segments = segments, products = products
    )
}