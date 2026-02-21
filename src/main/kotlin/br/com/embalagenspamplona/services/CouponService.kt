package br.com.embalagenspamplona.services

import br.com.embalagenspamplona.data.entities.CouponEntity
import java.math.BigDecimal
import java.util.UUID

interface CouponService {
    
    fun findByCode(code: String): CouponEntity
    
    fun validateCoupon(code: String, orderValue: BigDecimal): Boolean
    
    fun calculateDiscount(code: String, orderValue: BigDecimal): BigDecimal
    
    fun applyCoupon(code: String): CouponEntity
    
    fun findAll(): List<CouponEntity>
    
    fun findActive(): List<CouponEntity>
    
    fun create(coupon: CouponEntity): CouponEntity
    
    fun update(id: UUID, coupon: CouponEntity): CouponEntity
    
    fun delete(id: UUID)
    
    fun incrementUsage(code: String)
}
