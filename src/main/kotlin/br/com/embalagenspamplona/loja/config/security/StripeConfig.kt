package br.com.embalagenspamplona.loja.config.security

import com.stripe.Stripe
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class StripeConfig {
    @Value("\${stripe.secret.key}")
    lateinit var apiKey: String

    @PostConstruct
    fun setup() {
        Stripe.apiKey = apiKey
    }
}