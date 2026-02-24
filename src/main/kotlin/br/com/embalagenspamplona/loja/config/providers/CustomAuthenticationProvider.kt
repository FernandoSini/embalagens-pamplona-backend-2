package br.com.embalagenspamplona.loja.loja.config.providers

import br.com.embalagenspamplona.loja.exceptions.NotFoundException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationProvider: AuthenticationProvider{
    override fun authenticate(authentication: Authentication?): Authentication? {
        try {
            if (authentication != null) {
                return UsernamePasswordAuthenticationToken(authentication.principal, authentication.credentials)
            } else {
                throw InternalAuthenticationServiceException("Internal Authentication Provider is not authenticated")
            }
        }catch (e: InternalAuthenticationServiceException){
            throw NotFoundException(e)
        }

    }


    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }

}