package br.com.embalagenspamplona.loja.services

import br.com.embalagenspamplona.loja.data.dto.UserDTO
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse

interface AuthService {
    fun authenticate(request: HashMap<String, Any>, loginAttempts: Int=0): HashMap<String,Any>?
    fun refreshTokens(request:HashMap<String,Any>, servletResponse: HttpServletResponse): HashMap<String,Any>?
    fun register(request: HashMap<String, Any>): HashMap<String, Any>?
    fun verifyCode(request:HashMap<String,Any>): Boolean
    fun resendCode(userId:String)
}