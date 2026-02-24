/*
package br.com.embalagenspamplona.loja.repository.datasource.local

import br.com.embalagenspamplona.loja.data.entities.PaymentEntity
import br.com.embalagenspamplona.loja.data.enums.PaymentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.Optional
import java.util.UUID

@Repository
interface PaymentRepository : JpaRepository<PaymentEntity, Long> {
    
    */
/*fun findByOrderId(orderId: Long): Optional<PaymentEntity>*//*

    
    fun findByTransactionId(transactionId: String): Optional<PaymentEntity>
    
    fun findByExternalReference(externalReference: String): Optional<PaymentEntity>
    
    fun findByStatus(status: PaymentStatus): List<PaymentEntity>
    
    fun findByStatusAndCreatedAtBefore(status: PaymentStatus, createdAt: ZonedDateTime): List<PaymentEntity>
}
*/
