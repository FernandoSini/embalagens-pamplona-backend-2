package br.com.embalagenspamplona.repository.datasource.local

import br.com.embalagenspamplona.data.dto.PagedResponse
import br.com.embalagenspamplona.data.dto.ProductDTO
import br.com.embalagenspamplona.data.entities.CategoryEntity
import br.com.embalagenspamplona.data.entities.ProductEntity
import br.com.embalagenspamplona.data.entities.SegmentEntity
import br.com.embalagenspamplona.data.enums.ProductCategory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.UUID

@Repository
interface ProductRepository : JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {

    @Query("SELECT p from ProductEntity p INNER JOIN p.categoryEntity where p.categoryEntity.id = :categoryId")
    fun findProductEntityByCategoryEntityId(@Param("categoryId") id: Long, pageRequest: Pageable): Page<ProductDTO>

    @Query("SELECT p from ProductEntity p INNER JOIN p.categoryEntity c INNER JOIN c.segments s where s.id = :segmentId")
    fun findProductEntityByCategoryEntitySegments(
        @Param("segmentId") segmentID: Long,
        pageRequest: Pageable
    ): Page<ProductEntity>

    @Query("Select p from ProductEntity p INNER JOIN p.categoryEntity c where c IN :categories ")
    fun findProductEntitiesByCategories(@Param("categories") categories: List<CategoryEntity>): List<ProductEntity>


    @Query("SELECT p FROM ProductEntity p WHERE p.promotion.price IS NOT NULL")
    fun findPromotionalProducts(): List<ProductEntity>

    @Query("SELECT p FROM ProductEntity p WHERE p.name = :search or p.description = :search")
    fun searchByNameOrDescription(@Param("search") search: String, pageable: Pageable): Page<ProductEntity>

    fun findBySkuIgnoreCase(sku: String): ProductEntity?

    fun findByBarcodeIgnoreCase(barcode: String): ProductEntity?

    /* @Query("SELECT p FROM ProductEntity p WHERE p.quantity <= :threshold AND p.active = true")
     fun findLowStockProducts(threshold: Int): List<ProductEntity>*/

    /* @Query("SELECT p FROM ProductEntity p WHERE p.quantity = 0 AND p.active = true")
     fun findOutOfStockProducts(): List<ProductEntity>*/

    /*   @Query("SELECT MIN(p.price) FROM ProductEntity p WHERE p.active = true")
       fun findMinPrice(): BigDecimal?*/

    /* @Query("SELECT MAX(p.price) FROM ProductEntity p WHERE p.active = true")
     fun findMaxPrice(): BigDecimal?*/

    /* @Query("SELECT DISTINCT p.unit FROM ProductEntity p WHERE p.active = true")
     fun findDistinctUnits(): List<String>*/

    fun countBySegmentIdAndActiveTrue(segmentId: Long): Long

}
