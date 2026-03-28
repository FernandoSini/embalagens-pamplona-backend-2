package br.com.embalagenspamplona.loja.services.impl

import br.com.embalagenspamplona.loja.adapters.Mapper
import br.com.embalagenspamplona.loja.data.dto.CreateAddressRequest
import br.com.embalagenspamplona.loja.data.dto.UserDTO
import br.com.embalagenspamplona.loja.data.entities.AddressEntity
import br.com.embalagenspamplona.loja.data.entities.UserEntity
import br.com.embalagenspamplona.loja.exceptions.BadRequestException
import br.com.embalagenspamplona.loja.exceptions.NotFoundException
import br.com.embalagenspamplona.loja.repository.datasource.local.AddressRepository
import br.com.embalagenspamplona.loja.repository.datasource.local.UserRepository
import br.com.embalagenspamplona.loja.services.UserService
import org.hibernate.annotations.NotFound
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service("userDetailsService")
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService, UserService {

    /* @Autowired
     private lateinit var userRepository: UserRepository*/

    override fun loadUserByUsername(data: String?): UserDetails? {
        try {
            if (data.isNullOrEmpty()) {
                return null
            } else {
                val user = userRepository.findUserEntityByEmailOrName(data).orElseThrow {
                    throw NotFoundException(Exception("Usuário não encontrado com esse email"))
                }
                return User.builder().username(user.email)
                    .password(user.password)
                    .roles(user.authorities.first()?.authority) //investigar o porque isso assim funciona e ter que ajustar pra receber uma lista de strings
                    // mas baseado na nossa regra de negocios ele so vai poder ter 1role
                    .build()
            }
        } catch (e: Exception) {

            throw e
        }

    }

    override fun findById(userId: Long): UserDTO {
        TODO("Not yet implemented")
    }

    override fun findByEmail(email: String): UserDTO? {
        val user = userRepository.findByEmail(email).orElseThrow {
            throw NotFoundException(Exception("User not found Exception!"))
        }
        val mappedUser = Mapper().mapTo(user, UserDTO::class.java)
        return mappedUser
    }

    override fun findByEmailOrName(emailOrName: String): UserDTO? {
        val user = userRepository.findUserEntityByEmailOrName(emailOrName).orElseThrow {
            throw NotFoundException(Exception("User not found Exception!"))
        }
        return Mapper().mapTo(user, UserDTO::class.java)
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
        val existUser = userRepository.findByEmail(userEntity.email)
        if (existUser.isEmpty || !existUser.isPresent) {


            val createdUser = userRepository.save(userEntity)

            return Mapper().mapTo(createdUser, UserDTO::class.java)
            //return createdUser
        } else {
            return null
        }
    }

    override fun findCustomers(): MutableSet<UserDTO> {
        val users = userRepository.findUserByRole(2)
        val userList = users.map { e-> Mapper().mapTo(e, UserDTO::class.java) }.toMutableSet()
        if(userList.isEmpty()){
            throw NotFoundException(Exception("Usuários não encontrados!"))
        }
        return userList

    }
}
