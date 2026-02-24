package br.com.embalagenspamplona.loja.controller

import br.com.embalagenspamplona.loja.data.dto.ApiResponse
import br.com.embalagenspamplona.loja.data.dto.CheckoutResponse
import br.com.embalagenspamplona.loja.data.dto.OrderRequest
import br.com.embalagenspamplona.loja.exceptions.BadRequestException
import br.com.embalagenspamplona.loja.exceptions.PaymentNeededException
import br.com.embalagenspamplona.loja.services.CheckoutService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@RestController
@RequestMapping("/api/v1/checkout")
@Tag(name = "Checkout", description = "Finalização de compra - converte carrinho em pedido")
class CheckoutController(
    private val checkoutService: CheckoutService
) {
    private val logger = LoggerFactory.getLogger(CheckoutController::class.java)

    @Value("\${secret.key.crypto}")
    private val secretKetCrypto: String = ""

    @PostMapping("/finalizar-pedido")
    @Operation(summary = "Finalizar compra e criar sessão de pagamento Stripe")
    fun checkout(@RequestBody request: OrderRequest
    ): ResponseEntity<ApiResponse<Map<String, String>>> {

            val response = checkoutService.checkout(request)

            // Cria a sessão Stripe automaticamente com o orderId do pedido criado
            val stripeSession = checkoutService.createStripeSession(response.orderId)

            val responseWithStripe = response.copy(
                stripeClientSecret = stripeSession["clientSecret"],
                stripeSessionId = stripeSession["sessionId"]
            )

            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                    ApiResponse.success(
                        mapOf("sessionId" to responseWithStripe.stripeSessionId.toString()),
                        "Sessão criada com sucesso!"
                    )
                )

    }

    /*  @PostMapping("/criar-sessao")
      @Operation(summary = `"Criar sessão de pagamento Stripe para um pedido existente")
      fun createSessionCheckoutEmbedded(
          @RequestBody request: Map<String, Long>
      ): ResponseEntity<Map<String, String>> {
          val orderId = request["orderId"]
              ?: throw IllegalArgumentException("orderId é obrigatório")
          val session = checkoutService.createStripeSession(orderId)
          return ResponseEntity.ok(session)
      }*/

    @OptIn(ExperimentalEncodingApi::class)
    @GetMapping("/confirmar-pagamento")
    @Operation(summary = "Confirmar o status do pagamento de uma sessão Stripe")
    fun confirmPayment(
        @RequestParam("sessionId") encryptedSessionId: String
    ): ResponseEntity<ApiResponse<String>> {
        val secretKeySpec = SecretKeySpec(secretKetCrypto.toByteArray(), "AES")
        val bytesEncrypted = Base64.decode(encryptedSessionId)
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        val decryptedBytes = cipher.doFinal(bytesEncrypted)
        val result = checkoutService.confirmPayment(String(decryptedBytes))
        if (!result.isEmpty()) {
            if (result["status"] == "paid") {
                return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success("redirect:/checkout/success", "Pagamento confirmado!"))
            } else {
                return ResponseEntity.status(HttpStatus.OK)
                    .body(
                        ApiResponse.success(
                            "redirect:/checkout/error?reason=${result["status"]}",
                            result["message"].toString()
                        )
                    )
            }
        } else {
            throw PaymentNeededException(Exception("Pagamento obrigatório: Não coseguimos recuperar a informacao do carrinho "))
        }
    }


}
