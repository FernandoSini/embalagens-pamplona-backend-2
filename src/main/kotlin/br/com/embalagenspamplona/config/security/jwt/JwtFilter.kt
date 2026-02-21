package br.com.embalagenspamplona.config.security.jwt

import br.com.embalagenspamplona.exceptions.NotFoundException
import br.com.embalagenspamplona.services.TokenService
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.MalformedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.GenericFilter
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Service
import org.springframework.web.filter.GenericFilterBean

@Service
class JwtFilter(private val tokenService: TokenService) : GenericFilterBean() {
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
            }

        } catch (e: JwtException) {
            (response as HttpServletResponse).status = HttpStatus.BAD_REQUEST.value()
            (response as HttpServletResponse).writer.print("JwtException" + e.message.toString())
        } catch (ex: MalformedJwtException) {
            (response as HttpServletResponse).status = HttpStatus.BAD_REQUEST.value()
            (response as HttpServletResponse).writer.printf("Malformed token: " + ex.message)
        } catch (ex: UsernameNotFoundException) {
            (response as HttpServletResponse).status = HttpStatus.NOT_FOUND.value()
            (response as HttpServletResponse).writer.printf("Not found user to chain token: " + ex.message)
        }
    }


    private fun updateContext(authFound: Authentication, request: HttpServletRequest?) {
        val authToken = UsernamePasswordAuthenticationToken(authFound.principal, null, authFound.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authToken
    }
}