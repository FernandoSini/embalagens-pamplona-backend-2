package br.com.embalagenspamplona.loja.data.entities

import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "segments")
 class SegmentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "segment_id")
    val id: Long = 0L,

    @Column(nullable = false, length = 100)
    val title: String?="",

    @Column(length = 500)
    val description: String = "",

   /* @Column(name = "image_url")
    val imageUrl: String? = null,*/

    @Column(name = "pill")
    val pill:String="",
    @Column(name ="icon")
    val icon:String="",

    @ManyToMany(mappedBy = "segments")
    val categories: MutableSet<CategoryEntity> = mutableSetOf<CategoryEntity>(),


    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: ZonedDateTime? =null
) {
    fun copy(
        id: Long = this.id,
        title: String? = this.title,
        description: String = this.description,
        pill: String = this.pill,
        icon:String = this.icon,
        categories: MutableSet<CategoryEntity> = this.categories,
        createdAt: ZonedDateTime = this.createdAt,
        updatedAt: ZonedDateTime? = this.updatedAt
    ) = SegmentEntity(
        id = id, title = title, description = description,
        pill = pill,
        categories = categories,
        icon= icon,
        createdAt = createdAt, updatedAt = updatedAt
    )
}
