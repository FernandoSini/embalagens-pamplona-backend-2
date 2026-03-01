package br.com.embalagenspamplona.loja.data.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.ZonedDateTime

data class RoleDTO(
    @JsonIgnore val id: Long? = null,
    val authority: String? = null,
    @JsonIgnore val description: String? = null,
    @JsonIgnore val updatedAt: ZonedDateTime? = null,
    @JsonIgnore val createdAt: ZonedDateTime? = null
)