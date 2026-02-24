package br.com.embalagenspamplona.loja.services

import br.com.embalagenspamplona.loja.data.dto.*
import br.com.embalagenspamplona.loja.gateway.pagarme.PagarmeWebhookEvent


interface PaymentService {
    
    fun processPayment(request: ProcessPaymentRequest): PaymentDTO
    
    fun processPixPayment(request: ProcessPaymentRequest): PixPaymentResponse
    
    fun processBoletoPayment(request: ProcessPaymentRequest): BoletoPaymentResponse
    
    fun processCardPayment(request: ProcessPaymentRequest): CardPaymentResponse
    
    fun getPaymentByOrderId(orderId: Long): PaymentDTO
    
    fun getPaymentById(paymentId: Long): PaymentDTO
    
    fun handleWebhook(request: PaymentWebhookRequest): PaymentDTO

    fun handlePagarmeWebhook(event: PagarmeWebhookEvent): PaymentDTO
    
    fun refund(request: RefundRequest): PaymentDTO
    
    fun cancelPayment(paymentId: Long): PaymentDTO
}
