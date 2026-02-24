/*
package br.com.embalagenspamplona.loja.controller

import br.com.embalagenspamplona.loja.data.dto.*
import br.com.embalagenspamplona.loja.services.PaymentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Pagamentos", description = "Gerenciamento de pagamentos")
class PaymentController(
    private val paymentService: PaymentService
) {

    @GetMapping("/{paymentId}")
    @Operation(summary = "Buscar pagamento por ID")
    fun getPaymentById(@PathVariable paymentId: Long): ResponseEntity<ApiResponse<PaymentDTO>> {
        val payment = paymentService.getPaymentById(paymentId)
        return ResponseEntity.ok(ApiResponse.success(payment))
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Buscar pagamento por ID do pedido")
    fun getPaymentByOrderId(@PathVariable orderId: Long): ResponseEntity<ApiResponse<PaymentDTO>> {
        val payment = paymentService.getPaymentByOrderId(orderId)
        return ResponseEntity.ok(ApiResponse.success(payment))
    }

    @PostMapping("/process")
    @Operation(summary = "Processar pagamento")
    fun processPayment(@RequestBody request: ProcessPaymentRequest): ResponseEntity<ApiResponse<PaymentDTO>> {
        val payment = paymentService.processPayment(request)
        return ResponseEntity.ok(ApiResponse.success(payment, "Pagamento processado"))
    }

    @PostMapping("/pix")
    @Operation(summary = "Processar pagamento via PIX")
    fun processPixPayment(@RequestBody request: ProcessPaymentRequest): ResponseEntity<ApiResponse<PixPaymentResponse>> {
        val response = paymentService.processPixPayment(request)
        return ResponseEntity.ok(ApiResponse.success(response, "PIX gerado com sucesso"))
    }

    @PostMapping("/boleto")
    @Operation(summary = "Processar pagamento via Boleto")
    fun processBoletoPayment(@RequestBody request: ProcessPaymentRequest): ResponseEntity<ApiResponse<BoletoPaymentResponse>> {
        val response = paymentService.processBoletoPayment(request)
        return ResponseEntity.ok(ApiResponse.success(response, "Boleto gerado com sucesso"))
    }

    @PostMapping("/card")
    @Operation(summary = "Processar pagamento via Cartão")
    fun processCardPayment(@RequestBody request: ProcessPaymentRequest): ResponseEntity<ApiResponse<CardPaymentResponse>> {
        val response = paymentService.processCardPayment(request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PostMapping("/webhook")
    @Operation(summary = "Webhook para notificações de pagamento")
    fun handleWebhook(@RequestBody request: PaymentWebhookRequest): ResponseEntity<ApiResponse<PaymentDTO>> {
        val payment = paymentService.handleWebhook(request)
        return ResponseEntity.ok(ApiResponse.success(payment))
    }

    @PostMapping( "/refund")
    @Operation(summary = "Solicitar reembolso")
    fun refund(@RequestBody request: RefundRequest): ResponseEntity<ApiResponse<PaymentDTO>> {
        val payment = paymentService.refund(request)
        return ResponseEntity.ok(ApiResponse.success(payment, "Reembolso processado com sucesso"))
    }

    @PostMapping("/{paymentId}/cancel")
    @Operation(summary = "Cancelar pagamento")
    fun cancelPayment(@PathVariable paymentId: Long): ResponseEntity<ApiResponse<PaymentDTO>> {
        val payment = paymentService.cancelPayment(paymentId)
        return ResponseEntity.ok(ApiResponse.success(payment, "Pagamento cancelado"))
    }
}
*/
