package br.com.embalagenspamplona.services

import br.com.embalagenspamplona.data.dto.*
import org.springframework.data.domain.Pageable
import java.util.UUID

interface ProductService {

    fun findAll(pageable: ProductFilterRequest): PagedResponse<ProductDTO>
    fun findAll(pageable: Pageable): PagedResponse<ProductDTO>
    fun findById(id: Long): ProductDTO

    fun findByCategoryId(id:Long, page:Int, size:Int): PagedResponse<ProductDTO>

    fun findBySegmentId(segmentId: Long, page: Int, size: Int): PagedResponse<ProductDTO>
    fun search(query: String, page: Int, size: Int): PagedResponse<ProductDTO>

    fun create(request: ProductDTO): ProductDTO

    fun update( request: ProductDTO): ProductDTO

    fun delete(id: Long)



}
