package br.com.embalagenspamplona.loja.repository.datasource.local

import br.com.embalagenspamplona.loja.data.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.Optional
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {

    @Query("SELECT u from UserEntity u where u.email = :email")
    fun findByEmail(@Param("email") email: String): Optional<UserEntity>
    
    fun findByEmailAndActiveTrue(email: String): Optional<UserEntity>
    
    fun existsByEmail(email: String): Boolean
    

    fun findByCpfCnpj(cpfCnpj: String): Optional<UserEntity>
    
    @Query("SELECT c FROM UserEntity c LEFT JOIN FETCH c.addresses WHERE c.id = :id")
    fun findByIdWithAddresses(id: Long): UserEntity?
    
    @Query("SELECT c FROM UserEntity c LEFT JOIN FETCH c.cart WHERE c.id = :id")
    fun findByIdWithCart(id: Long): UserEntity?
    
    fun findByActiveTrue(): List<UserEntity>

    @Modifying
    @Transactional
    @Query(
        "UPDATE UserEntity u SET u.active = false, " +
        "u.name = '', " +
        "u.lastName = '', " +
        "u.email = CONCAT('deleted_', :userId), " +
        "u.gender = '', " +
        "u.birthday = '', " +
        "u.password = '', " +
        "u.phone = null, " +
        "u.cpfCnpj = null, " +
        "u.updatedAt = :now " +
        "WHERE u.id = :userId"
    )
    fun deleteByUserId(@Param("userId") userId: Long, @Param("now") now: ZonedDateTime = ZonedDateTime.now()): Boolean

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity  u set u.active = false WHERE u.id = :userId")
    fun deactivateUser(@Param("userId") userId:Long): Boolean

    @Query("SELECT u from UserEntity u WHERE u.email = :userInfo OR u.name = :userInfo")
    fun findUserEntityByEmailOrName(@Param("userInfo") userInfo:String): Optional<UserEntity>
}
