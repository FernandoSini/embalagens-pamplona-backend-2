package br.com.embalagenspamplona.loja.controller

import br.com.embalagenspamplona.loja.data.dto.*
import br.com.embalagenspamplona.loja.data.enums.OrderStatus
import br.com.embalagenspamplona.loja.exceptions.BadRequestException
import br.com.embalagenspamplona.loja.services.OrderService
import com.stripe.exception.StripeException
import com.stripe.model.Refund
import com.stripe.param.RefundCreateParams
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.apache.coyote.Response
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Pedidos", description = "Gerenciamento de pedidos")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping("/{orderId}/refund")
    fun refundProduct(@PathVariable("orderId") orderId: Long): ResponseEntity<String> {
        val order = orderService.findById(orderId)
        val params = RefundCreateParams.builder().setPaymentIntent(order.stripePaymentIntentID).build()
        //fazer a logica para atualizar o stock do produto no banco
        return try {
            val refund = Refund.create(params)
            ResponseEntity.ok(refund.status)
        } catch (e: StripeException) {
            ResponseEntity.status(500).body(e.message)
            throw e

        }


    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID")
    fun findById(@PathVariable id: Long): ResponseEntity<ApiResponse<OrderDTO>> {
        val order = orderService.findById(id)
        return ResponseEntity.ok(ApiResponse.success(order))
    }

    @GetMapping("/client/{clientEmail}")
    @Operation(summary = "Listar pedidos do cliente por email")
    fun findByClientEmail(
        @PathVariable clientEmail: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<PagedResponse<OrderDTO>>> {
        val orders = orderService.findByClientEmail(clientEmail, page, size)
        return ResponseEntity.ok(ApiResponse.success(orders))
    }

    @GetMapping
    @Operation(summary = "Listar todos os pedidos com filtros")
    fun findAll(
        @RequestParam(required = false) clientEmail: String?,
        @RequestParam(required = false) status: OrderStatus?,
        @RequestParam(required = false) startDate: LocalDateTime?,
        @RequestParam(required = false) endDate: LocalDateTime?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<PagedResponse<OrderDTO>>> {
        val filter = OrderFilterRequest(
            clientEmail = clientEmail,
            status = status,
            startDate = startDate,
            endDate = endDate,
            page = page,
            size = size
        )
        val orders = orderService.findAll(filter)
        return ResponseEntity.ok(ApiResponse.success(orders))
    }

   /* @PostMapping
    @Operation(summary = "Criar novo pedido")
    fun create(@RequestBody request: OrderDTO): ResponseEntity<ApiResponse<OrderDTO>> {
        val order = orderService.createOrder(request)
        if(order ==null){
            throw BadRequestException(Exception("Não foi possível criar o pedido, alguma informação está incompleta!"))
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(order, "Pedido criado com sucesso"))
    }*/


    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do pedido")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestBody request: UpdateOrderStatusRequest
    ): ResponseEntity<ApiResponse<OrderDTO>> {
        val order = orderService.updateStatus(id, request)
        return ResponseEntity.ok(ApiResponse.success(order, "Status atualizado com sucesso"))
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancelar pedido")
    fun cancelOrder(
        @PathVariable id: Long,
        @RequestParam(required = false) reason: String?
    ): ResponseEntity<ApiResponse<OrderDTO>> {
        val order = orderService.cancelOrder(id, reason)
        return ResponseEntity.ok(ApiResponse.success(order, "Pedido cancelado com sucesso"))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir pedido")
    fun delete(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> {
        orderService.delete(id)
        return ResponseEntity.ok(ApiResponse.success(Unit, "Pedido excluído com sucesso"))
    }
}
