package br.com.embalagenspamplona.data.dto

import br.com.embalagenspamplona.data.entities.ImageEntity
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

data class ProductDTO(
    val id: Long? = null,
    val name: String="",
    val description: String? = null,
    val price: BigDecimal = BigDecimal.ZERO,
    val promotion: PromotionDTO? = null,
    val quantity: Int = 0,
    val sku: String? = null,
    val createdAt: ZonedDateTime? = null,
    val updatedAt: ZonedDateTime? = null,
    val images: List<ImageDTO> = emptyList(),
    val pack: String? = null,
    //  val barcode: String? = null,
    /* val active: Boolean = true,
     val featured: Boolean = false,*/
    /* val minQuantity: Int = 1,
     val maxQuantity: Int? = null,*/
   // val unit: String = "UN",

    /* val weightGrams: Int? = null,
     val segmentId: UUID? = null,
     val segmentName: String? = null,*/
) {
    /*val effectivePrice: BigDecimal
        get() = promoPrice ?: price

    val hasPromotion: Boolean
        get() = promoPrice != null && promoPrice < price*/
}

data class CreateProductRequest(
    val name: String,
    val description: String? = null,
    val price: BigDecimal,
    val promotionalPrice: BigDecimal? = null,
    val stock: Int = 0,
    val sku: String? = null,
    val barcode: String? = null,
/*    val imageUrl: String? = null,
    val additionalImages: List<String> = emptyList(),*/
    val featured: Boolean = false,
    val minQuantity: Int = 1,
    val maxQuantity: Int? = null,
    val unit: String = "UN",
    val pack: String? = null,
    val weightGrams: Int? = null,
    val segmentId: UUID? = null
)

data class UpdateProductRequest(
    val name: String? = null,
    val description: String? = null,
    val price: BigDecimal? = null,
    val promotionalPrice: BigDecimal? = null,
    val stock: Int? = null,
    val sku: String? = null,
    val barcode: String? = null,
/*    val imageUrl: String? = null,
    val additionalImages: List<String>? = null,*/
    val active: Boolean? = null,
    val featured: Boolean? = null,
    val minQuantity: Int? = null,
    val maxQuantity: Int? = null,
    val unit: String? = null,
    val pack: String? = null,
    val weightGrams: Int? = null,
    val segmentId: UUID? = null
)

data class ProductFilterRequest(
    val segmentId: Long? = 0L,
    val search: String? = null,
    val minPrice: BigDecimal? = null,
    val maxPrice: BigDecimal? = null,
    val page: Int = 0,
    val size: Int = 20,
    val sortBy: String = "name",
    val sortDirection: String = "ASC"
)
