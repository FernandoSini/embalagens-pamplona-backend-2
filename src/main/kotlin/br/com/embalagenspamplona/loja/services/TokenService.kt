package br.com.embalagenspamplona.loja.services

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import java.util.Date

interface TokenService {
    fun generateToken(user: UserDetails, expiration: Date, additionalClaims: HashMap<String, Any> = hashMapOf()): String
    fun isTokenValid(token:String, userDetails: UserDetails?): Boolean
    fun getAuthentication(token:String, userDetails: UserDetails?): Authentication?
    fun resolveToken(request: HttpServletRequest):String?
    fun storeTokenRedis(refreshToken:String)
    fun deleteInvalidToken(refreshToken: String)
    fun getUserInfo(token:String):UserDetails?
}