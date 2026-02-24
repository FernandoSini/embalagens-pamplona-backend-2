package br.com.embalagenspamplona.loja.data.dto

import java.math.BigDecimal

data class CatalogDTO(
    val categories: MutableSet<CategoryDTO> = mutableSetOf<CategoryDTO>(),
    val featuredProducts: List<ProductDTO> = emptyList(),
    val promotionalProducts: List<ProductDTO> = emptyList()
)

data class SegmentWithProductsDTO(
    val segment: SegmentDTO,
    val products: List<ProductDTO> = emptyList(),
    val totalProducts: Int = 0
)

data class CatalogSearchResult(
    val products: List<ProductDTO>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)

data class PriceRange(
    val min: BigDecimal,
    val max: BigDecimal
)

data class CatalogFilters(
    val segments: List<SegmentDTO>,
    val priceRange: PriceRange,
    val units: List<String>
)
