package br.com.embalagenspamplona.loja.services.impl

import br.com.embalagenspamplona.loja.adapters.Mapper
import br.com.embalagenspamplona.loja.data.dto.CategoryDTO
import br.com.embalagenspamplona.loja.data.dto.CategoryPaginationRequest
import br.com.embalagenspamplona.loja.data.entities.CategoryEntity
import br.com.embalagenspamplona.loja.exceptions.NotFoundException
import br.com.embalagenspamplona.loja.repository.datasource.local.CategoryRepository
import br.com.embalagenspamplona.loja.services.CategoryService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class CategoryServiceImpl(private val categoryRepository: CategoryRepository) : CategoryService {

    override fun create(request: CategoryDTO): CategoryDTO? {
        val existCategory = categoryRepository.findById(request.id!!)
        if (existCategory.isEmpty) {
            val mapper = Mapper()

            val entity = mapper.mapTo(request, CategoryEntity::class.java)
            entity.icon = entity.title[0].uppercase()
            val savedEntity = categoryRepository.save(entity)
            return mapper.mapTo(savedEntity, CategoryDTO::class.java)
        } else {
            return null
        }


    }

    override fun getCategoryDetails(id: Long): CategoryDTO {
        val category =
            categoryRepository.findById(id).orElseThrow { throw NotFoundException(Exception("Categoria não encontrada!")) }
        val mapper = Mapper()
        return mapper.mapTo(category, CategoryDTO::class.java)
    }

    override fun getCategories(categoryPaginationRequest: CategoryPaginationRequest): Page<CategoryEntity> {
        val pageRequest = PageRequest.of(categoryPaginationRequest.page, categoryPaginationRequest.limit)
        val mapper = Mapper()
        return categoryRepository.findAll(pageRequest)

    }

    override fun update(request: CategoryDTO): Boolean {
            val existedCategory = categoryRepository.findById(request.id!!).orElseThrow{throw NotFoundException(Exception("Categoria não encontrada!")) }
      if(request.title.isNotEmpty()){
          existedCategory.title = request.title
          existedCategory.icon = request.title[0].uppercase()
          existedCategory.description = request.descrption
      }
        existedCategory.updatedAt = request.updatedAt
        val updatedCategory = categoryRepository.save(existedCategory)

        return updatedCategory!=null
    }

    override fun delete(id: Long): Boolean {
        val isDeleted = categoryRepository.deleteCategoryById(id)
        return isDeleted
    }

}