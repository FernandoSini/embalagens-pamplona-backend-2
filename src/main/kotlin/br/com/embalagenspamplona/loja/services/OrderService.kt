package br.com.embalagenspamplona.loja.services

import br.com.embalagenspamplona.loja.data.dto.*
import br.com.embalagenspamplona.loja.data.enums.OrderStatus

interface OrderService {

    //fun createOrder(order: OrderDTO): OrderDTO?

    fun findById(id: Long): OrderDTO

    fun findByClientEmail(clientEmail: String, page: Int, size: Int): PagedResponse<OrderDTO>

    fun findAll(filter: OrderFilterRequest): PagedResponse<OrderDTO>


    fun updateOrderStatus(orderId: Long, orderStatus: OrderStatus)

    fun updateStatus(id: Long, request: UpdateOrderStatusRequest): OrderDTO

    fun cancelOrder(id: Long, reason: String?): OrderDTO

    fun delete(id: Long)
}
