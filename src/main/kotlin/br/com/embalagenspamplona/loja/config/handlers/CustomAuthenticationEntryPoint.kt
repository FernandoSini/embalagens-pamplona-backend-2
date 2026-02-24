package br.com.embalagenspamplona.loja.config.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class CustomAuthenticationEntryPoint: AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        exception: AuthenticationException?
    ) {
        response?.contentType= MediaType.APPLICATION_JSON_VALUE
        response?.status = HttpStatus.UNAUTHORIZED.value()
        response?.writer?.write(
            ObjectMapper().writeValueAsString(
                "error" to mapOf(
                    "status" to HttpStatus.UNAUTHORIZED.name.replaceFirstChar {
                        if(it.isLowerCase()) it.titlecase() else it.toString()
                    },
                    "statusCode" to HttpStatus.UNAUTHORIZED.value(),
                    "message" to "Authentication is required",
                    "timestamp" to ZonedDateTime.now().toLocalDateTime().truncatedTo(ChronoUnit.SECONDS).toString()

                )
            )
        )
    }
}