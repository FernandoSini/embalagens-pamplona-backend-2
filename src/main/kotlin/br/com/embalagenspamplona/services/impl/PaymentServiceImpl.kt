/*
package br.com.embalagenspamplona.services.impl

import br.com.embalagenspamplona.config.PagarmeProperties
import br.com.embalagenspamplona.data.dto.*
import br.com.embalagenspamplona.data.entities.OrderEntity
import br.com.embalagenspamplona.data.entities.PaymentEntity
import br.com.embalagenspamplona.data.enums.OrderStatus
import br.com.embalagenspamplona.data.enums.PaymentMethod
import br.com.embalagenspamplona.data.enums.PaymentStatus
import br.com.embalagenspamplona.gateway.pagarme.*
import br.com.embalagenspamplona.repository.datasource.local.OrderRepository
import br.com.embalagenspamplona.repository.datasource.local.PaymentRepository
import br.com.embalagenspamplona.services.PaymentService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
@Transactional
class PaymentServiceImpl(
    private val paymentRepository: PaymentRepository,
    private val orderRepository: OrderRepository,
    private val pagarmeClient: PagarmeClient,
    private val pagarmeProperties: PagarmeProperties,
    private val objectMapper: ObjectMapper
) : PaymentService {

    private val logger = LoggerFactory.getLogger(PaymentServiceImpl::class.java)

    override fun processPayment(request: ProcessPaymentRequest): PaymentDTO {
        return when (request.method) {
            PaymentMethod.PIX -> processPixPayment(request).let { getPaymentById(it.paymentId) }
            PaymentMethod.BOLETO -> processBoletoPayment(request).let { getPaymentById(it.paymentId) }
            PaymentMethod.CREDIT_CARD, PaymentMethod.DEBIT_CARD -> processCardPayment(request).let { getPaymentById(it.paymentId) }
            PaymentMethod.BANK_TRANSFER -> createPendingPayment(request)
        }
    }

    // ===================== PIX =====================

    override fun processPixPayment(request: ProcessPaymentRequest): PixPaymentResponse {
        val order = orderRepository.findByIdWithItems(request.orderId)
            ?: throw EntityNotFoundException("Pedido não encontrado: ${request.orderId}")

        // Construir e enviar request para o Pagar.me
        val pagarmeRequest = buildPagarmeOrderRequest(order, "pix", request)
        val pagarmeResponse = pagarmeClient.createOrder(pagarmeRequest)

        val charge = pagarmeResponse.charges?.firstOrNull()
            ?: throw RuntimeException("Nenhuma cobrança retornada pelo Pagar.me")
        val transaction = charge.lastTransaction
            ?: throw RuntimeException("Nenhuma transação retornada pelo Pagar.me")

        // Salvar pagamento no banco
        val payment = PaymentEntity(
            order = order,
            method = PaymentMethod.PIX,
            status = PaymentStatus.PENDING,
            amount = request.amount,
            pixCode = transaction.qrCode,
            pixQrCode = transaction.qrCodeUrl,
            transactionId = charge.id,
            externalReference = pagarmeResponse.id,
            gatewayResponse = objectMapper.writeValueAsString(pagarmeResponse)
        )

        val savedPayment = paymentRepository.save(payment)

        // Parsear data de expiração do PIX
        val expiresAt = parseLocalDateTime(transaction.expiresAt)
            ?: LocalDateTime.now().plusHours(14)

        return PixPaymentResponse(
            paymentId = savedPayment.id!!,
            pixCode = transaction.qrCode ?: "",
            pixQrCode = transaction.qrCodeUrl ?: "",
            expiresAt = expiresAt
        )
    }

    // ===================== BOLETO =====================

    override fun processBoletoPayment(request: ProcessPaymentRequest): BoletoPaymentResponse {
        val order = orderRepository.findByIdWithItems(request.orderId)
            ?: throw EntityNotFoundException("Pedido não encontrado: ${request.orderId}")

        val pagarmeRequest = buildPagarmeOrderRequest(order, "boleto", request)
        val pagarmeResponse = pagarmeClient.createOrder(pagarmeRequest)

        val charge = pagarmeResponse.charges?.firstOrNull()
            ?: throw RuntimeException("Nenhuma cobrança retornada pelo Pagar.me")
        val transaction = charge.lastTransaction
            ?: throw RuntimeException("Nenhuma transação retornada pelo Pagar.me")

        val boletoUrl = transaction.url ?: transaction.pdf ?: ""
        val boletoBarcode = transaction.line ?: transaction.barcode ?: ""
        val dueDate = parseLocalDateTime(transaction.dueAt)
            ?: LocalDateTime.now().plusDays(pagarmeProperties.boletoDueDays.toLong())

        val payment = PaymentEntity(
            order = order,
            method = PaymentMethod.BOLETO,
            status = PaymentStatus.PENDING,
            amount = request.amount,
            boletoUrl = boletoUrl,
            boletoBarcode = boletoBarcode,
            boletoDueDate = dueDate.atZone(ZoneId.systemDefault()),
            transactionId = charge.id,
            externalReference = pagarmeResponse.id,
            gatewayResponse = objectMapper.writeValueAsString(pagarmeResponse)
        )

        val savedPayment = paymentRepository.save(payment)

        return BoletoPaymentResponse(
            paymentId = savedPayment.id!!,
            boletoUrl = boletoUrl,
            boletoBarcode = boletoBarcode,
            dueDate = dueDate
        )
    }

    // ===================== CARTÃO =====================

    override fun processCardPayment(request: ProcessPaymentRequest): CardPaymentResponse {
        val order = orderRepository.findByIdWithItems(request.orderId)
            ?: throw EntityNotFoundException("Pedido não encontrado: ${request.orderId}")

        val paymentMethodStr = when (request.method) {
            PaymentMethod.DEBIT_CARD -> "debit_card"
            else -> "credit_card"
        }

        val pagarmeRequest = buildPagarmeOrderRequest(order, paymentMethodStr, request)
        val pagarmeResponse = pagarmeClient.createOrder(pagarmeRequest)

        val charge = pagarmeResponse.charges?.firstOrNull()
            ?: throw RuntimeException("Nenhuma cobrança retornada pelo Pagar.me")
        val transaction = charge.lastTransaction

        val cardLastDigits = transaction?.card?.lastFourDigits ?: "****"
        val cardBrand = transaction?.card?.brand ?: "Unknown"

        // Mapear status da cobrança do Pagar.me
        val paymentStatus = mapPagarmeStatus(charge.status)

        val payment = PaymentEntity(
            order = order,
            method = request.method,
            status = paymentStatus,
            amount = request.amount,
            transactionId = charge.id,
            externalReference = pagarmeResponse.id,
            cardLastDigits = cardLastDigits,
            cardBrand = cardBrand,
            installments = request.installments,
            paidAt = if (paymentStatus == PaymentStatus.APPROVED) ZonedDateTime.now() else null,
            gatewayResponse = objectMapper.writeValueAsString(pagarmeResponse)
        )

        val savedPayment = paymentRepository.save(payment)

        // Se aprovado, atualizar status do pedido
        if (paymentStatus == PaymentStatus.APPROVED) {
            orderRepository.save(order.copy(
                status = OrderStatus.CONFIRMED,
                updatedAt = ZonedDateTime.now()
            ))
        }

        return CardPaymentResponse(
            paymentId = savedPayment.id!!,
            status = paymentStatus,
            transactionId = charge.id,
            cardLastDigits = cardLastDigits,
            cardBrand = cardBrand
        )
    }

    // ===================== CONSULTAS =====================

    override fun getPaymentByOrderId(orderId: Long): PaymentDTO {
        return paymentRepository.findByOrderId(orderId)
            .orElseThrow { EntityNotFoundException("Pagamento não encontrado para o pedido: $orderId") }
            .toDTO()
    }

    override fun getPaymentById(paymentId: UUID): PaymentDTO {
        TODO("Not yet implemented")
    }

    override fun getPaymentById(paymentId: Long): PaymentDTO {
        return paymentRepository.findById(paymentId)
            .orElseThrow { EntityNotFoundException("Pagamento não encontrado: $paymentId") }
            .toDTO()
    }

    // ===================== WEBHOOK GENÉRICO =====================

    override fun handleWebhook(request: PaymentWebhookRequest): PaymentDTO {
        val payment = paymentRepository.findByTransactionId(request.transactionId)
            .orElseGet { 
                paymentRepository.findByExternalReference(request.externalReference ?: "")
                    .orElseThrow { EntityNotFoundException("Pagamento não encontrado: ${request.transactionId}") }
            }

        val newStatus = when (request.status.uppercase()) {
            "APPROVED", "PAID", "CONFIRMED" -> PaymentStatus.APPROVED
            "REJECTED", "DECLINED", "FAILED" -> PaymentStatus.REJECTED
            "CANCELLED", "CANCELED" -> PaymentStatus.CANCELLED
            "REFUNDED" -> PaymentStatus.REFUNDED
            else -> PaymentStatus.PENDING
        }

        val updatedPayment = payment.copy(
            status = newStatus,
            paidAt = if (newStatus == PaymentStatus.APPROVED) {
                request.paidAt?.atZone(ZoneId.systemDefault()) ?: ZonedDateTime.now()
            } else null,
            gatewayResponse = request.gatewayResponse,
            updatedAt = ZonedDateTime.now()
        )

        val savedPayment = paymentRepository.save(updatedPayment)

        // Atualizar status do pedido baseado no pagamento
        val order = payment.order!!
        val orderStatus = when (newStatus) {
            PaymentStatus.APPROVED -> OrderStatus.CONFIRMED
            PaymentStatus.REJECTED, PaymentStatus.CANCELLED -> OrderStatus.CANCELLED
            else -> order.status
        }

        if (orderStatus != order.status) {
            orderRepository.save(order.copy(
                status = orderStatus,
                updatedAt = ZonedDateTime.now()
            ))
        }

        return savedPayment.toDTO()
    }

    // ===================== WEBHOOK PAGAR.ME =====================

    override fun handlePagarmeWebhook(event: PagarmeWebhookEvent): PaymentDTO {
        logger.info("Processando webhook Pagar.me: tipo={}, id={}", event.type, event.id)

        val chargeData = event.data
            ?: throw IllegalArgumentException("Dados do webhook não encontrados")

        val chargeId = chargeData.id
            ?: throw IllegalArgumentException("ID da cobrança não encontrado no webhook")

        // Buscar pagamento pelo ID da cobrança do Pagar.me (armazenado como transactionId)
        val payment = paymentRepository.findByTransactionId(chargeId)
            .orElseThrow { EntityNotFoundException("Pagamento não encontrado para cobrança Pagar.me: $chargeId") }

        // Mapear status do Pagar.me para PaymentStatus
        val newStatus = mapPagarmeStatus(chargeData.status)

        val updatedPayment = payment.copy(
            status = newStatus,
            paidAt = if (newStatus == PaymentStatus.APPROVED) ZonedDateTime.now() else payment.paidAt,
            gatewayResponse = objectMapper.writeValueAsString(event),
            updatedAt = ZonedDateTime.now()
        )

        val savedPayment = paymentRepository.save(updatedPayment)

        // Atualizar status do pedido
        val order = payment.order!!
        val orderStatus = when (newStatus) {
            PaymentStatus.APPROVED -> OrderStatus.CONFIRMED
            PaymentStatus.REJECTED, PaymentStatus.CANCELLED -> OrderStatus.CANCELLED
            else -> order.status
        }

        if (orderStatus != order.status) {
            orderRepository.save(order.copy(
                status = orderStatus,
                updatedAt = ZonedDateTime.now()
            ))
        }

        logger.info("Webhook Pagar.me processado: pagamento={}, status={}", savedPayment.id, newStatus)
        return savedPayment.toDTO()
    }

    // ===================== REEMBOLSO =====================

    override fun refund(request: RefundRequest): PaymentDTO {
        val payment = paymentRepository.findById(request.paymentId)
            .orElseThrow { EntityNotFoundException("Pagamento não encontrado: ${request.paymentId}") }

        if (payment.status != PaymentStatus.APPROVED) {
            throw IllegalArgumentException("Apenas pagamentos aprovados podem ser reembolsados")
        }

        // Cancelar/estornar no Pagar.me
        val chargeId = payment.transactionId
        if (!chargeId.isNullOrBlank()) {
            try {
                pagarmeClient.cancelCharge(chargeId)
                logger.info("Cobrança estornada no Pagar.me: {}", chargeId)
            } catch (e: Exception) {
                logger.error("Erro ao estornar cobrança no Pagar.me: {}", e.message)
                throw RuntimeException("Erro ao processar reembolso no Pagar.me", e)
            }
        }

        val refundAmount = request.amount ?: payment.amount

        val refundedPayment = payment.copy(
            status = PaymentStatus.REFUNDED,
            refundAmount = refundAmount,
            refundedAt = ZonedDateTime.now(),
            updatedAt = ZonedDateTime.now()
        )

        return paymentRepository.save(refundedPayment).toDTO()
    }

    // ===================== CANCELAMENTO =====================

    override fun cancelPayment(paymentId: UUID): PaymentDTO {
        val payment = paymentRepository.findById(paymentId)
            .orElseThrow { EntityNotFoundException("Pagamento não encontrado: $paymentId") }

        if (payment.status == PaymentStatus.APPROVED) {
            throw IllegalArgumentException("Pagamentos aprovados devem ser reembolsados, não cancelados")
        }

        // Cancelar no Pagar.me
        val chargeId = payment.transactionId
        if (!chargeId.isNullOrBlank()) {
            try {
                pagarmeClient.cancelCharge(chargeId)
                logger.info("Cobrança cancelada no Pagar.me: {}", chargeId)
            } catch (e: Exception) {
                logger.warn("Erro ao cancelar cobrança no Pagar.me (pode já estar cancelada): {}", e.message)
            }
        }

        val cancelledPayment = payment.copy(
            status = PaymentStatus.CANCELLED,
            cancelledAt = ZonedDateTime.now(),
            updatedAt = ZonedDateTime.now()
        )

        return paymentRepository.save(cancelledPayment).toDTO()
    }

    // ===================== MÉTODOS PRIVADOS =====================

    private fun createPendingPayment(request: ProcessPaymentRequest): PaymentDTO {
        val order = orderRepository.findById(request.orderId)
            .orElseThrow { EntityNotFoundException("Pedido não encontrado: ${request.orderId}") }

        val payment = PaymentEntity(
            order = order,
            method = request.method,
            status = PaymentStatus.PENDING,
            amount = request.amount,
            externalReference = request.orderId.toString()
        )

        return paymentRepository.save(payment).toDTO()
    }

    */
