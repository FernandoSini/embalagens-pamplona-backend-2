package br.com.embalagenspamplona.repository.datasource.local

import br.com.embalagenspamplona.data.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {
    
    fun findByEmail(email: String): Optional<UserEntity>
    
    fun findByEmailAndActiveTrue(email: String): Optional<UserEntity>
    
    fun existsByEmail(email: String): Boolean
    
    fun findByCpf(cpf: String): Optional<UserEntity>
    
    fun findByCnpj(cnpj: String): Optional<UserEntity>
    
    @Query("SELECT c FROM UserEntity c LEFT JOIN FETCH c.addresses WHERE c.id = :id")
    fun findByIdWithAddresses(id: UUID): UserEntity?
    
    @Query("SELECT c FROM UserEntity c LEFT JOIN FETCH c.cart WHERE c.id = :id")
    fun findByIdWithCart(id: UUID): UserEntity?
    
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
        "u.updatedAt = CURRENT_TIMESTAMP " +
        "WHERE u.id = :userId"
    )
    fun deleteByUserId(@Param("userId") userId: Long): Boolean

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity  u set u.active = false")
    fun deactivateUser(@Param("userId") userId:Long): Boolean

    @Query("SELECT u from UserEntity u WHERE u.email =: data OR u.name =: data")
    fun findUserEntityByEmailOrName(@Param("userInfo") data:String): Optional<UserEntity>
}
