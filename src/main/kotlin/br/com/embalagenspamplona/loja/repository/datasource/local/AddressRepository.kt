package br.com.embalagenspamplona.loja.repository.datasource.local

import br.com.embalagenspamplona.loja.data.entities.AddressEntity
import br.com.embalagenspamplona.loja.data.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.*

@Repository
interface AddressRepository : JpaRepository<AddressEntity, Long> {

    fun findAddressByCustomerId(customerId: Long): List<AddressEntity>

    fun findByCustomerIdAndIsDefaultTrue(customerId: Long): AddressEntity?

    @Modifying
    @Query("UPDATE AddressEntity a SET a.isDefault = false WHERE a.customer.id = :customerId")
    fun clearDefaultAddresses(customerId: Long)
    /*  fun findAddressEntityByCustomer_Id(customerId: Long): MutableList<AddressEntity>*/

    @Modifying
    @Query("DELETE AddressEntity a where a.id = :addressId AND a.customer.id =: customerId")
    fun deleteAddressByCustomerId(
        @Param("customerId") customerId: Long,
        @Param("addressId") addressId: Long
    ): Boolean

    @Modifying
    @Query("UPDATE AddressEntity a SET a.isDefault = true WHERE a.customer.id =: customerId AND a.id = :addressId")
    fun setDefaultAddress(@Param("customerId") customerId: Long, @Param("addressId") addressId: Long): Boolean

    @Modifying
    @Transactional
    @Query(
        "UPDATE AddressEntity a SET " +
        "a.street = '', " +
        "a.number = '', " +
        "a.complementNumber = null, " +
        "a.neighborhood = '', " +
        "a.city = '', " +
        "a.state = '', " +
        "a.zipCode = '', " +
        "a.reference = null, " +
        "a.label = null, " +
        "a.updatedAt = :now " +
        "WHERE a.customer.id = :userId"
    )
    fun anonymizeAddressesByUserId(@Param("userId") userId: Long, @Param("now") now: ZonedDateTime= ZonedDateTime.now())

}
