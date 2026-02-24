package br.com.embalagenspamplona.loja.data.dto

import java.time.LocalDateTime
import java.util.UUID

data class CustomerDTO(
    val id: Long? = null,
    val name: String,
    val email: String,
    val phone: String? = null,
    val cpf: String? = null,
    val cnpj: String? = null,
    val companyName: String? = null,
    val addresses: List<AddressDTO> = emptyList(),
    val emailVerified: Boolean = false,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

data class CreateCustomerRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String? = null,
    val cpf: String? = null,
    val cnpj: String? = null,
    val companyName: String? = null
)

data class UpdateCustomerRequest(
    val name: String? = null,
    val phone: String? = null,
    val cpf: String? = null,
    val cnpj: String? = null,
    val companyName: String? = null
)

data class AddressDTO(
    val id: Long,
    val street: String,
    val number: String,
    val complementNumber: String? = null,
    val neighborhood: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val reference: String? = null,
    val isDefault: Boolean = false,
    val label: String? = null
)

data class CreateAddressRequest(
    val userId: Long,
    val street: String,
    val number: String,
    val complementNumber: String,
    val neighborhood: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val reference: String? = null,
    val isDefault: Boolean = false,
    val label: String? = null
)
data class updateAddressRequest(
    val addressId: Long,
    val userId: Long,
    val street: String,
    val complementNumber: String,
    val unityBlock: String? = null,
    val neighborhood: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val reference: String? = null,
    val isDefault: Boolean = false,
    val label: String? = null
)
