package br.com.embalagenspamplona.config.security.jwt

import jakarta.validation.constraints.NotBlank
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(value = "jwt", ignoreUnknownFields = false)
data class JwtProperties(
    val key:String,
    @param:NotBlank
    @param:Value("\${jwt.access-token-expiration}") val accessTokenExpiration: Long,
    @param:NotBlank
    @param:Value("\${jwt.refresh-token-expiration}") val refreshTokenExpiration:Long,

)