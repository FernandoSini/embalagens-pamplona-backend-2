package br.com.embalagenspamplona.loja.gateway.pagarme

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

// ======================== REQUEST MODELS ========================

data class PagarmeOrderRequest(
    val items: List<PagarmeItem>,
    val customer: PagarmeCustomer,
    val payments: List<PagarmePayment>,
    val code: String? = null,
    val closed: Boolean = true
)

data class PagarmeItem(
    val amount: Int, // valor em centavos
    val description: String,
    val quantity: Int,
    val code: String? = null
)

data class PagarmeCustomer(
    val name: String,
    val email: String,
    val document: String,
    @JsonProperty("document_type")
    val documentType: String = "cpf",
    val type: String = "individual",
    val phones: PagarmePhones? = null
)

data class PagarmePhones(
    @JsonProperty("mobile_phone")
    val mobilePhone: PagarmeMobilePhone? = null
)

data class PagarmeMobilePhone(
    @JsonProperty("country_code")
    val countryCode: String = "55",
    @JsonProperty("area_code")
    val areaCode: String,
    val number: String
)

data class PagarmePayment(
    @JsonProperty("payment_method")
    val paymentMethod: String, // "pix", "boleto", "credit_card", "debit_card"
    val pix: PagarmePixConfig? = null,
    val boleto: PagarmeBoletoConfig? = null,
    @JsonProperty("credit_card")
    val creditCard: PagarmeCreditCardConfig? = null,
    val amount: Int? = null
)

data class PagarmePixConfig(
    @JsonProperty("expires_in")
    val expiresIn: Int = 52800 // segundos (padrão ~14.6 horas)
)

data class PagarmeBoletoConfig(
    val instructions: String = "Pagar até o vencimento",
    @JsonProperty("due_at")
    val dueAt: String // ISO 8601
)

data class PagarmeCreditCardConfig(
    val installments: Int = 1,
    @JsonProperty("card_token")
    val cardToken: String? = null,
    val card: PagarmeCardInfo? = null,
    @JsonProperty("statement_descriptor")
    val statementDescriptor: String = "EMBPAMPLONA"
)

data class PagarmeCardInfo(
    @JsonProperty("billing_address")
    val billingAddress: PagarmeAddress? = null
)

data class PagarmeAddress(
    @JsonProperty("line_1")
    val line1: String,
    @JsonProperty("line_2")
    val line2: String? = null,
    @JsonProperty("zip_code")
    val zipCode: String,
    val city: String,
    val state: String,
    val country: String = "BR"
)

// ======================== RESPONSE MODELS ========================

@JsonIgnoreProperties(ignoreUnknown = true)
data class PagarmeOrderResponse(
    val id: String? = null,
    val code: String? = null,
    val status: String? = null,
    val charges: List<PagarmeChargeResponse>? = null,
    val customer: PagarmeCustomerResponse? = null,
    val amount: Int? = null,
    @JsonProperty("created_at")
    val createdAt: String? = null,
    @JsonProperty("updated_at")
    val updatedAt: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PagarmeChargeResponse(
    val id: String? = null,
    val code: String? = null,
    val status: String? = null,
    val amount: Int? = null,
    @JsonProperty("paid_amount")
    val paidAmount: Int? = null,
    @JsonProperty("payment_method")
    val paymentMethod: String? = null,
    @JsonProperty("last_transaction")
    val lastTransaction: PagarmeTransactionResponse? = null,
    @JsonProperty("created_at")
    val createdAt: String? = null,
    @JsonProperty("updated_at")
    val updatedAt: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PagarmeTransactionResponse(
    val id: String? = null,
    @JsonProperty("transaction_type")
    val transactionType: String? = null,
    val status: String? = null,
    val amount: Int? = null,

    // Campos PIX
    @JsonProperty("qr_code")
    val qrCode: String? = null,
    @JsonProperty("qr_code_url")
    val qrCodeUrl: String? = null,
    @JsonProperty("expires_at")
    val expiresAt: String? = null,

    // Campos Boleto
    val url: String? = null,
    val barcode: String? = null,
    @JsonProperty("due_at")
    val dueAt: String? = null,
    val line: String? = null,
    val pdf: String? = null,

    // Campos Cartão
    @JsonProperty("gateway_id")
    val gatewayId: String? = null,
    @JsonProperty("acquirer_tid")
    val acquirerTid: String? = null,
    @JsonProperty("acquirer_nsu")
    val acquirerNsu: String? = null,
    @JsonProperty("acquirer_auth_code")
    val acquirerAuthCode: String? = null,
    val installments: Int? = null,
    val card: PagarmeCardResponse? = null,

    @JsonProperty("gateway_response")
    val gatewayResponse: PagarmeGatewayResponse? = null,
    @JsonProperty("created_at")
    val createdAt: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PagarmeCardResponse(
    val id: String? = null,
    @JsonProperty("first_six_digits")
    val firstSixDigits: String? = null,
    @JsonProperty("last_four_digits")
    val lastFourDigits: String? = null,
    val brand: String? = null,
    @JsonProperty("holder_name")
    val holderName: String? = null,
    val type: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PagarmeGatewayResponse(
    val code: String? = null,
    val errors: List<PagarmeError>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PagarmeError(
    val message: String? = null,
    val type: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PagarmeCustomerResponse(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val document: String? = null
)

// ======================== WEBHOOK MODELS ========================

@JsonIgnoreProperties(ignoreUnknown = true)
data class PagarmeWebhookEvent(
    val id: String? = null,
    val type: String? = null,
    val data: PagarmeWebhookData? = null,
    @JsonProperty("created_at")
    val createdAt: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PagarmeWebhookData(
    val id: String? = null,
    val code: String? = null,
    val status: String? = null,
    val amount: Int? = null,
    @JsonProperty("paid_amount")
    val paidAmount: Int? = null,
    @JsonProperty("payment_method")
    val paymentMethod: String? = null,
    @JsonProperty("last_transaction")
    val lastTransaction: PagarmeTransactionResponse? = null
)
