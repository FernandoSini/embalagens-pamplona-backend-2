package br.com.embalagenspamplona.repository.datasource.local

import br.com.embalagenspamplona.data.entities.CartItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface CartItemRepository : JpaRepository<CartItemEntity, Long> {
    @Query("Select c from CartItemEntity c INNER JOIN c.cart where c.cart.id = :cartId")
    fun findByCartId(@Param("cartId") cartId: Long): List<CartItemEntity>

    fun findByCartIdAndProductId(cartId: Long, productId: Long): Optional<CartItemEntity>

    @Modifying
    @Query("DELETE FROM CartItemEntity ci WHERE ci.cart.id = :cartId")
    fun deleteAllByCartId(cartId: Long)

    fun countByCartId(cartId: Long): Long
}
