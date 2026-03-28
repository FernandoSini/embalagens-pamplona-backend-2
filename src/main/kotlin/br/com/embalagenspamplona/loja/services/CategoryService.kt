package br.com.embalagenspamplona.loja.services

import br.com.embalagenspamplona.loja.data.dto.CategoryDTO
import br.com.embalagenspamplona.loja.data.dto.CategoryPaginationRequest
import br.com.embalagenspamplona.loja.data.entities.CategoryEntity
import org.apache.commons.lang3.mutable.Mutable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CategoryService {

    fun create(request: CategoryDTO): CategoryDTO?

    fun getCategoryDetails(id: Long): CategoryDTO?
    fun getCategories(categoryPaginationRequest: CategoryPaginationRequest): Page<CategoryEntity>
    fun update(request: CategoryDTO): Boolean

    fun delete(id: Long): Boolean
}