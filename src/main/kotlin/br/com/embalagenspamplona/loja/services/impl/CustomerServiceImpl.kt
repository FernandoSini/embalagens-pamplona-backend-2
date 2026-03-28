package br.com.embalagenspamplona.loja.services.impl

import br.com.embalagenspamplona.loja.data.dto.AddressDTO
import br.com.embalagenspamplona.loja.data.dto.CreateAddressRequest
import br.com.embalagenspamplona.loja.data.dto.CreateCustomerRequest
import br.com.embalagenspamplona.loja.data.dto.CustomerDTO
import br.com.embalagenspamplona.loja.data.dto.UserDTO
import br.com.embalagenspamplona.loja.exceptions.NotFoundException
import br.com.embalagenspamplona.loja.services.CustomerService
import br.com.embalagenspamplona.loja.services.UserService
import org.springframework.stereotype.Service


@Service
class CustomerServiceImpl(private val userService: UserService): CustomerService {
   // @Throws(NotFoundException::class)
    override fun findById(id: Long): UserDTO {
       val user= userService.findById(id)
        if(user!=null){
            return user;
        }else{
            throw NotFoundException(Exception("Usuário não encontrado!"))
        }
    }

    override fun findByEmail(email: String): CustomerDTO {
        TODO("Not yet implemented")
    }



    override fun update(
        id: Long,
        request: UserDTO
    ): UserDTO {
        TODO("Not yet implemented")
    }

    override fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override fun addAddress(
        customerId: Long,
        request: CreateAddressRequest
    ): AddressDTO {
        TODO("Not yet implemented")
    }

    override fun updateAddress(
        customerId: Long,
        addressId: Long,
        request: CreateAddressRequest
    ): AddressDTO {
        TODO("Not yet implemented")
    }

    override fun deleteAddress(customerId: Long, addressId: Long) {
        TODO("Not yet implemented")
    }

    override fun setDefaultAddress(
        customerId: Long,
        addressId: Long
    ): AddressDTO {
        TODO("Not yet implemented")
    }

    override fun getAddresses(customerId: Long): List<AddressDTO> {
        TODO("Not yet implemented")
    }

    override fun existsByEmail(email: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getCustomers(): List<UserDTO> {
       val list = userService.findCustomers()
        return list.toList();
    }
}