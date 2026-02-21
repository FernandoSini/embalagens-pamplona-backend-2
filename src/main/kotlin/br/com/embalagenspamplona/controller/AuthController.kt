package br.com.embalagenspamplona.controller

import br.com.embalagenspamplona.adapters.Mapper
import br.com.embalagenspamplona.data.dto.ApiResponse
import br.com.embalagenspamplona.data.dto.UserDTO
import br.com.embalagenspamplona.exceptions.BadRequestException
import br.com.embalagenspamplona.exceptions.NotFoundException
import br.com.embalagenspamplona.services.AuthService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import okhttp3.Cookie
import org.apache.kafka.shaded.com.google.protobuf.Api
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/api/v1/auth")
class AuthController(private val authService: AuthService) {


    @PostMapping("/login")
    fun login(@RequestBody request: HashMap<String, Any>): ResponseEntity<ApiResponse<UserDTO>> {
        val response = authService.authenticate(request) ?: throw NotFoundException(Exception("Usuário não encontrado"))

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, response["cookie"].toString())
            .body(ApiResponse.success(data = response["user"]!! as UserDTO))

    }


    @PostMapping("/register")
    fun register(@RequestBody request: HashMap<String,Any>): ResponseEntity<ApiResponse<UserDTO>>{
        val response= authService.register(request)?:throw BadRequestException(Exception("Não foi possivel criar o usuário"))
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,response["cookie"].toString()).body(ApiResponse.success(response["user"]!! as UserDTO))
    }

    @PostMapping
    fun refreshToken(
        @CookieValue("tokens") tokens: String,
        servletRequest: HttpServletRequest,
        servletResponse: HttpServletResponse
    ): ResponseEntity<ApiResponse<String>> {
        try {
            val tokens: HashMap<String, Any> = ObjectMapper().readValue(tokens)
            val response = authService.refreshTokens(tokens, servletResponse)
                ?: throw BadRequestException(Exception("Houve um erro ao atualizar os tokens!"))
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, response["cookie"].toString())
                .body(ApiResponse.success(data = "Token atualizado com sucesso"))
        } catch (e: Exception) {
            throw Exception(e.message)

        }

    }
}