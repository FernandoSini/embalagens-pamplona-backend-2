package br.com.embalagenspamplona.controller

import br.com.embalagenspamplona.data.dto.*
import br.com.embalagenspamplona.services.CartService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@RestController
@RequestMapping("/api/v1/cart")
@Tag(name = "Carrinho", description = "Gerenciamento do carrinho de compras")
class CartController(
    private val cartService: CartService
) {

    @Value("\${secret.key.crypto}")
    private val secretKey: String = ""

    @OptIn(ExperimentalEncodingApi::class)
    @PutMapping("/update")
    @Operation(summary = "Atualizar carrinho")
    fun updateCart(
        @RequestBody customerId: String,
        @RequestBody cartItems: List<CartItemDTO>
    ): ResponseEntity<ApiResponse<CartDTO>> {
        val bytes = Base64.decode(customerId)
        val uncryptedId = ByteBuffer.wrap(bytes).getLong()
        val cart = cartService.updateCart(uncryptedId, cartItems)

        return ResponseEntity.ok(ApiResponse.success(cart, "Carrinho atualizado com sucesso"))
    }

    @PutMapping
    @OptIn(ExperimentalEncodingApi::class)
    @GetMapping("/")
    @Operation(summary = "Obter carrinho do cliente")
    fun getCart(@RequestBody customerId: String): ResponseEntity<ApiResponse<CartDTO>> {
        val bytes = Base64.decode(customerId)
        val uncryptedId = ByteBuffer.wrap(bytes).getLong()
        val cart = cartService.getCart(uncryptedId)
        return ResponseEntity.ok(ApiResponse.success(cart))
    }

    @PostMapping("/{customerId}/items")
    @Operation(summary = "Adicionar item ao carrinho")
    fun addItem(
        @PathVariable customerId: Long,
        @RequestBody request: CartItemDTO
    ): ResponseEntity<ApiResponse<CartDTO>> {
        val cart = cartService.addItem(customerId, request)
        return ResponseEntity.ok(ApiResponse.success(cart, "Item adicionado ao carrinho"))
    }

    @PutMapping("/{customerId}/items/{itemId}")
    @Operation(summary = "Atualizar quantidade do item no carrinho")
    fun updateItemQuantity(
        @PathVariable customerId: Long,
        @PathVariable itemId: Long,
        @RequestBody request: UpdateCartItemRequest
    ): ResponseEntity<ApiResponse<CartDTO>> {
        val cart = cartService.updateItemQuantity(customerId, itemId, request)
        return ResponseEntity.ok(ApiResponse.success(cart, "Quantidade atualizada"))
    }

    @DeleteMapping("/{customerId}/items/{itemId}")
    @Operation(summary = "Remover item do carrinho")
    fun removeItem(
        @PathVariable customerId: Long,
        @PathVariable itemId: Long
    ): ResponseEntity<ApiResponse<CartDTO>> {
        val cart = cartService.removeItem(customerId, itemId)
        return ResponseEntity.ok(ApiResponse.success(cart, "Item removido do carrinho"))
    }

    @OptIn(ExperimentalEncodingApi::class)
    @DeleteMapping("/clear")
    @Operation(summary = "Limpar carrinho")
    fun clearCart(@RequestBody customerId: String): ResponseEntity<ApiResponse<Unit>> {
        /* val bytes = Base64.decode(customerId)
         val uncryptedId = ByteBuffer.wrap(bytes).getLong()*/
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(),"AES")
        val bytesEncrypted = Base64.decode(customerId)
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        val bytesDecrypted = cipher.doFinal(bytesEncrypted)

        val realIdString = String(bytesDecrypted, StandardCharsets.UTF_8)
        cartService.clearCart(realIdString.toLong())
        return ResponseEntity.ok(ApiResponse.success(Unit, "Carrinho limpo com sucesso"))
    }

    @PostMapping("/{customerId}/coupon")
    @Operation(summary = "Aplicar cupom de desconto")
    fun applyCoupon(
        @PathVariable customerId: Long,
        @RequestBody request: ApplyCouponRequest
    ): ResponseEntity<ApiResponse<CartDTO>> {
        val cart = cartService.applyCoupon(customerId, request)
        return ResponseEntity.ok(ApiResponse.success(cart, "Cupom aplicado com sucesso"))
    }

    @DeleteMapping("/{customerId}/coupon")
    @Operation(summary = "Remover cupom de desconto")
    fun removeCoupon(@PathVariable customerId: Long): ResponseEntity<ApiResponse<CartDTO>> {
        val cart = cartService.removeCoupon(customerId)
        return ResponseEntity.ok(ApiResponse.success(cart, "Cupom removido"))
    }

    @GetMapping("/{customerId}/summary")
    @Operation(summary = "Obter resumo do carrinho")
    fun getCartSummary(@PathVariable customerId: Long): ResponseEntity<ApiResponse<CartSummaryDTO>> {
        val summary = cartService.getCartSummary(customerId)
        return ResponseEntity.ok(ApiResponse.success(summary))
    }

    @GetMapping("/{customerId}/validate")
    @Operation(summary = "Validar carrinho antes do checkout")
    fun validateCart(@PathVariable customerId: Long): ResponseEntity<ApiResponse<Map<String, Any>>> {
        val errors = cartService.validateCart(customerId)
        val isValid = errors.isEmpty()
        return ResponseEntity.ok(
            ApiResponse.success(
                mapOf(
                    "valid" to isValid,
                    "errors" to errors
                )
            )
        )
    }
}
