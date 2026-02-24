package br.com.embalagenspamplona.loja.data.dto

import java.time.ZonedDateTime

data class ImageDTO(
    val id: Long = 0L,
    val url: String = "",
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    val productDTO: ProductDTO = ProductDTO(),

)
