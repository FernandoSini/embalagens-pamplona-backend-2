package br.com.embalagenspamplona.loja.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class PagarmeProperties {

    @Value("\${pagarme.secret-key:}")
    lateinit var secretKey: String

    @Value("\${pagarme.public-key:}")
    lateinit var publicKey: String

    @Value("\${pagarme.base-url:https://api.pagar.me/core/v5}")
    lateinit var baseUrl: String

    @Value("\${pagarme.webhook-secret:}")
    lateinit var webhookSecret: String

    @Value("\${pagarme.pix-expiration-seconds:52800}")
    var pixExpirationSeconds: Int = 52800

    @Value("\${pagarme.boleto-due-days:3}")
    var boletoDueDays: Int = 3
}
