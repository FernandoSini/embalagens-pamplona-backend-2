package br.com.embalagenspamplona.controller

import br.com.embalagenspamplona.data.enums.OrderStatus
import br.com.embalagenspamplona.exceptions.InternalServerException
import br.com.embalagenspamplona.services.OrderService
import com.stripe.exception.SignatureVerificationException
import com.stripe.exception.StripeException
import com.stripe.model.WebhookEndpoint
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.stripe.net.Webhook
import com.stripe.model.checkout.Session
import org.apache.coyote.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/api/stripe")
class StripeWebhookController {


    @Autowired
    private lateinit var orderService: OrderService

    @Value("\${stripe.endpoint.secret}")
    private val endpointSecret: String = "";

    @PostMapping("/webhook")
    fun handleStripeEvent(
        @RequestBody payload: String,
        @RequestHeader("Stripe-Signature") signatureHeader: String
    ): ResponseEntity<String> {
        return try {
            val event =
                Webhook.constructEvent(payload, signatureHeader, endpointSecret)


            when (event.type) {
                "checkout.session.completed" -> {
                    val session = event.dataObjectDeserializer.`object`.get() as Session

                    if (session.paymentStatus == "paid") {
                        val orderId = session.clientReferenceId?.toLongOrNull()
                        if (orderId != null) {
                            orderService.updateOrderStatus(orderId, OrderStatus.PAID)
                        }
                    }
                    return ResponseEntity.ok("Pagamento confirmado! Podemos encerrar a sessao de compra!")
                }

                "payment_intent.succeeded" -> {
                    return ResponseEntity.ok("Recebemos o pagamento!")
                }
                "payment_intent.payment_failed" -> {
                    // Ocorreu um erro técnico no processamento final
                    println("Pagamento falhou para o pedido: ${event.id}")
                    return ResponseEntity.ok().body("")
                }


                "checkout.session.async_payment_succeeded" -> {
                    //esse aqui é só pra caso tivermos o boleto ou pix
                    //pix pagamento feito-> liberar agora
                    // O Boleto foi pago 2 dias depois! -> AGORA LIBERAR
                    val session = event.dataObjectDeserializer.`object`.get() as Session
                    val orderId = session.clientReferenceId.toLong()
                    orderService.updateOrderStatus(orderId, OrderStatus.PAID)
                    return ResponseEntity.ok().body("")
                }

                "checkout.session.async_payment_failed" -> {
                    // O Boleto expirou ou o Pix não foi feito no tempo limite
                    val session = event.dataObjectDeserializer.`object`.get() as Session
                    orderService.cancelOrder(session.clientReferenceId.toLong(), reason = "")
                    return ResponseEntity.ok().body("")
                }

                else -> {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("")

                }
            }
        } catch (e: SignatureVerificationException) {
            return ResponseEntity.status(400).body("assinatura inválida: err: ${e.message}")
        } catch (e: StripeException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("erro interno do stripe: ${e.message}")
        } catch (e: Exception) {

            throw InternalServerException(Exception("Erro interno do servidor do site ${e.message}"))
        }


    }

}