package br.com.embalagenspamplona.loja.services.impl

import br.com.embalagenspamplona.loja.adapters.Mapper
import br.com.embalagenspamplona.loja.data.dto.CategoryDTO
import br.com.embalagenspamplona.loja.data.dto.ProductDTO
import br.com.embalagenspamplona.loja.data.dto.PromotionDTO
import br.com.embalagenspamplona.loja.data.dto.SegmentDTO
import br.com.embalagenspamplona.loja.data.entities.CategoryEntity
import br.com.embalagenspamplona.loja.data.entities.ProductEntity
import br.com.embalagenspamplona.loja.data.entities.SegmentEntity
import br.com.embalagenspamplona.loja.repository.datasource.local.ProductRepository
import br.com.embalagenspamplona.loja.repository.datasource.local.SegmentRepository
import br.com.embalagenspamplona.loja.services.SegmentService
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.ZonedDateTime

@Service
@Transactional
class SegmentServiceImpl(
    private val segmentRepository: SegmentRepository,
    private val productRepository: ProductRepository
) : SegmentService {

    override fun findAll(): List<SegmentDTO> {
        return segmentRepository.findAll().map { it.toDTO() }
    }

    override fun findById(id: Long): SegmentDTO {
        return segmentRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Segmento não encontrado: $id") }
            .toDTO()
    }

    override fun create(request: SegmentDTO): SegmentDTO {
        if (segmentRepository.existsByTitleIgnoreCase(request.title)) {
            throw IllegalArgumentException("Já existe um segmento com este nome")
        }

        val segment = SegmentEntity(
            title = request.title,
            description = request.description,
            pill = request.pill,
            icon = request.title[0].uppercase(),
            categories = request.categories.map { e-> Mapper().mapTo(e, CategoryEntity::class.java) }.toMutableSet(),
            createdAt = ZonedDateTime.now(),
        )
        return segmentRepository.save(segment).toDTO()
    }

    override fun update(request: SegmentDTO) : SegmentDTO{
        val segment = segmentRepository.findById(request.id)
            .orElseThrow { EntityNotFoundException("Segmento não encontrado: ${request.id}") }

        val updatedSegment = segment.copy(
            title = request.title ?: segment.title,
            description = request.description ?: segment.description,
            pill = request.pill?:segment.pill,
            icon = request.title[0].uppercase()?: segment.icon,
            updatedAt = ZonedDateTime.now(),
        )

         return segmentRepository.save(updatedSegment).toDTO()
    }

    override fun delete(id: Long) {
        if (!segmentRepository.existsById(id)) {
            throw EntityNotFoundException("Segmento não encontrado: $id")
        }
        segmentRepository.deleteById(id)
    }


    private fun SegmentEntity.toDTO(): SegmentDTO {
        return SegmentDTO(
            id = this.id,
            title = this.title.toString(),
            description = this.description,
            pill = this.pill,
            icon = this.icon,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
}

// Extension function para converter ProductEntity para ProductDTO
fun ProductEntity.toProductDTO(): ProductDTO {
    return ProductDTO(
        id = this.id,
        name = this.name,
        description = this.description,
        price = this.price,
        promotion = PromotionDTO(
            id = this.promotion?.id,
            description = this.promotion?.description,
            price = this.promotion?.price?: BigDecimal.ZERO,
            code = this.promotion?.code,
            startDate = this.promotion?.startsAt,
            endDate = this.promotion?.expiresAt
        ),
        category = CategoryDTO(
            id= this.categoryEntity.id,
            title = this.categoryEntity.title,
            icon = this.categoryEntity.icon,
            createdAt=this.categoryEntity.createdAt
        ),
        sku = this.sku,
        quantity = this.quantity,
        pack = this.pack,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
