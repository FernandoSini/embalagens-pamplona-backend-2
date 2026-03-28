package br.com.embalagenspamplona.loja.controller

import br.com.embalagenspamplona.loja.adapters.Mapper
import br.com.embalagenspamplona.loja.data.dto.ApiResponse
import br.com.embalagenspamplona.loja.data.dto.CategoryDTO
import br.com.embalagenspamplona.loja.data.dto.CategoryPaginationRequest
import br.com.embalagenspamplona.loja.data.dto.PagedResponse
import br.com.embalagenspamplona.loja.services.CategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/categories")
class CategoryController(private val categoryService: CategoryService) {


    @PostMapping("/create", consumes = ["application/json"], produces = ["application/json"])
    fun createCategory(@RequestBody category: CategoryDTO): ResponseEntity<ApiResponse<CategoryDTO>>{

        return ResponseEntity.ok(ApiResponse.success(CategoryDTO()))
    }

    @GetMapping("/")
    fun findAllCategories(
        @RequestParam(defaultValue = "0", required = false) page: Int,
        @RequestParam(defaultValue = "20", required = false) limit: Int,
        @RequestParam(required = false) search:String?
    ): ResponseEntity<PagedResponse<CategoryDTO>> {
        val categoryPaginationRequest = CategoryPaginationRequest(page, limit, search=search)
        val categoriesPaginationData = categoryService.getCategories(categoryPaginationRequest)
        val response = PagedResponse(
            result = categoriesPaginationData.content.map { e -> Mapper().mapTo(e, CategoryDTO::class.java) }
                .toMutableSet().toMutableList(),
            totalElements = categoriesPaginationData.totalElements,
            totalPages = categoriesPaginationData.totalPages,
            currentPage = categoriesPaginationData.number,
            size = categoriesPaginationData.size,
            hasNext = categoriesPaginationData.hasNext(),
            hasPrevious = categoriesPaginationData.hasPrevious(),
        )
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @GetMapping("/{id}")
    fun findCategoryById(@PathVariable("id") categoryId: Long): ResponseEntity<ApiResponse<CategoryDTO>> {
        try {

            val category = categoryService.getCategoryDetails(categoryId)
            val response = if (category == null) ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Categoria não encontrada!"))
            else ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(category, "Categoria carregada com sucesso!"))
            return response
        } catch (e: Exception) {
            throw e
        }

    }

    @PutMapping("/update")
    fun updateCategory(@RequestBody request: CategoryDTO): ResponseEntity<ApiResponse<String>> {
        try {
            val isUpdatedCategory = categoryService.update(request)
            if (isUpdatedCategory) {
                return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success("Categoria atualizada com sucesso!"))
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Categoria não encontrada!"))
            }
        } catch (e: Exception) {
            throw e
        }
    }
}