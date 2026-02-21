package br.com.embalagenspamplona.services

import br.com.embalagenspamplona.data.dto.*
import br.com.embalagenspamplona.gateway.pagarme.PagarmeWebhookEvent
import java.util.UUID

interface PaymentService {
    
    fun processPayment(request: ProcessPaymentRequest): PaymentDTO
    
    fun processPixPayment(request: ProcessPaymentRequest): PixPaymentResponse
    
    fun processBoletoPayment(request: ProcessPaymentRequest): BoletoPaymentResponse
    
    fun processCardPayment(request: ProcessPaymentRequest): CardPaymentResponse
    
    fun getPaymentByOrderId(orderId: Long): PaymentDTO
    
    fun getPaymentById(paymentId: UUID): PaymentDTO
    
    fun handleWebhook(request: PaymentWebhookRequest): PaymentDTO

    fun handlePagarmeWebhook(event: PagarmeWebhookEvent): PaymentDTO
    
    fun refund(request: RefundRequest): PaymentDTO
    
    fun cancelPayment(paymentId: UUID): PaymentDTO
}
