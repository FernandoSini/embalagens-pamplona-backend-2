package br.com.embalagenspamplona.config.handlers

import br.com.embalagenspamplona.exceptions.CustomExceptionHandler
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus

import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.web.context.request.WebRequest

class CustomAuthenticationFailureHandler: AuthenticationFailureHandler{

    private fun renderFailureStatus(throwable: RuntimeException?, request: WebRequest):Int{
        when(throwable?.cause){
            is UsernameNotFoundException-> return HttpStatus.NOT_FOUND.value()
            is AuthenticationException -> return HttpStatus.UNAUTHORIZED.value()
            else -> return CustomExceptionHandler().handleException(throwable!!,request).statusCode.value()
        }
    }

    override fun onAuthenticationFailure(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        exception: AuthenticationException?
    ) {
        TODO("Not yet implemented")
    }

}