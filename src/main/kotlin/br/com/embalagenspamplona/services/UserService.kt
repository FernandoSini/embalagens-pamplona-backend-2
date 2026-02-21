package br.com.embalagenspamplona.services

import br.com.embalagenspamplona.data.dto.AddressDTO
import br.com.embalagenspamplona.data.dto.CreateAddressRequest
import br.com.embalagenspamplona.data.dto.UserDTO
import br.com.embalagenspamplona.data.entities.UserEntity
import org.springframework.security.core.userdetails.UserDetails

interface UserService {
    fun createUser(userEntity: UserEntity): UserDTO?
    fun findById(userId: Long): UserDTO?
    fun loadUserByUsername(data:String?): UserDetails?
    fun findByEmail(email: String): UserDTO?
    fun update(userObject: UserDTO): Boolean
    fun deleteUser(userId: Long): Boolean
    fun deactivateAccount(userId: Long): Boolean

}