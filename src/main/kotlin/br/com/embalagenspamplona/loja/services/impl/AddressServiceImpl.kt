package br.com.embalagenspamplona.loja.services.impl

import br.com.embalagenspamplona.loja.adapters.Mapper
import br.com.embalagenspamplona.loja.data.dto.AddressDTO
import br.com.embalagenspamplona.loja.data.dto.CreateAddressRequest
import br.com.embalagenspamplona.loja.data.entities.AddressEntity
import br.com.embalagenspamplona.loja.data.entities.UserEntity
import br.com.embalagenspamplona.loja.exceptions.InternalServerException
import br.com.embalagenspamplona.loja.exceptions.NotFoundException
import br.com.embalagenspamplona.loja.repository.datasource.local.AddressRepository
import br.com.embalagenspamplona.loja.repository.datasource.local.UserRepository
import br.com.embalagenspamplona.loja.services.AddressService
import org.apache.commons.lang3.mutable.Mutable
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class AddressServiceImpl(
    private val addressRepository: AddressRepository,
    private val userRepository: UserRepository
) :
    AddressService {
    override fun getCurrentAddress(addressId: Long): AddressDTO {
        val address = addressRepository.findById(addressId)
            .orElseThrow { throw NotFoundException(Exception("Endereço não encontrado")) }
        val dto = Mapper().mapTo(address::class.java, AddressDTO::class.java)
        if (dto == null) throw InternalServerException(Exception("Erro na conversão dos dados do endereço atual!"))
        return dto
    }

    override fun getUserAddresses(userId: Long): MutableList<AddressDTO> {
        val addressesEntity = addressRepository.findAddressByCustomerId(userId).toMutableList()
        val mappedAddresses = addressesEntity.mapNotNull { e ->
            Mapper().mapTo(e::class.java, AddressDTO::class.java)
        }.toMutableList()
        if (mappedAddresses.isEmpty()) throw InternalServerException(Exception("erro na conversão dos dados de endereço"))
        return mappedAddresses
    }

    override fun updateUserAddress(userId: Long, updatedAddress: AddressDTO): AddressDTO {
        val address = addressRepository.findById(updatedAddress.id).orElseThrow {
            throw NotFoundException(Exception("Address Not found"))
        }
        address.city = updatedAddress.city
        address.zipCode = updatedAddress.zipCode
        address.state = updatedAddress.state
        address.neighborhood = updatedAddress.neighborhood
        if (updatedAddress.complementNumber != null) {
            address.complementNumber = updatedAddress.complementNumber
        }
        address.number = updatedAddress.number
        if (updatedAddress.reference != null) {
            address.reference = updatedAddress.reference
        }
        address.street = updatedAddress.street
        val savedEntity = addressRepository.save(address)
        val dto = Mapper().mapTo(savedEntity, AddressDTO::class.java)
        if (dto == null) throw InternalServerException(Exception("Os dados foram atualizados mas houve um erro na conversão"))
        return dto
    }

    override fun addAddress(
        createAddress: CreateAddressRequest,
        userId: Long
    ): AddressDTO {
        val userEntity = userRepository.findById(userId)
            .orElseThrow { throw NotFoundException(Exception("Usuário não encontrado para atualizar endereço")) }

        val address = AddressEntity(
            city = createAddress.city,
            street = createAddress.street,
            state = createAddress.state,
            neighborhood = createAddress.neighborhood,
            number = createAddress.number,
            complementNumber = createAddress.complementNumber,
            zipCode = createAddress.zipCode,
            createdAt = ZonedDateTime.now(),
            customer = userEntity
        )
        val savedEntity = addressRepository.save(address)
        val dto = Mapper().mapTo(savedEntity::class.java, AddressDTO::class.java)
        if (dto == null) throw InternalServerException(Exception("Os dados de endereço foram salvos mas houve um erro ao convertê-los"))
        return dto

    }

    override fun deleteAddress(customerId: Long, addressId: Long): Boolean {
        val deleted = addressRepository.deleteAddressByCustomerId(customerId = customerId, addressId = addressId)
        return deleted
    }

    override fun setDefaultAddress(customerId: Long, addressId: Long): Boolean {
        val isDefault = addressRepository.setDefaultAddress(customerId, addressId)
        return isDefault
    }
}