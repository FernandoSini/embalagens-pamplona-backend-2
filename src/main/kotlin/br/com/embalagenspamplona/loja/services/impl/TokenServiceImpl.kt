package br.com.embalagenspamplona.loja.services.impl

import br.com.embalagenspamplona.loja.exceptions.InternalServerException
import br.com.embalagenspamplona.loja.services.TokenService
import br.com.embalagenspamplona.loja.services.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.net.URLDecoder
import java.util.Arrays

import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

@Service
class TokenServiceImpl : TokenService {

    @Value("\${jwt.key}")
    private val secretKey: String = ""

    @Value("\${jwt.refresh-token-expiration}")
    private val refreshTokenExpiration: Long = 0L

    @Value("\${jwt.access-token-expiration}")
    private val accessTokenExpiration: Long = 0L

    private lateinit var signingKey: SecretKeySpec
    private lateinit var redisTemplate: RedisTemplate<String, Any>


    @Qualifier("userDetailsService")
    @Autowired
    private lateinit var userServiceImpl: UserDetailsService

    @OptIn(ExperimentalEncodingApi::class)
    @PostConstruct
    fun initTokenService() {
     /*   val keyBytes = try {
            Base64.decode(secretKey)
        } catch (e: IllegalArgumentException) {
            secretKey.toByteArray(Charsets.UTF_8)
        }*/
        val bytes = Base64.decode(secretKey)
        signingKey = SecretKeySpec(bytes,0,bytes.size, "HmacSHA256")
    }

    override fun generateToken(
        user: UserDetails,
        expiration: Date,
        additionalClaims: HashMap<String, Any>
    ): String {
        val claims = Jwts.claims().subject(user.username).id(UUID.randomUUID().toString())
        claims.add("roles", user.authorities).add(additionalClaims)

        val now = Date()
        val expiration = Date(now.time + expiration.time)
        return Jwts.builder().claims(claims.build())
            .issuedAt(now)
            .expiration(expiration).signWith(signingKey).compact()
    }

    override fun isTokenValid(
        token: String,
        userDetails: UserDetails?
    ): Boolean {
        try {
            val claims: Jws<Claims> = Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token)
            if (claims.payload.expiration.before(Date())) {
                return false
            }
            return true

        } catch (e: JwtException) {
            throw JwtException("Token de acesso inválido Jwt: ${e.message}")
        }
    }

    override fun getAuthentication(
        token: String,
        userDetails: UserDetails?
    ): Authentication? {
        try {
            val subject = Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).payload.subject
            val userInfo = userServiceImpl.loadUserByUsername(subject)
            if (userInfo != null) {
                return UsernamePasswordAuthenticationToken(userInfo, "", userInfo.authorities)
            } else {
                throw UsernameNotFoundException("Usuário não encontrado para fazer autenticacao")
            }
        } catch (e: Exception) {
            throw Exception("Token jwt inválido! error:${e}");
        }

    }



    override fun resolveToken(request: HttpServletRequest): String? {
        val authCookie = request.cookies?.find { it.name == "tokens" }
        val accessToken = authCookie?.let { cookie ->
            try {
                val decodedValue = URLDecoder.decode(cookie.value, Charsets.UTF_8)
                val tokens: HashMap<String, Any> = ObjectMapper().readValue(decodedValue)
                return tokens["accessToken"].toString()

            } catch (e: Exception) {
                throw InternalServerException(Exception("Erro ao processar token de acesso! Error: ${e.message}"))

            }
        }
        return accessToken
    }

    override fun storeTokenRedis(refreshToken: String) {
        val tokenData = Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(refreshToken).payload
        redisTemplate.opsForValue().set(
            "refreshToken" + tokenData.id,
            tokenData.subject,
            tokenData.expiration.time.seconds.toLong(DurationUnit.SECONDS), TimeUnit.SECONDS
        )
    }

    override fun deleteInvalidToken(refreshToken: String) {
        val claims = Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(refreshToken)
        redisTemplate.delete("refreshToken" + claims.payload.id)
    }

    override fun getUserInfo(token: String): UserDetails? {
        val extractedUser = Jwts.parser().verifyWith(signingKey).build()
            .parseSignedClaims(token).payload.subject
        return userServiceImpl.loadUserByUsername(extractedUser)
    }
}