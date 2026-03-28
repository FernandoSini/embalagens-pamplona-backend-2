package br.com.embalagenspamplona.loja.config.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit


@Component
class CustomLogoutSuccessHandler: LogoutSuccessHandler {
    override fun onLogoutSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        response?.status = HttpStatus.PERMANENT_REDIRECT.value()
        response?.contentType = MediaType.APPLICATION_JSON_VALUE
        response?.characterEncoding = "UTF-8"
        response?.writer?.write(
            ObjectMapper().writeValueAsString(
                mapOf(
                    "status" to HttpStatus.PERMANENT_REDIRECT.name.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase() else it.toString()
                    },
                    "statusCode" to HttpStatus.PERMANENT_REDIRECT.value(),
                    "message" to "Usuário deslogado!",
                    "timestamp" to ZonedDateTime.now().toLocalDateTime().truncatedTo(ChronoUnit.SECONDS).toString()

                )
            )
        )
    }
}