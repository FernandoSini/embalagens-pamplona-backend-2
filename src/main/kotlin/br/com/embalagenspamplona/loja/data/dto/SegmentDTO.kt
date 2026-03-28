package br.com.embalagenspamplona.loja.data.dto

import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

data class SegmentDTO(
    val id: Long = 0L,
    val title: String ="",
    val description: String = "",
    val pill:String ="",
    val icon:String="",
    val categories: MutableSet<CategoryDTO> = mutableSetOf(),
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    val updatedAt: ZonedDateTime? = null
)

