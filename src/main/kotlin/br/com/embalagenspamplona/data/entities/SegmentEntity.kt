package br.com.embalagenspamplona.data.entities

import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "segments")
data class SegmentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: Long = 0L,

    @Column(nullable = false, length = 100)
    val title: String?="",

    @Column(length = 500)
    val description: String = "",

   /* @Column(name = "image_url")
    val imageUrl: String? = null,*/

    @Column(name = "pill")
    val pill:String="",

    @ManyToMany(mappedBy = "segments")
    val categories: MutableSet<CategoryEntity> = mutableSetOf<CategoryEntity>(),


    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: ZonedDateTime = ZonedDateTime.now()
)
