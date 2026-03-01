package br.com.embalagenspamplona.loja.services.impl

import br.com.embalagenspamplona.loja.config.security.cookies.CookieUtils
import br.com.embalagenspamplona.loja.config.security.jwt.JwtProperties
import br.com.embalagenspamplona.loja.data.dto.UserDTO
import br.com.embalagenspamplona.loja.data.entities.RoleEntity
import br.com.embalagenspamplona.loja.data.entities.UserEntity
import br.com.embalagenspamplona.loja.exceptions.BadRequestException
import br.com.embalagenspamplona.loja.exceptions.ForbiddenException
import br.com.embalagenspamplona.loja.exceptions.InternalServerException
import br.com.embalagenspamplona.loja.exceptions.LimitExceededException
import br.com.embalagenspamplona.loja.exceptions.NotFoundException
import br.com.embalagenspamplona.loja.exceptions.TooManyRequestsException
import br.com.embalagenspamplona.loja.services.AuthService
import br.com.embalagenspamplona.loja.services.RoleService
import br.com.embalagenspamplona.loja.services.TokenService
import br.com.embalagenspamplona.loja.services.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.Date
import java.util.concurrent.TimeUnit

@Service
class AuthServiceImpl(
    private val authManager: AuthenticationManager,
    private val jwtProperties: JwtProperties,
    private val userService: UserService,
    private val tokenService: TokenService,
    private val cookieUtils: CookieUtils,
    private val roleService: RoleService,
    private val redisTemplate: RedisTemplate<String, Any>
) : AuthService {

    companion object {
        private const val MAX_LOGIN_ATTEMPTS = 3
        private const val LOGIN_LOCKOUT_MINUTES = 15L
        private const val LOGIN_ATTEMPTS_KEY_PREFIX = "loginAttempts:"
    }

    override fun authenticate(
        request: HashMap<String, Any>,
        loginAttempts: Int
    ): HashMap<String, Any>? {

        val attemptsKey = "$LOGIN_ATTEMPTS_KEY_PREFIX${request["username"].toString()}"
        val currentAttempts = when (val current = redisTemplate.opsForValue().get(attemptsKey)) {
            is Number -> current.toInt()
            is String -> current.toIntOrNull() ?: 0
            else -> 0
        }

        try {
            if (currentAttempts >= MAX_LOGIN_ATTEMPTS) {
                throw BadCredentialsException("Limite excedido.")
            }

            val authentication = authManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    request["username"].toString(),
                    request["password"].toString(),
                    )
            )

            if (authentication.isAuthenticated) {
                redisTemplate.delete(attemptsKey)
                val userDetailsCredentials = userService.loadUserByUsername(request["username"].toString())
                    ?: throw NotFoundException(Exception("Usuário não existe"))

                val accessToken = createAccessToken(userDetailsCredentials)
                val refreshToken = createRefreshToken(userDetailsCredentials)

                val userDTO = userService.findByEmail(request["username"].toString())
                if (userDTO == null) {
                    throw InternalServerException(Exception("Conseguimos encontrar as credencias mas tivemos um erro ao retornar usas informacoes"))
                }
                val tokens = HashMap<String, Any>()
                tokens["accessToken"] = accessToken
                tokens["refreshToken"] = refreshToken
                tokenService.storeTokenRedis(refreshToken)
                val cookie = cookieUtils.addCookie("tokens", ObjectMapper().writeValueAsString(tokens))
                val response = HashMap<String, Any>()
                response["cookie"] = cookie
                response["user"] = userDTO
                return response
            } else {
                throw BadCredentialsException("Não foi possível autenticar, porfavor, tente novamente!")
            }
        } catch (e: BadCredentialsException) {
            val newAttempts = redisTemplate.opsForValue().increment(attemptsKey)?.toInt() ?: 1
            redisTemplate.expire(attemptsKey, LOGIN_LOCKOUT_MINUTES, TimeUnit.MINUTES)
            val remainingAttempts = MAX_LOGIN_ATTEMPTS - newAttempts
            if (remainingAttempts > 0) {
                throw BadCredentialsException("Credenciais inválidas! ${remainingAttempts} tentativa(s) restante(s).")
            } else {
                throw TooManyRequestsException(Exception("Conta temporariamente bloqueada."))
            }

        } catch (e: Exception) {
            throw e
        }
    }


    override fun refreshTokens(
        request: HashMap<String, Any>,
        httpServletResponse: HttpServletResponse
    ): HashMap<String, Any>? {
        val userInfo = tokenService.getUserInfo(request["refreshToken"].toString())
        if (userInfo == null || !tokenService.isTokenValid(request["refreshToken"].toString(), userInfo)) {
            tokenService.deleteInvalidToken(request["refreshToken"].toString())
            cookieUtils.deleteCookie(httpServletResponse, "tokens")
            /* val result = HashMap<String, Any>()
             result["cookie"] = cookieUtils.addCookie("", "")
             return result*/
            throw ForbiddenException(Exception("Refresh token inválido!"))

        } else {
            val newAccessToken = createAccessToken(userInfo)
            val newRefreshToken = createRefreshToken(userInfo)
            tokenService.deleteInvalidToken(request["refreshToken"].toString())
            cookieUtils.deleteCookie(httpServletResponse, "tokens")
            val newTokens = HashMap<String, Any>()
            newTokens["accessToken"] = newAccessToken
            newTokens["refreshToken"] = newRefreshToken
            tokenService.storeTokenRedis(newTokens["refreshToken"].toString())
            val cookie = cookieUtils.addCookie("tokens", newTokens.toString())
            val result = HashMap<String, Any>()
            result["cookie"] = cookie
            return result

        }
    }

    override fun register(request: HashMap<String, Any>): HashMap<String, Any>? {
        try {
            val userEntity = UserEntity(
                name = request["name"].toString(),
                lastName = request["last_name"].toString(),
                password = BCryptPasswordEncoder().encode(request["password"].toString()),
                gender = request["gender"].toString(),
                phone = request["phone"].toString(),
                email = request["email"].toString(),
                birthday = request["birthday"].toString(),
                cpfCnpj = request["cpfcnpj"].toString()
            );
            val role = roleService.findRoleById(1L)
            userEntity.role = role

            val createdEntity = userService.createUser(userEntity)

            if (createdEntity != null) {

                val authentication = authManager.authenticate(
                    UsernamePasswordAuthenticationToken(
                        request["email"].toString(),
                        request["password"].toString(),

                        )
                )
                if (authentication.isAuthenticated) {
                    val authData = userService.loadUserByUsername(request["email"].toString())
                        ?: throw InternalServerException(
                            Exception(
                                "Usuário foi criado mas não foi possível autenticar agora! Por favor, tente novamente!"
                            )
                        )
                    val accessToken = createAccessToken(authData)
                    val refreshToken = createRefreshToken(authData)
                    val tokens = hashMapOf<String, Any>()
                    tokens["accessToken"] = accessToken
                    tokens["refreshToken"] = refreshToken
                    tokenService.storeTokenRedis(refreshToken)
                    val cookie = cookieUtils.addCookie("tokens", ObjectMapper().writeValueAsString(tokens))
                    val response = hashMapOf<String, Any>()
                    response["cookie"] = cookie
                    response["user"] = createdEntity
                    return response
                } else {
                    throw InternalServerException(Exception("Erro ao concluir cadastro do usuário! Não foi possível fazer login via cadastro!"))
                }
            } else {
                throw BadRequestException(Exception("O Usuário ja existe!"))
            }
        } catch (e: Exception) {
            throw BadRequestException(Exception("Erro no cadastro do usuário: ${e.message}"))
        }

    }

    override fun verifyCode(request: HashMap<String, Any>): Boolean {
        TODO("Not yet implemented")
    }

    override fun resendCode(userId: String) {
        TODO("Not yet implemented")
    }

    fun createAccessToken(user: UserDetails): String {
        return tokenService.generateToken(user, getAccessTokenExpiration())
    }

    fun createRefreshToken(user: UserDetails): String {
        return tokenService.generateToken(user, getRefreshTokenExpiration())
    }

    private fun getAccessTokenExpiration(): Date {
        return Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
    }

    private fun getRefreshTokenExpiration(): Date {
        return Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration)
    }

}