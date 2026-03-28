package br.com.embalagenspamplona.loja.controller

import br.com.embalagenspamplona.loja.adapters.Mapper
import br.com.embalagenspamplona.loja.config.security.cookies.CookieUtils
import br.com.embalagenspamplona.loja.data.dto.ApiResponse
import br.com.embalagenspamplona.loja.data.dto.UserDTO
import br.com.embalagenspamplona.loja.exceptions.BadRequestException
import br.com.embalagenspamplona.loja.exceptions.NotFoundException
import br.com.embalagenspamplona.loja.services.AuthService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.stripe.model.apps.Secret
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.flow.combine
import okhttp3.Response
import org.apache.kafka.shaded.com.google.protobuf.Api
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

@Controller
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val cookieUtils: CookieUtils,
    private val servletResponse: ServletResponse
) {
    @Value("\${secret.key.crypto}")
    private val secretKetCrypto: String = ""
    private val salt= "embalagens-pamplona-auth"

    @PostMapping("/login")
    fun login(@RequestBody request: HashMap<String,Any>): ResponseEntity<ApiResponse<UserDTO>> {
        val combined = Base64.decode(request["encrypted"].toString())

        // 2. Separar IV (12 bytes) do resto (Ciphertext + Tag)
        val iv = combined.sliceArray(0 until 12)
        val ciphertextWithTag = combined.sliceArray(12 until combined.size)

        // 3. Derivar a chave (PBKDF2) exatamente como no Front
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(secretKetCrypto.toCharArray(), salt.toByteArray(), 100000, 256)
        val tmp = factory.generateSecret(spec)
        val secretKey = SecretKeySpec(tmp.encoded, "AES")

        // 4. Configurar Cipher GCM (TagLength 128 bits no JS é o padrão aqui)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
        val decryptedBytes = cipher.doFinal(ciphertextWithTag)
        val authDataString =  String(decryptedBytes)
        val hashMap = ObjectMapper().readValue<HashMap<String, Any>>(authDataString)
        val response = authService.authenticate(hashMap) ?: throw NotFoundException(Exception("Usuário não encontrado"))

        val cookieObj:Cookie = response["cookie"] as Cookie
       // val mapped = ObjectMapper().writeValueAsString(cookieObj.value)
        val encoded = URLEncoder.encode(cookieObj.value, StandardCharsets.UTF_8.name())
        val cookie = ResponseCookie.from(cookieObj.name, encoded)
            .maxAge(cookieObj.maxAge.toLong())
            .secure(cookieObj.secure)
            .path(cookieObj.path)
            .sameSite(cookieObj.attributes["SameSite"])
            .httpOnly(cookieObj.isHttpOnly)
            .build()




        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(ApiResponse.success(data = response["user"]!! as UserDTO))

    }


    @PostMapping("/register", produces = ["application/json","application/xml"])
    fun register(@RequestBody request: HashMap<String,Any>): ResponseEntity<ApiResponse<UserDTO>>{
        val encrypted = Base64.decode(request["encrypted"].toString())

        //separando iv de 12 bytes do ciphertext
        val iv = encrypted.sliceArray(0 until 12)
        val ciperTextWithTag = encrypted.sliceArray(12 until encrypted.size)

        //criando uma chave P8KDF2WithHmacSHA256
        val factory = SecretKeyFactory.getInstance("P8KDF2WithHmacSHA256")
        val spec = PBEKeySpec(secretKetCrypto.toCharArray(), salt.toByteArray(),100000, 256)
        val tmp = factory.generateSecret(spec)
        val secretKey = SecretKeySpec(tmp.encoded, "AES")

        //Configurou cipher para receber o TagLength de 128 que é enviado pelo front
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(128,iv)

        //descriptografamos a key
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
        val decryptedBytes = cipher.doFinal(ciperTextWithTag)
        val authDataString = String(decryptedBytes)
        val hashMap = ObjectMapper().readValue<HashMap<String,Any>>(authDataString)


        val response= authService.register(hashMap)?:throw BadRequestException(Exception("Não foi possivel criar o usuário"))
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

    @GetMapping("/logout/success")
    fun redirect(httpServletResponse: HttpServletResponse, httpServletRequest: HttpServletRequest):ResponseEntity<Any>{
       val cookieResponse = ResponseCookie.from("tokens")
           .value(null).maxAge(0)
           .httpOnly(true)
           .path("/")
           .secure(true)
           .build()


        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).header(HttpHeaders.SET_COOKIE, cookieResponse.toString()).location(URI.create("/")).build()
    }
}