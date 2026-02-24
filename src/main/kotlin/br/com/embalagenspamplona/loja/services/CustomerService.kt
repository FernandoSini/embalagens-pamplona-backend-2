package br.com.embalagenspamplona.loja.services

import br.com.embalagenspamplona.loja.data.dto.*


interface CustomerService {
    
    fun findById(id: Long): CustomerDTO
    
    fun findByEmail(email: String): CustomerDTO

    fun create(request: CreateCustomerRequest): CustomerDTO
    
    fun update(id: Long, request: UserDTO): CustomerDTO
    
    fun delete(id: Long)
    
    fun addAddress(customerId: Long, request: CreateAddressRequest): AddressDTO
    
    fun updateAddress(customerId: Long, addressId: Long, request: CreateAddressRequest): AddressDTO
    
    fun deleteAddress(customerId: Long, addressId: Long)
    
    fun setDefaultAddress(customerId: Long, addressId: Long): AddressDTO
    
    fun getAddresses(customerId: Long): List<AddressDTO>
    
    fun existsByEmail(email: String): Boolean
}