/**
     * Constrói o request para criar um pedido no Pagar.me.
     * Extrai dados do cliente a partir do usuário do pedido,
     * e os itens do pedido como itens do Pagar.me.
     *//*

    private fun buildPagarmeOrderRequest(
        order: OrderEntity,
        paymentMethod: String,
        request: ProcessPaymentRequest
    ): PagarmeOrderRequest {
        val user = order.user

        // Construir itens do pedido para o Pagar.me (valores em centavos)
        val items = if (order.items.isNotEmpty()) {
            order.items.map { item ->
                PagarmeItem(
                    amount = item.price.multiply(BigDecimal(item.quantitySelected)).toCentavos(),
                    description = item.name,
                    quantity = item.quantitySelected,
                    code = item.product.id?.toString()
                )
            }
        } else {
            // Fallback: usar o total como um único item
            listOf(
                PagarmeItem(
                    amount = request.amount.toCentavos(),
                    description = "Pedido #${order.id}",
                    quantity = 1,
                    code = order.id.toString()
                )
            )
        }

        // Construir dados do cliente para o Pagar.me
        val customer = PagarmeCustomer(
            name = user?.name ?: request.cardHolderName ?: "Cliente",
            email = user?.email ?: "cliente@email.com",
            document = user?.cpf ?: user?.cnpj ?: request.cardHolderDocument ?: "",
            documentType = if (user?.cnpj != null) "cnpj" else "cpf",
            type = if (user?.cnpj != null) "company" else "individual",
            phones = user?.phone?.let { parsePhone(it) }
        )

        // Construir configuração de pagamento conforme o método
        val payment = when (paymentMethod) {
            "pix" -> PagarmePayment(
                paymentMethod = "pix",
                pix = PagarmePixConfig(expiresIn = pagarmeProperties.pixExpirationSeconds),
                amount = request.amount.toCentavos()
            )
            "boleto" -> PagarmePayment(
                paymentMethod = "boleto",
                boleto = PagarmeBoletoConfig(
                    instructions = "Pagar até o vencimento",
                    dueAt = ZonedDateTime.now()
                        .plusDays(pagarmeProperties.boletoDueDays.toLong())
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                ),
                amount = request.amount.toCentavos()
            )
            "credit_card" -> PagarmePayment(
                paymentMethod = "credit_card",
                creditCard = PagarmeCreditCardConfig(
                    installments = request.installments,
                    cardToken = request.cardToken,
                    statementDescriptor = "EMBPAMPLONA"
                ),
                amount = request.amount.toCentavos()
            )
            "debit_card" -> PagarmePayment(
                paymentMethod = "debit_card",
                creditCard = PagarmeCreditCardConfig(
                    installments = 1,
                    cardToken = request.cardToken,
                    statementDescriptor = "EMBPAMPLONA"
                ),
                amount = request.amount.toCentavos()
            )
            else -> throw IllegalArgumentException("Método de pagamento não suportado no Pagar.me: $paymentMethod")
        }

        return PagarmeOrderRequest(
            items = items,
            customer = customer,
            payments = listOf(payment),
            code = order.id.toString()
        )
    }

    */
