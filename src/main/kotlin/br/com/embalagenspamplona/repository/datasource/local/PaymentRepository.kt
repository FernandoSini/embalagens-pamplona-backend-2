package br.com.embalagenspamplona.repository.datasource.local

import br.com.embalagenspamplona.data.entities.PaymentEntity
import br.com.embalagenspamplona.data.enums.PaymentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

@Repository
interface PaymentRepository : JpaRepository<PaymentEntity, Long> {
    
    fun findByOrderId(orderId: Long): Optional<PaymentEntity>
    
    fun findByTransactionId(transactionId: String): Optional<PaymentEntity>
    
    fun findByExternalReference(externalReference: String): Optional<PaymentEntity>
    
    fun findByStatus(status: PaymentStatus): List<PaymentEntity>
    
    fun findByStatusAndCreatedAtBefore(status: PaymentStatus, createdAt: LocalDateTime): List<PaymentEntity>
}
