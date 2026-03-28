package br.com.embalagenspamplona.loja.services

import br.com.embalagenspamplona.loja.data.dto.*
import br.com.embalagenspamplona.loja.data.entities.CategoryEntity

interface CatalogService {

    fun getSegmentCatalog(selectedSegment: SegmentDTO, page:Int, size:Int): PagedResponse<ProductDTO>
    fun getCatalog(selectedCategory: CategoryDTO?, page:Int, size:Int): PagedResponse<ProductDTO>


    fun searchProducts(request: ProductFilterRequest): PagedResponse<ProductDTO>


}
