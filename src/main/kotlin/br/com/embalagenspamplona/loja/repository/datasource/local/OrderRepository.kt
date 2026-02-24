package br.com.embalagenspamplona.loja.repository.datasource.local

import br.com.embalagenspamplona.loja.data.entities.CartEntity
import br.com.embalagenspamplona.loja.data.entities.OrderEntity
import br.com.embalagenspamplona.loja.data.enums.OrderStatus
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OrderRepository : JpaRepository<OrderEntity, Long> {
    
    fun findByUserEmail(email: String, pageable: Pageable): Page<OrderEntity>
    
    fun findByUserEmailAndStatus(email: String, status: OrderStatus, pageable: Pageable): Page<OrderEntity>
    
    fun findByStatus(status: OrderStatus, pageable: Pageable): Page<OrderEntity>
    
    @Query("SELECT o FROM OrderEntity o WHERE o.date BETWEEN :startDate AND :endDate")
    fun findByDateRange(startDate: LocalDateTime, endDate: LocalDateTime, pageable: Pageable): Page<OrderEntity>

    fun findOrderEntityByOrderNumber(orderNumber: Long): OrderEntity
    @Transactional
    @Modifying
    @Query("UPDATE OrderEntity o SET o.status = :orderStatus where o.id = :orderId")
    fun updateOrderStatus(@Param("orderId") orderId:Long,@Param("orderStatus") orderStatus: OrderStatus)

    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.id = :id")
    fun findByIdWithItems(id: Long): OrderEntity?

    fun countByStatus(status: OrderStatus): Long
    
    @Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.createdAt >= :startDate")
    fun countOrdersSince(startDate: LocalDateTime): Long
    fun cart(cart: CartEntity): MutableList<OrderEntity>
}
