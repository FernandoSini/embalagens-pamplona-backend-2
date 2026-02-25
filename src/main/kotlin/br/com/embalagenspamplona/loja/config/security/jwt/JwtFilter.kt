package br.com.embalagenspamplona.loja.loja.config.security.jwt

import br.com.embalagenspamplona.loja.data.dto.ApiResponse
import br.com.embalagenspamplona.loja.exceptions.BadRequestException
import br.com.embalagenspamplona.loja.exceptions.ForbiddenException
import br.com.embalagenspamplona.loja.exceptions.InternalServerException
import br.com.embalagenspamplona.loja.exceptions.NotFoundException
import br.com.embalagenspamplona.loja.services.TokenService
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.MalformedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.GenericFilter
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.kafka.shaded.com.google.protobuf.Api
import org.h2.command.Token
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Service
import org.springframework.web.filter.GenericFilterBean
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.jvm.Throws

@Service
class JwtFilter(private val tokenService: TokenService) : GenericFilterBean() {


    @Throws(exceptionClasses = [Exception::class, JwtException::class, ForbiddenException::class, BadRequestException::class, NotFoundException::class])
    override fun doFilter(
        request: ServletRequest?,
        response: ServletResponse?,
        filter: FilterChain?
    ) {
        val resolvedToken = tokenService.resolveToken(request as HttpServletRequest)
        try {
            if (resolvedToken != null && tokenService.isTokenValid(resolvedToken, null)) {
                val authentication = tokenService.getAuthentication(resolvedToken, null)
                if (authentication != null) {
                    updateContext(authentication, request)
                } else {
                    throw NotFoundException(Exception("User not found on auth!"))
                }
                filter?.doFilter(request, response)
            } else {
                throw ForbiddenException(Exception("Acesso não autorizado! Faça o login para poder acessar esta área!"))
            }


        } catch (e: JwtException) {
            (response as HttpServletResponse).status = HttpStatus.BAD_REQUEST.value()
            (response).writer.print("JwtException" + e.message.toString())
            throw InternalServerException(Exception("Jwt exception: ${response.status}, ${response.writer}"))
        } catch (ex: MalformedJwtException) {
            (response as HttpServletResponse).status = HttpStatus.BAD_REQUEST.value()
            response.writer.printf("Malformed token: " + ex.message)
            throw BadRequestException(Exception("Token mal formado! Erro: ${response.status},${response.writer} "))
        } catch (ex: UsernameNotFoundException) {
            (response as HttpServletResponse).status = HttpStatus.NOT_FOUND.value()
            response.writer.printf("Not found user to chain token: " + ex.message)
            throw NotFoundException(Exception("Usuário não encontrado!"))
        } catch (e: Exception) {
            (response as HttpServletResponse).status = HttpStatus.INTERNAL_SERVER_ERROR.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = "UTF-8"
            response.writer?.write(
                ObjectMapper().writeValueAsString(
                    mapOf(
                        "status" to HttpStatus.UNAUTHORIZED.name.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase() else it.toString()
                        },
                        "statusCode" to HttpStatus.UNAUTHORIZED.value(),
                        "message" to "Erro Interno: ${e.message}",
                        "timestamp" to ZonedDateTime.now().toLocalDateTime().truncatedTo(ChronoUnit.SECONDS).toString()

                    )
                )

            )
            // response.writer.printf("Erro interno: " + e.message)
            // throw Exception("Erro: Status: ${response.status}, message:${response}")


        }


    }


    private fun updateContext(authFound: Authentication, request: HttpServletRequest?) {
        val authToken = UsernamePasswordAuthenticationToken(authFound.principal, null, authFound.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authToken
    }
}