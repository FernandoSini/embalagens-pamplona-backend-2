package br.com.embalagenspamplona.loja.controller

import br.com.embalagenspamplona.loja.data.dto.*
import br.com.embalagenspamplona.loja.services.CatalogService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.UUID
import javax.swing.text.Segment

@RestController
@RequestMapping("/api/v1/catalog",)
@Tag(name = "Catálogo", description = "Acesso ao catálogo de produtos")
class CatalogController(
    private val catalogService: CatalogService
) {

    @GetMapping("/")
    @Operation(summary = "Obter catálogo completo (segmentos, destaques e promoções)")
    fun getCatalog(
        @RequestBody(required = false) selectedCategory: CategoryDTO?,
        @RequestParam(name = "page", defaultValue = "0") page:Int,
        @RequestParam(name = "size", defaultValue = "5") size: Int
    ): ResponseEntity<ApiResponse<MutableSet<ProductDTO>>> {
        val catalog = catalogService.getCatalog(selectedCategory, page,size)
        return ResponseEntity.ok(ApiResponse.success(catalog.result.toMutableSet()))
    }



    @GetMapping("/search")
    @Operation(summary = "Buscar produtos no catálogo")
    fun searchProducts(
        @RequestParam(required = false) segmentId: Long?,
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) minPrice: BigDecimal?,
        @RequestParam(required = false) maxPrice: BigDecimal?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "name") sortBy: String,
        @RequestParam(defaultValue = "ASC") sortDirection: String
    ): ResponseEntity<ApiResponse<PagedResponse<ProductDTO>>> {
        val filter = ProductFilterRequest(
            search = search,
            minPrice = minPrice,
            maxPrice = maxPrice,
            page = page,
            size = size,
            sortBy = sortBy,
            sortDirection = sortDirection
        )
        val result = catalogService.searchProducts(filter)
        return ResponseEntity.ok(ApiResponse.success(result))
    }


    

}
