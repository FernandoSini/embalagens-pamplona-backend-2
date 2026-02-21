package br.com.embalagenspamplona.config.security.cookies

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class CookieUtils(
    @param:Value("\${security.cookies}") private val secureCookie: Boolean,
    @param:Value("\${security.cookiesMaxAge}") private val ageMax: Int
) {
    //o mais seguro é o strict e o Lax é o padrao moderno
    fun addCookie(key:String, value:String): Cookie{
        val cookie= Cookie(key, value).apply {
            path="/"
            isHttpOnly = true
            secure = secureCookie
            //maxAge= maxAge PADRAO DO COOKIE
            maxAge= ageMax
            setAttribute("SameSite", "Strict")
        }
        return cookie
    }

    fun deleteCookie(servletResponse: HttpServletResponse, key: String){
        val cookie = Cookie(key,null).apply {
            path="/"
            isHttpOnly = true
            secure = secureCookie
            //maxAge= maxAge PADRAO DO COOKIE
            maxAge=ageMax

            setAttribute("SameSite", "Lax")
        }
        return servletResponse.addCookie(cookie)
    }

    /* Opção B(alternativa): Adicionar um Cookie Simples
    Se você insistir em manter tudo em cookies, pode adicionar um terceiro cookie,
    mas sem o flag httpOnly para que o javascript possa lê-lo  e cronometrar o refresh
     */
    fun addExpiresInCookie(servletResponse: HttpServletResponse, maxAgeInSeconds:Long){

        val cookie = Cookie("exipresIn", null).apply {
            path="/"
            isHttpOnly = false
            secure = secureCookie
            maxAge= maxAgeInSeconds.toInt()

        }
        servletResponse.addCookie(cookie)
    }

}