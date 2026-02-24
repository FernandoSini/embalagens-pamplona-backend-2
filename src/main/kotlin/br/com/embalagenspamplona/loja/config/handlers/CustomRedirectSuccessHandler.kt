package br.com.embalagenspamplona.loja.config.handlers

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

class CustomRedirectSuccessHandler : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        val roles = authentication?.authorities?.map { it.authority }
        if (roles != null) {
            when {
                "Admin" in roles -> response?.sendRedirect("/admin")
                "customer" in roles -> response?.sendRedirect("/")
            }

        }else{

            response?.encodeRedirectURL("/")
        }
    }

}