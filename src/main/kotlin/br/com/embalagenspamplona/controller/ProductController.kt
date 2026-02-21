package br.com.embalagenspamplona.controller

import br.com.embalagenspamplona.adapters.Mapper
import br.com.embalagenspamplona.data.dto.*
import br.com.embalagenspamplona.exceptions.BadRequestException
import br.com.embalagenspamplona.services.ProductService
import br.com.embalagenspamplona.services.R2Service
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.Map
import java.util.UUID

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Produtos", description = "Gerenciamento de produtos")
class ProductController(
    private val productService: ProductService,
    private val r2Service: R2Service
) {
    @PostMapping("/upload-url")
    fun getUploadUrl(@RequestParam originalName: String?): ResponseEntity<ApiResponse<MutableMap<String, String>>> {
        if (!originalName.isNullOrBlank()) {
            var extension: String = "";
            if (originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }
            val safeName: String = UUID.randomUUID().toString() + extension;
            val url: String = r2Service.generatePresignedUploadUrl(safeName) ?: ""

            return ResponseEntity.ok<ApiResponse<MutableMap<String, String>>>(
                ApiResponse.success(Map.of<String, String>("uploadUrl", url)))
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Nome do arquivo inválido"))
        }
    }

    @GetMapping
    @Operation(summary = "Listar produtos com paginação e filtros")
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) segmentId: UUID?,
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) minPrice: BigDecimal?,
        @RequestParam(required = false) maxPrice: BigDecimal?,
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
        val products = productService.findAll(filter)
        return ResponseEntity.ok(ApiResponse.success(products))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID")
    fun findById(@PathVariable id: String): ResponseEntity<ApiResponse<ProductDTO>> {
        val product = productService.findById(id.toLong())
        return ResponseEntity.ok(ApiResponse.success(product))
    }

    @GetMapping("/segment/{segmentId}")
    @Operation(summary = "Listar produtos por segmento")
    fun findBySegmentId(
        @PathVariable segmentId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<PagedResponse<ProductDTO>>> {
        val products = productService.findBySegmentId(segmentId, page, size)
        return ResponseEntity.ok(ApiResponse.success(products))
    }


    @GetMapping("/search")
    @Operation(summary = "Buscar produtos por nome ou descrição")
    fun search(
        @RequestParam query: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<PagedResponse<ProductDTO>>> {
        val products = productService.search(query, page, size)
        return ResponseEntity.ok(ApiResponse.success(products))
    }

    @PostMapping("/create")
    @Operation(summary = "Criar novo produto")
    fun create(@RequestBody request: ProductDTO): ResponseEntity<ApiResponse<Any>> {
        val dto= Mapper().mapTo(request::class.java, ProductDTO::class.java )
        if (dto==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Erro na conversao de dados"))
        }
        val product = productService.create(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(product, "Produto criado com sucesso"))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto")
    fun update(
        @PathVariable id: String,
        @RequestBody request: ProductDTO
    ): ResponseEntity<ApiResponse<ProductDTO>> {
        val dto= Mapper().mapTo(request::class.java, ProductDTO::class.java )
        if (dto==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Erro na conversao de dados"))
        }
        if(request.price >= request.promotion?.price){
            throw BadRequestException(Exception("O preço da promoção não pode ser maior ou igual ao valor do produto"))
        }
        val product = productService.update( dto)
        return ResponseEntity.ok(ApiResponse.success(product, "Produto atualizado com sucesso"))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir produto")
    fun delete(@PathVariable id: String): ResponseEntity<ApiResponse<Unit>> {
        productService.delete(id.toLong())
        return ResponseEntity.ok(ApiResponse.success(Unit, "Produto excluído com sucesso"))
    }

   /* @PatchMapping("/{id}/stock")
    @Operation(summary = "Atualizar estoque do produto")
    fun updateStock(
        @PathVariable id: String,
        @RequestParam quantity: Int
    ): ResponseEntity<ApiResponse<ProductDTO>> {
        val product = productService.updateStock(id, quantity)
        return ResponseEntity.ok(ApiResponse.success(product, "Estoque atualizado com sucesso"))
    }*/


}
