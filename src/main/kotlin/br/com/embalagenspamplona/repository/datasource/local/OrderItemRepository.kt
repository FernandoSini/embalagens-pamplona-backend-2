package br.com.embalagenspamplona.repository.datasource.local

import br.com.embalagenspamplona.data.entities.OrderItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderItemRepository : JpaRepository<OrderItemEntity, Long> {
    
    fun findByOrderId(orderId: Long): List<OrderItemEntity>
}
