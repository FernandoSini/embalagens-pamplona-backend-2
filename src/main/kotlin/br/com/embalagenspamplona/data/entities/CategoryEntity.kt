package br.com.embalagenspamplona.data.entities

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
    var id: Long? = null,

    @Column(name = "title")
    var title: String = "",

    @Column(name = "icon")
    var icon: String = "",

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    var createdAt: ZonedDateTime = ZonedDateTime.now(),

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    var updatedAt: ZonedDateTime = ZonedDateTime.now(),

    @ManyToMany
    @JoinTable(
        name = "segments_categories",
        joinColumns = [JoinColumn(
            name = "category_id",
            referencedColumnName = "id",
            foreignKey = ForeignKey(name = "fk_category_id")
        )],
        inverseJoinColumns = [
            JoinColumn(
                name = "segment_id",
                referencedColumnName = "id",
                foreignKey = ForeignKey(name = "fk_segment_id")
            )
        ]
    )
    val segments: MutableSet<SegmentEntity> = mutableSetOf(),
    @OneToMany(mappedBy = "category")
    val products: MutableList<ProductEntity> = mutableListOf<ProductEntity>(),

    )