/**
     * Parseia um telefone brasileiro para o formato do Pagar.me.
     * Aceita formatos como: (11)99999-9999, 11999999999, +5511999999999
     *//*

    private fun parsePhone(phone: String): PagarmePhones? {
        val cleanPhone = phone.replace(Regex("[^0-9]"), "")
        if (cleanPhone.length < 10) return null

        // Remover código do país se presente
        val nationalNumber = if (cleanPhone.startsWith("55") && cleanPhone.length >= 12) {
            cleanPhone.substring(2)
        } else {
            cleanPhone
        }

        val areaCode = nationalNumber.substring(0, 2)
        val number = nationalNumber.substring(2)

        return PagarmePhones(
            mobilePhone = PagarmeMobilePhone(
                countryCode = "55",
                areaCode = areaCode,
                number = number
            )
        )
    }

    */
/**
     * Mapeia o status de cobrança do Pagar.me para o PaymentStatus interno.
     *//*

    private fun mapPagarmeStatus(status: String?): PaymentStatus {
        return when (status?.lowercase()) {
            "paid" -> PaymentStatus.APPROVED
            "pending" -> PaymentStatus.PENDING
            "processing" -> PaymentStatus.PROCESSING
            "failed", "payment_failed", "not_authorized" -> PaymentStatus.REJECTED
            "canceled", "cancelled", "voided" -> PaymentStatus.CANCELLED
            "chargedback", "refunded" -> PaymentStatus.REFUNDED
            else -> PaymentStatus.PENDING
        }
    }

    */
