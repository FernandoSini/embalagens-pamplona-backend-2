package br.com.embalagenspamplona.loja.services

import br.com.embalagenspamplona.loja.data.dto.CheckoutResponse
import br.com.embalagenspamplona.loja.data.dto.OrderRequest

interface CheckoutService {

    fun checkout(request: OrderRequest): CheckoutResponse

    fun createStripeSession(orderId: Long): Map<String, String>

    fun confirmPayment(sessionId: String): Map<String, Any>
}
