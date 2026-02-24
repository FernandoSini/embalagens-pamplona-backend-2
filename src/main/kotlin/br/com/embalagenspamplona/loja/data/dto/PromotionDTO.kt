package br.com.embalagenspamplona.loja.data.dto

import br.com.embalagenspamplona.loja.data.entities.ProductEntity
import jakarta.persistence.Column
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZonedDateTime

data class PromotionDTO(
    val id: Long? = null,
    val code: String?=null,
    val description: String? = null,
    val price: BigDecimal = BigDecimal.ZERO,
    val startDate: ZonedDateTime? = ZonedDateTime.now(),
    val endDate: ZonedDateTime? = ZonedDateTime.now(),
    val product: ProductDTO?=null,

    )
