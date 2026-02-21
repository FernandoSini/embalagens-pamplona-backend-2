package br.com.embalagenspamplona.repository.datasource.local

import br.com.embalagenspamplona.data.entities.CouponEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

@Repository
interface CouponRepository : JpaRepository<CouponEntity, UUID> {
    
    fun findByCodeIgnoreCase(code: String): Optional<CouponEntity>
    
    fun findByActiveTrue(): List<CouponEntity>
    
    fun findByActiveTrueAndExpiresAtAfter(now: LocalDateTime): List<CouponEntity>
    
    fun existsByCodeIgnoreCase(code: String): Boolean
}
