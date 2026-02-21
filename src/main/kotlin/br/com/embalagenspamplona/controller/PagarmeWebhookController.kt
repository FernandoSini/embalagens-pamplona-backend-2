package br.com.embalagenspamplona.controller

import br.com.embalagenspamplona.gateway.pagarme.PagarmeWebhookEvent
import br.com.embalagenspamplona.services.PaymentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/payments/webhook")
@Tag(name = "Pagar.me Webhook", description = "Webhook para notificações do Pagar.me")
class PagarmeWebhookController(
    private val paymentService: PaymentService
) {

    private val logger = LoggerFactory.getLogger(PagarmeWebhookController::class.java)

    @PostMapping("/pagarme")
    @Operation(summary = "Receber notificações de webhook do Pagar.me")
    fun handlePagarmeWebhook(@RequestBody event: PagarmeWebhookEvent): ResponseEntity<Any> {
        try {
            logger.info("Webhook Pagar.me recebido: tipo={}, id={}", event.type, event.id)
            paymentService.handlePagarmeWebhook(event)
            logger.info("Webhook Pagar.me processado com sucesso: {}", event.id)
        } catch (e: Exception) {
            logger.error("Erro ao processar webhook Pagar.me: {}", e.message, e)
        }
        // Sempre retorna 200 para o Pagar.me não reenviar
        return ResponseEntity.ok().build()
    }
}
