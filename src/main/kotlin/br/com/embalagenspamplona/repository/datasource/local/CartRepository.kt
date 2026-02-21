package br.com.embalagenspamplona.repository.datasource.local

import br.com.embalagenspamplona.data.entities.CartEntity
import br.com.embalagenspamplona.data.entities.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface CartRepository : JpaRepository<CartEntity, Long> {

    @Query("SELECT c from CartEntity c WHERE c.userEntity.id = :userId")
    fun findByUserEntityId(@Param("userId") userId: Long): Optional<CartEntity>

    @Query("SELECT c FROM CartEntity c LEFT JOIN FETCH c.items ci LEFT JOIN FETCH ci.product WHERE c.userEntity.id = :userId")
    fun findByUserEntityIdWithItems(@Param("userId") userId: Long): CartEntity?

    fun deleteByUserEntityId(userId: Long)
    fun order(order: OrderEntity): MutableList<CartEntity>
}
