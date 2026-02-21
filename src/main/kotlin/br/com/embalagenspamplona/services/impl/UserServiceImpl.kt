package br.com.embalagenspamplona.services.impl

import br.com.embalagenspamplona.adapters.Mapper
import br.com.embalagenspamplona.data.dto.CreateAddressRequest
import br.com.embalagenspamplona.data.dto.UserDTO
import br.com.embalagenspamplona.data.entities.AddressEntity
import br.com.embalagenspamplona.data.entities.UserEntity
import br.com.embalagenspamplona.exceptions.BadRequestException
import br.com.embalagenspamplona.exceptions.NotFoundException
import br.com.embalagenspamplona.repository.datasource.local.AddressRepository
import br.com.embalagenspamplona.repository.datasource.local.UserRepository
import br.com.embalagenspamplona.services.UserService
import org.hibernate.annotations.NotFound
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service("userDetailsService")
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val addressRepository: AddressRepository,
) : UserDetailsService, UserService {

    override fun loadUserByUsername(data: String?): UserDetails? {
        try {
            if (data.isNullOrEmpty()) {
                return null
            } else {
                val user = userRepository.findUserEntityByEmailOrName(data).orElseThrow {
                    NotFoundException(Exception("Usuário não encontrado com esse email"))
                }
                return User.builder().username(user.name ?: user.email)
                    .password(user.password)
                    .roles(user.authorities.toString())
                    .build()
            }
        } catch (e: Exception) {

            throw NotFoundException(Exception())
        }

    }

    override fun findById(userId: Long): UserDTO {
        TODO("Not yet implemented")
    }

    override fun findByEmail(email: String): UserDTO? {
        val user = userRepository.findByEmail(email).orElseThrow {
            throw NotFoundException(Exception("User not found Exception!"))
        }
        val mappedUser = Mapper().mapTo(user::class.java, UserDTO::class.java)
        return mappedUser

    }

    override fun update(userObject: UserDTO): Boolean {
        val userUpdated = userRepository.findById(userObject.id!!)
            .orElseThrow { throw NotFoundException(Exception("Usuário não encontrado com esse id!")) }

        if (userObject.email != null && userObject.email.isNotEmpty()) {
            userUpdated.email = userObject.email
        }
        if (userObject.name != null && userObject.name.isNotEmpty()) {
            userUpdated.name = userObject.name
        }
        if (userObject.cpfCnpj != null && userObject.cpfCnpj.isNotEmpty()) {
            userUpdated.cpfCnpj = userObject.cpfCnpj
        }
        if (userObject.phone != null && userObject.phone.isNotEmpty()) {
            userUpdated.phone = userObject.phone
        }
        if (userObject.birthday != null && userObject.birthday.isNotEmpty()) {
            userUpdated.birthday = userObject.birthday
        }
        if (userObject.gender != null && userObject.gender.isNotEmpty()) {
            userUpdated.gender = userObject.gender
        }
        if (userObject.lastName != null && userObject.lastName.isNotEmpty()) {
            userUpdated.lastName = userObject.lastName
        }

        return userRepository.save(userUpdated) != null


    }

    override fun deleteUser(userId: Long): Boolean {
        val isDeleted = userRepository.deleteByUserId(userId)
        return isDeleted
    }

    override fun deactivateAccount(userId: Long): Boolean {
        // addressRepository.anonymizeAddressesByUserId(userId)
        //val isDeleted = userRepository.deleteByUserId(userId)
        val isDeactivated = userRepository.deactivateUser(userId)
        return isDeactivated
    }

    override fun createUser(userEntity: UserEntity): UserDTO? {
        val existUser = userRepository.findByEmail(userEntity.email.toString())
        if (existUser.isEmpty || !existUser.isPresent) {


            val createdUser = userRepository.save(userEntity)
            return Mapper().mapTo(createdUser::class, UserDTO::class.java)
        } else {
            throw BadRequestException(Exception("Usuário já existente"))
        }
    }


}
