package br.com.embalagenspamplona.loja.loja.config.providers

import br.com.embalagenspamplona.loja.exceptions.NotFoundException
import br.com.embalagenspamplona.loja.services.UserService
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import kotlin.jvm.Throws

@Component
class CustomAuthenticationProvider(private val userService: UserService): AuthenticationProvider{
    @Throws(exceptionClasses = [Exception::class, InternalAuthenticationServiceException::class, BadCredentialsException::class])
    override fun authenticate(authentication: Authentication?): Authentication? {
        try {
            if (authentication != null) {
                val username  =  authentication.principal as? String?: throw InternalAuthenticationServiceException("Inválid credentials")
                val password = authentication.credentials as? String?: throw InternalAuthenticationServiceException("Inválid credentials")
                val userDetails = userService.loadUserByUsername(username)?:throw InternalAuthenticationServiceException("Não conseguimos autenticar o usuário. Por favor, tente novamente mais tarde!")
                if(!BCryptPasswordEncoder().matches(password, userDetails.password) ){
                    throw BadCredentialsException("Senha inválida! Tente novamente!")
                }
                //lembrar que o authentication.authorities libera quando esta junto dos outros dois do principal e credentials
                return UsernamePasswordAuthenticationToken(userDetails.username, userDetails.password, userDetails.authorities)
            } else {
                throw InternalAuthenticationServiceException("Internal Authentication Provider is not authenticated")
            }
        }catch (e: InternalAuthenticationServiceException){
          //  throw NotFoundException(e)
            throw e
        }catch(e:Exception){
            throw e
        }

    }


    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }

}