package br.com.embalagenspamplona.services

import br.com.embalagenspamplona.data.dto.*
import br.com.embalagenspamplona.data.entities.CategoryEntity

interface CatalogService {

    fun getSegmentCatalog(selectedSegment: SegmentDTO, page:Int, size:Int): PagedResponse<ProductDTO>
    fun getCatalog(selectedCategory: CategoryDTO?, page:Int, size:Int): PagedResponse<ProductDTO>


    fun searchProducts(request: ProductFilterRequest): CatalogSearchResult


}
