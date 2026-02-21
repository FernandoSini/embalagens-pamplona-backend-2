package br.com.embalagenspamplona.gateway.pagarme

import br.com.embalagenspamplona.config.PagarmeProperties
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.Base64

@Component
class PagarmeClient(
    private val pagarmeProperties: PagarmeProperties,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(PagarmeClient::class.java)

    private val webClient: WebClient by lazy {
        val credentials = Base64.getEncoder().encodeToString(
            "${pagarmeProperties.secretKey}:".toByteArray()
        )
        WebClient.builder()
            .baseUrl(pagarmeProperties.baseUrl)
            .defaultHeader("Authorization", "Basic $credentials")
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    /**
     * Cria um pedido no Pagar.me com a cobrança (PIX, Boleto ou Cartão).
     */
    fun createOrder(request: PagarmeOrderRequest): PagarmeOrderResponse {
        try {
            logger.info("Criando pedido no Pagar.me para o código: {}", request.code)
            logger.debug("Request body: {}", objectMapper.writeValueAsString(request))

            val response = webClient.post()
                .uri("/orders")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PagarmeOrderResponse::class.java)
                .block() ?: throw RuntimeException("Resposta vazia do Pagar.me")

            logger.info("Pedido criado no Pagar.me com ID: {}, status: {}", response.id, response.status)
            return response
        } catch (e: WebClientResponseException) {
            logger.error("Erro na API Pagar.me [{}]: {}", e.statusCode, e.responseBodyAsString)
            throw RuntimeException("Erro ao comunicar com Pagar.me: ${e.responseBodyAsString}", e)
        }
    }

    /**
     * Consulta um pedido existente no Pagar.me.
     */
    fun getOrder(orderId: String): PagarmeOrderResponse {
        try {
            logger.info("Consultando pedido no Pagar.me: {}", orderId)

            return webClient.get()
                .uri("/orders/{orderId}", orderId)
                .retrieve()
                .bodyToMono(PagarmeOrderResponse::class.java)
                .block() ?: throw RuntimeException("Resposta vazia do Pagar.me")
        } catch (e: WebClientResponseException) {
            logger.error("Erro na API Pagar.me [{}]: {}", e.statusCode, e.responseBodyAsString)
            throw RuntimeException("Erro ao consultar pedido no Pagar.me: ${e.responseBodyAsString}", e)
        }
    }

    /**
     * Cancela/estorna uma cobrança no Pagar.me.
     * Usado para reembolsos e cancelamentos.
     */
    fun cancelCharge(chargeId: String): PagarmeChargeResponse {
        try {
            logger.info("Cancelando cobrança no Pagar.me: {}", chargeId)

            return webClient.delete()
                .uri("/charges/{chargeId}", chargeId)
                .retrieve()
                .bodyToMono(PagarmeChargeResponse::class.java)
                .block() ?: throw RuntimeException("Resposta vazia do Pagar.me")
        } catch (e: WebClientResponseException) {
            logger.error("Erro na API Pagar.me [{}]: {}", e.statusCode, e.responseBodyAsString)
            throw RuntimeException("Erro ao cancelar cobrança no Pagar.me: ${e.responseBodyAsString}", e)
        }
    }

    /**
     * Captura uma cobrança previamente autorizada.
     */
    fun captureCharge(chargeId: String, amount: Int): PagarmeChargeResponse {
        try {
            logger.info("Capturando cobrança no Pagar.me: {} - valor: {}", chargeId, amount)

            return webClient.post()
                .uri("/charges/{chargeId}/capture", chargeId)
                .bodyValue(mapOf("amount" to amount, "code" to chargeId))
                .retrieve()
                .bodyToMono(PagarmeChargeResponse::class.java)
                .block() ?: throw RuntimeException("Resposta vazia do Pagar.me")
        } catch (e: WebClientResponseException) {
            logger.error("Erro na API Pagar.me [{}]: {}", e.statusCode, e.responseBodyAsString)
            throw RuntimeException("Erro ao capturar cobrança no Pagar.me: ${e.responseBodyAsString}", e)
        }
    }
}
