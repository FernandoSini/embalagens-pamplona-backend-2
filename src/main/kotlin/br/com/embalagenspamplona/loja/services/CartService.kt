package br.com.embalagenspamplona.loja.services

import br.com.embalagenspamplona.loja.data.dto.*


interface CartService {

    fun findByUserId(userId: Long): CartDTO

    fun getCart(customerId: Long): CartDTO

    fun addItem(customerId: Long, request: CartItemDTO): CartDTO

    fun updateItemQuantity(customerId: Long, itemId: Long, request: UpdateCartItemRequest): CartDTO

    fun removeItem(customerId: Long, itemId: Long): CartDTO

    fun updateCart(customerId: Long, items: List<CartItemDTO>): CartDTO

    fun clearCart(customerId: Long)

    fun applyCoupon(customerId: Long, request: ApplyCouponRequest): CartDTO

    fun removeCoupon(customerId: Long): CartDTO

    fun getCartSummary(customerId: Long): CartSummaryDTO

    fun validateCart(customerId: Long): List<String>
}
