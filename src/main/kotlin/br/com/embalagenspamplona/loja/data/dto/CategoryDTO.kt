package br.com.embalagenspamplona.loja.data.dto

import br.com.embalagenspamplona.loja.data.entities.ProductEntity
import br.com.embalagenspamplona.loja.data.entities.SegmentEntity
import jakarta.persistence.Column
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import java.time.ZonedDateTime

data class CategoryDTO(

    var id: Long? = null,
    var title: String = "",
    var icon: String = "",
    var createdAt: ZonedDateTime = ZonedDateTime.now(),
    var updatedAt: ZonedDateTime = ZonedDateTime.now(),
    val segments: MutableSet<SegmentEntity> = mutableSetOf(),
    val products: MutableList<ProductEntity> = mutableListOf<ProductEntity>(),
)