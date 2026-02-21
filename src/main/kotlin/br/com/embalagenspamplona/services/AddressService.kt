package br.com.embalagenspamplona.services

import br.com.embalagenspamplona.data.dto.AddressDTO
import br.com.embalagenspamplona.data.dto.CreateAddressRequest

interface AddressService {
    fun getCurrentAddress(addressId:Long): AddressDTO
    fun getUserAddresses(userId:Long): MutableList<AddressDTO>
    fun updateUserAddress(userId:Long, updatedAddress: AddressDTO): AddressDTO?
    fun addAddress(createAddress: CreateAddressRequest, userId: Long): AddressDTO
    fun deleteAddress(customerId: Long, addressId: Long): Boolean
    fun setDefaultAddress(customerId:Long, addressId: Long): Boolean
}