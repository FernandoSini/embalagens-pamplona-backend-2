package br.com.embalagenspamplona.loja.config.security

import br.com.embalagenspamplona.loja.config.handlers.CustomAuthenticationEntryPoint
import br.com.embalagenspamplona.loja.config.handlers.CustomRedirectSuccessHandler

import br.com.embalagenspamplona.loja.config.security.jwt.JwtProperties
import br.com.embalagenspamplona.loja.exceptions.CustomExceptionHandler
import br.com.embalagenspamplona.loja.loja.config.security.jwt.JwtFilter
import br.com.embalagenspamplona.loja.repository.datasource.local.UserRepository
import br.com.embalagenspamplona.loja.services.TokenService
import br.com.embalagenspamplona.loja.services.UserService
import br.com.embalagenspamplona.loja.services.impl.TokenServiceImpl
import br.com.embalagenspamplona.loja.services.impl.UserServiceImpl
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
@EnableWebSecurity
class SecurityConfig(private val authenticationProvider: AuthenticationProvider) {

    @Bean
    fun encoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity, jwtFilter: JwtFilter): SecurityFilterChain {
        http
            .anonymous { it.disable() }
            .httpBasic { it.disable() }
            .cors {
                it.configurationSource(corsConfigurationSource())
            }
            /*   .csrf { it.disable() }*/
            .csrf {
                it.disable()
                CookieCsrfTokenRepository.withHttpOnlyFalse()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                /*   it.invalidSessionUrl("/api/v1/auth/invalid-session")
                   it.sessionAuthenticationErrorUrl("/api/v1/auth/login-error")
                   it.sessionAuthenticationStrategy(ChangeSessionIdAuthenticationStrategy())*/
            }
            .authorizeHttpRequests { auth ->
                // Endpoints públicos
                auth.requestMatchers(
                    "/css/**", "/js/**", "/images/**", "/static/**", "/webjars/**",
                    "/h2/**",
                    "/swagger",
                    "/api/v1/products/**",
                    "/api/v1/segments/**",
                    "/api/v1/auth/**",
                    "/api/v1/payments/webhook",
                    "/api/v1/payments/webhook/**",
                    "/actuator/**",
                    "/api/stripe/**",
                    "/api/docs/**",
                    "/swagger/**",
                    "/swagger-ui.html",
                    "/swagger-ui/index.html",
                    "/swagger-ui/**",

                    ).permitAll()
                // Endpoints que requerem autenticação
                auth.requestMatchers(
                    "/api/v1/catalog/**",
                    "/api/v1/cart/**",
                    "/api/v1/orders/**",
                    "/api/v1/customers/**",
                    "/api/v1/payments/**",
                ).authenticated() // TODO: Alterar para authenticated() após implementar JWT
                    .anyRequest().authenticated()
            }.authenticationProvider(authenticationProvider).exceptionHandling {
                /* it.disable()*/
                it.authenticationEntryPoint(authenticationEntryPoint())
                Customizer.withDefaults<CustomExceptionHandler>()
                /*   it.accessDeniedPage("/error")*/
            }.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .headers { it ->
                //protecao contra xss
                it.xssProtection {
                    it.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                }.contentSecurityPolicy { it ->
                    it.policyDirectives("script-src 'self'")
                }.httpStrictTransportSecurity {
                    it.maxAgeInSeconds(31536000)
                }.frameOptions { it.sameOrigin() }
                    .contentTypeOptions { it.disable() }
            }
            /* .formLogin { it.successHandler(customAuthenticationSuccessHandler()) }*/
            .logout {
                it.logoutUrl("/logout")
                it.logoutSuccessUrl("/auth/login").invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .deleteCookies("SESSID")
                    .deleteCookies("tokens")

            }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.exposedHeaders = listOf("Authorization")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun authenticationEntryPoint(): AuthenticationEntryPoint {
        return CustomAuthenticationEntryPoint()
    }

    @Bean
    fun customAuthenticationSuccessHandler(): AuthenticationSuccessHandler {
        return CustomRedirectSuccessHandler()
    }

    @Bean
    fun javaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        return mailSender
    }

    @Bean
    fun AuthenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    @Bean
    fun jwtFilter(tokenService: TokenService): JwtFilter{
        return JwtFilter(tokenService)
    }
    /*@Bean
    fun userDetailsService(userRepository: UserRepository): UserDetailsService {
        return UserServiceImpl(userRepository)
    }

    @Bean
    fun userService(userRepository: UserRepository): UserService {
        return UserServiceImpl(userRepository)
    }*/

    /*@Bean
    fun tokenService(userRepository: UserRepository): TokenService {
        return TokenServiceImpl(userDetailsService(userRepository))
    }

    @Bean
    fun jwtFilter(tokenService: TokenService): JwtFilter {
        return JwtFilter(tokenService)
    }*/


}