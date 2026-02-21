package br.com.embalagenspamplona.services

import br.com.embalagenspamplona.data.dto.*
import java.util.UUID

interface CustomerService {
    
    fun findById(id: UUID): CustomerDTO
    
    fun findByEmail(email: String): CustomerDTO

    fun create(request: CreateCustomerRequest): CustomerDTO
    
    fun update(id: Long, request: UserDTO): CustomerDTO
    
    fun delete(id: Long)
    
    fun addAddress(customerId: UUID, request: CreateAddressRequest): AddressDTO
    
    fun updateAddress(customerId: UUID, addressId: UUID, request: CreateAddressRequest): AddressDTO
    
    fun deleteAddress(customerId: UUID, addressId: UUID)
    
    fun setDefaultAddress(customerId: UUID, addressId: UUID): AddressDTO
    
    fun getAddresses(customerId: UUID): List<AddressDTO>
    
    fun existsByEmail(email: String): Boolean
}