/**
     * Converte BigDecimal (reais) para centavos (Int) para a API do Pagar.me.
     *//*

    private fun BigDecimal.toCentavos(): Int {
        return this.multiply(BigDecimal(100)).toInt()
    }

    */
/**
     * Parseia uma data ISO 8601 para LocalDateTime.
     *//*

    private fun parseLocalDateTime(dateStr: String?): LocalDateTime? {
        if (dateStr.isNullOrBlank()) return null
        return try {
            ZonedDateTime.parse(dateStr).toLocalDateTime()
        } catch (e: Exception) {
            try {
                LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            } catch (e2: Exception) {
                logger.warn("Não foi possível parsear data: {}", dateStr)
                null
            }
        }
    }

    */
/**
     * Converte PaymentEntity para PaymentDTO.
     *//*

    private fun PaymentEntity.toDTO(): PaymentDTO {
        return PaymentDTO(
            id = this.id,
            orderId = this.order?.id,
            method = this.method,
            status = this.status,
            amount = this.amount,
            transactionId = this.transactionId,
            pixCode = this.pixCode,
            pixQrCode = this.pixQrCode,
            boletoUrl = this.boletoUrl,
            boletoBarcode = this.boletoBarcode,
            boletoDueDate = this.boletoDueDate?.toLocalDateTime(),
            cardLastDigits = this.cardLastDigits,
            cardBrand = this.cardBrand,
            installments = this.installments,
            paidAt = this.paidAt?.toLocalDateTime(),
            createdAt = this.createdAt.toLocalDateTime()
        )
    }
}
*/
