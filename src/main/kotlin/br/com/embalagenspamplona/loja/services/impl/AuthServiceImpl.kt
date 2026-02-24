package br.com.embalagenspamplona.loja.services.impl

import br.com.embalagenspamplona.loja.config.security.cookies.CookieUtils
import br.com.embalagenspamplona.loja.config.security.jwt.JwtProperties
import br.com.embalagenspamplona.loja.data.dto.UserDTO
import br.com.embalagenspamplona.loja.data.entities.RoleEntity
import br.com.embalagenspamplona.loja.data.entities.UserEntity
import br.com.embalagenspamplona.loja.exceptions.BadRequestException
import br.com.embalagenspamplona.loja.exceptions.ForbiddenException
import br.com.embalagenspamplona.loja.exceptions.InternalServerException
import br.com.embalagenspamplona.loja.exceptions.NotFoundException
import br.com.embalagenspamplona.loja.services.AuthService
import br.com.embalagenspamplona.loja.services.TokenService
import br.com.embalagenspamplona.loja.services.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.hibernate.annotations.NotFound
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.Date

@Service
class AuthServiceImpl(
    private val authManager: AuthenticationManager,
    private val jwtProperties: JwtProperties,
    private val userService: UserService,
    private val tokenService: TokenService,
    private val cookieUtils: CookieUtils
) :
    AuthService {
    override fun authenticate(
        request: HashMap<String, Any>,
        loginAttempts: Int
    ): HashMap<String, Any>? {
        var attempts = loginAttempts
        while (attempts < 3) {

            val authentication =
                authManager.authenticate(UsernamePasswordAuthenticationToken(request["username"], request["password"]))

            if (authentication.isAuthenticated) {
                val userAuthData = userService.loadUserByUsername(request["username"].toString())
                    ?: throw NotFoundException(Exception("Usuário não existe"))
                val accessToken = createAccessToken(userAuthData)
                val refreshToken = createRefreshToken(userAuthData)
                val user = userService.findByEmail(userAuthData.username.toString())
                if (user == null) {
                    return null
                } else {
                    val tokens = HashMap<String, Any>()
                    tokens["accessToken"] = accessToken
                    tokens["refreshToken"] = refreshToken
                    tokenService.storeTokenRedis(refreshToken)
                    val cookie = cookieUtils.addCookie("tokens", ObjectMapper().writeValueAsString(tokens))
                    val response = HashMap<String, Any>()

                    response["cookie"] = cookie
                    response["user"] = user
                    return response
                }


            } else {
                attempts++
                throw BadCredentialsException("Credenciais não encontradas! $attempts tentativas restantes")
            }

        }
        return null
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
                password = BCryptPasswordEncoder().encode(request["password"].toString()),
                gender = request["gender"].toString(),
                phone = request["phone"].toString(),
                email = request["email"].toString(),
                role = RoleEntity(2L, "comum", description = "usuário comum")
            );
            val createdEntity = userService.createUser(userEntity)
            if (createdEntity != null) {
                val authentication = authManager.authenticate(
                    UsernamePasswordAuthenticationToken(
                        request["username"].toString(),
                        request["password"].toString()
                    )

                )
                if (authentication.isAuthenticated) {
                    val authData = userService.loadUserByUsername(request["username"].toString())
                        ?: throw InternalServerException(
                            Exception(
                                "Usuário foi criado mas nao foi possivel autenticar agora! Por favor, tente novamente!"
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
                }
                else {
                    throw InternalServerException(Exception("Erro ao concluir cadastro do usuário!"))
                }
            } else {
                throw BadRequestException(Exception("Erro ao cadastrar o usuário no banco!"))
            }
        } catch (e: Exception) {
            throw BadRequestException(Exception("Erro ao cadastrar usuário ${e.message}"))
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