package br.com.embalagenspamplona.loja.services.impl

import br.com.embalagenspamplona.loja.data.entities.CouponEntity
import br.com.embalagenspamplona.loja.repository.datasource.local.CouponRepository
import br.com.embalagenspamplona.loja.services.CouponService
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class CouponServiceImpl(
    private val couponRepository: CouponRepository
) : CouponService {

    override fun findByCode(code: String): CouponEntity {
        return couponRepository.findByCodeIgnoreCase(code)
            .orElseThrow { EntityNotFoundException("Cupom não encontrado: $code") }
    }

    override fun validateCoupon(code: String, orderValue: BigDecimal): Boolean {
        val coupon = couponRepository.findByCodeIgnoreCase(code).orElse(null) ?: return false
        
        /*if (!coupon.isValid()) {
            return false
        }*/

        if (coupon.minOrderValue != null && orderValue < coupon.minOrderValue) {
            return false
        }

        return true
    }

    override fun calculateDiscount(code: String, orderValue: BigDecimal): BigDecimal {
        val coupon = findByCode(code)

        if (!validateCoupon(code, orderValue)) {
            return BigDecimal.ZERO
        }

        var discount = when (coupon.discountType.uppercase()) {
            "PERCENTAGE" -> {
                orderValue.multiply(coupon.discountValue)
                    .divide(BigDecimal(100), 2, RoundingMode.HALF_UP)
            }
            "FIXED" -> coupon.discountValue
            else -> BigDecimal.ZERO
        }

        // Aplicar desconto máximo se definido
        if (coupon.maxDiscount != null && discount > coupon.maxDiscount) {
            discount = coupon.maxDiscount
        }

        // O desconto não pode ser maior que o valor do pedido
        if (discount > orderValue) {
            discount = orderValue
        }

        return discount
    }

    override fun applyCoupon(code: String): CouponEntity {
        val coupon = findByCode(code)
        
       /* if (!coupon.isValid()) {
            throw IllegalArgumentException("Cupom inválido ou expirado")
        }
*/
        return coupon
    }

    override fun findAll(): List<CouponEntity> {
        return couponRepository.findAll()
    }

    override fun findActive(): List<CouponEntity> {
        return couponRepository.findByActiveTrueAndExpiresAtAfter(LocalDateTime.now())
    }

    override fun create(coupon: CouponEntity): CouponEntity {
        if (couponRepository.existsByCodeIgnoreCase(coupon.code)) {
            throw IllegalArgumentException("Já existe um cupom com este código")
        }
        return couponRepository.save(coupon)
    }

    override fun update(id: Long, coupon: CouponEntity): CouponEntity {
        val existingCoupon = couponRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Cupom não encontrado: $id") }

        val updatedCoupon = existingCoupon.copy(
            code = coupon.code,
            description = coupon.description,
            discountType = coupon.discountType,
            discountValue = coupon.discountValue,
            minOrderValue = coupon.minOrderValue,
            maxDiscount = coupon.maxDiscount,
            usageLimit = coupon.usageLimit,
            usagePerCustomer = coupon.usagePerCustomer,
            active = coupon.active,
            startsAt = coupon.startsAt,
            expiresAt = coupon.expiresAt,
            updatedAt = LocalDateTime.now()
        )

        return couponRepository.save(updatedCoupon)
    }

    override fun delete(id: Long) {
        if (!couponRepository.existsById(id)) {
            throw EntityNotFoundException("Cupom não encontrado: $id")
        }
        couponRepository.deleteById(id)
    }

    override fun incrementUsage(code: String) {
        val coupon = findByCode(code)
        couponRepository.save(coupon.copy(
            usageCount = coupon.usageCount + 1,
            updatedAt = LocalDateTime.now()
        ))
    }
}
