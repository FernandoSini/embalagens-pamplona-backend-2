package br.com.embalagenspamplona.services.impl

import br.com.embalagenspamplona.data.dto.*
import br.com.embalagenspamplona.data.entities.CategoryEntity
import br.com.embalagenspamplona.data.entities.SegmentEntity
import br.com.embalagenspamplona.repository.datasource.local.CategoryRepository
import br.com.embalagenspamplona.repository.datasource.local.ProductRepository
import br.com.embalagenspamplona.repository.datasource.local.SegmentRepository
import br.com.embalagenspamplona.services.CatalogService
import br.com.embalagenspamplona.services.ProductService
import org.springframework.data.domain.PageRequest

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional(readOnly = true)
class CatalogServiceImpl(
    private val productService: ProductService,
    private val categoryRepository: CategoryRepository
) : CatalogService {

    override fun getSegmentCatalog(selectedSegment: SegmentDTO, page: Int, size: Int): PagedResponse<ProductDTO> {
        val pageRequest = PageRequest.of(page, size)
        val pagedResult =
            productService.findBySegmentId(selectedSegment.id, pageRequest.pageNumber, pageRequest.pageSize)
        return PagedResponse(
            content = pagedResult.content,
            totalPages = pagedResult.totalPages,
            totalElements = pagedResult.totalElements,
            size = pagedResult.size,
            currentPage = pagedResult.currentPage,
            hasNext = pagedResult.hasNext,
            hasPrevious = pagedResult.hasPrevious
        );
    }

    override fun getCatalog(
        selectedCategory: CategoryDTO?,
        page: Int,
        size: Int
    ): PagedResponse<ProductDTO> {

        if (selectedCategory != null) {
            val pagedResponse = productService.findByCategoryId(selectedCategory.id!!, page, size)
            return pagedResponse

        } else {
            val pagedRequest = PageRequest.of(page, size)
            val pagedResponse = productService.findAll(pagedRequest)
            return pagedResponse

        }

    }


    override fun searchProducts(request: ProductFilterRequest): CatalogSearchResult {
        val sort = Sort.by(
            if (request.sortDirection.equals("DESC", ignoreCase = true)) Sort.Direction.DESC
            else Sort.Direction.ASC,
            request.sortBy
        )
        val pageRequest = PageRequest.of(request.page, request.size, sort)

        val page = if (!request.search.isNullOrBlank()) {
            productService.search(request.search, request.page, request.size)
        } else {

            productService.findBySegmentId(request.segmentId ?: 0L, pageRequest.pageNumber, pageRequest.pageSize)
        }

        return CatalogSearchResult(
            products = page.content.map { it },
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            currentPage = page.currentPage,
            hasNext = page.hasNext,
            hasPrevious = page.hasPrevious
        )
    }


}
