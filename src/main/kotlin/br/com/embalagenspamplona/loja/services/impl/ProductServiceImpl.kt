package br.com.embalagenspamplona.loja.services.impl

import br.com.embalagenspamplona.loja.adapters.Mapper
import br.com.embalagenspamplona.loja.data.dto.*
import br.com.embalagenspamplona.loja.data.entities.ImageEntity
import br.com.embalagenspamplona.loja.data.entities.ProductEntity
import br.com.embalagenspamplona.loja.data.entities.PromotionEntity
import br.com.embalagenspamplona.loja.exceptions.InternalServerException
import br.com.embalagenspamplona.loja.repository.datasource.local.ImageRepository
import br.com.embalagenspamplona.loja.repository.datasource.local.ProductRepository
import br.com.embalagenspamplona.loja.repository.datasource.local.PromotionRepository
import br.com.embalagenspamplona.loja.repository.datasource.local.SegmentRepository
import br.com.embalagenspamplona.loja.services.ProductService
import br.com.embalagenspamplona.loja.services.R2Service
import com.stripe.model.Product
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

@Service
@Transactional
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val promotionRepository: PromotionRepository,
    private val imageRepository: ImageRepository,
    private val r2Service: R2Service,
    private val segmentRepository: SegmentRepository
) : ProductService {

    override fun findAll(pageable: ProductFilterRequest): PagedResponse<ProductDTO> {
        val sort = Sort.by(
            if (pageable.sortDirection.equals("DESC", ignoreCase = true)) Sort.Direction.DESC
            else Sort.Direction.ASC,
            pageable.sortBy
        )
        val pageRequest = PageRequest.of(pageable.page, pageable.size, sort)

        val page = productRepository.findAll(pageRequest)

        return PagedResponse(
            content = page.content.map { it.toProductDTO() },
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            currentPage = page.number,
            size = page.size,
            hasNext = page.hasNext(),
            hasPrevious = page.hasPrevious()
        )
    }

    override fun findAll(pageable: Pageable): PagedResponse<ProductDTO> {

        val pageRequest = PageRequest.of(pageable.pageNumber, pageable.pageSize)

        val page = productRepository.findAll(pageRequest)

        return PagedResponse(
            content = page.content.map { it.toProductDTO() },
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            currentPage = page.number,
            size = page.size,
            hasNext = page.hasNext(),
            hasPrevious = page.hasPrevious()
        )
    }

    override fun findById(id: Long): ProductDTO {
        return productRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Produto não encontrado: $id") }
            .toProductDTO()
    }

    override fun findByCategoryId(id: Long, page:Int,size:Int): PagedResponse<ProductDTO> {
        val pageRequest: Pageable = PageRequest.of(page, size)
        val productPage  =productRepository.findProductEntityByCategoryEntityId(id, pageRequest)
        return PagedResponse(
            content = productPage.content.map { it },
            totalElements = productPage.totalElements,
            totalPages = productPage.totalPages,
            currentPage = productPage.number,
            size = productPage.size,
            hasNext = productPage.hasNext(),
            hasPrevious = productPage.hasPrevious()
        )
    }

    override fun findBySegmentId(segmentId: Long, page: Int, size: Int): PagedResponse<ProductDTO> {
        val pageRequest: Pageable = PageRequest.of(page, size)
        val productPage = productRepository.findProductEntityByCategoryEntitySegments(segmentId, pageRequest)

        return PagedResponse(
            content = productPage.content.map { it.toProductDTO() },
            totalElements = productPage.totalElements,
            totalPages = productPage.totalPages,
            currentPage = productPage.number,
            size = productPage.size,
            hasNext = productPage.hasNext(),
            hasPrevious = productPage.hasPrevious()
        )
    }

    override fun search(query: String, page: Int, size: Int): PagedResponse<ProductDTO> {
        val pageRequest = PageRequest.of(page, size)
        val productPage = productRepository.searchByNameOrDescription(query, pageRequest)

        return PagedResponse(
            content = productPage.content.map { it.toProductDTO() },
            totalElements = productPage.totalElements,
            totalPages = productPage.totalPages,
            currentPage = productPage.number,
            size = productPage.size,
            hasNext = productPage.hasNext(),
            hasPrevious = productPage.hasPrevious()
        )
    }

    override fun create(request: ProductDTO): ProductDTO {
        val mapper = Mapper()
        try {
            val product = mapper.mapTo(request::class.java, ProductEntity::class.java)
            if (product != null) {
                if (product.promotion != null) {
                    val promotion =

                        PromotionEntity(
                            id = product.promotion.id,
                            description = product.promotion.description,
                            code = product.promotion.code,
                            price = product.promotion.price,
                            startsAt = product.promotion.startsAt,
                            expiresAt = product.promotion.expiresAt,
                        )


                    val savedPromotion = promotionRepository.save(promotion)
                    val productWithPromotions = product.copy(promotion = savedPromotion)
                    val savedProduct = productRepository.save(productWithPromotions)

                    if (request.images.isNotEmpty()) {
                        val imageEntities = request.images.map { dto ->
                            ImageEntity(
                                url = dto.url,
                                product = savedProduct
                            )
                        }
                        imageRepository.saveAll(imageEntities)
                    }

                    return savedProduct.toProductDTO()
                } else {
                    if (request.images.isNotEmpty()) {
                        val imageEntities = request.images.map { dto ->
                            ImageEntity(
                                url = dto.url,
                                product = product
                            )
                        }
                        imageRepository.saveAll(imageEntities)
                    }

                    return productRepository.save(product).toProductDTO()

                }
            } else {
                throw InternalServerException(Exception("Erro na conversao do produto!"))
            }
        } catch (e: Exception) {
            throw InternalServerException(Exception("Não foi possivel criar produto: ${e.message}"))
        }
    }

    override fun update(request: ProductDTO): ProductDTO {
        val product = productRepository.findById(request.id!!)
            .orElseThrow { EntityNotFoundException("Produto não encontrado: ${request.id}") }

        val updatedProduct = product.copy(
            name = request.name.ifEmpty { product.name },
            description = request.description ?: product.description,
            price = if (request.price != BigDecimal.ZERO) request.price else product.price,
            quantity = if (request.quantity != 0) request.quantity else product.quantity,
            sku = request.sku ?: product.sku,
            pack = request.pack ?: product.pack,
            updatedAt = ZonedDateTime.now()
        )

        val savedProduct = productRepository.save(updatedProduct)

        // Promotion
        val existingPromotion = product.promotion

        if (request.promotion != null) {
            val dto = request.promotion
            if (existingPromotion != null) {
                val updatedPromo = existingPromotion.copy(
                    description = dto.description ?: existingPromotion.description,
                    code = dto.code ?: existingPromotion.code,
                    price = dto.price,
                    startsAt = dto.startDate ?: existingPromotion.startsAt,
                    expiresAt = dto.endDate ?: existingPromotion.expiresAt,
                    updatedAt = ZonedDateTime.now()
                )
                promotionRepository.save(updatedPromo)
            } else {
                val newPromo = PromotionEntity(
                    description = dto.description,
                    code = dto.code,
                    price = dto.price,
                    startsAt = dto.startDate,
                    expiresAt = dto.endDate,
                    product = savedProduct
                )
                promotionRepository.save(newPromo)
            }
        } else if (existingPromotion != null && ZonedDateTime.now().isAfter(existingPromotion.expiresAt)) {
            promotionRepository.delete(existingPromotion)
        }

        // Images
        if (request.images.isNotEmpty()) {
            val existingImageIds = product.images.map { it.id }.toSet()
            val requestImageIds = request.images.filter { it.id != 0L }.map { it.id }.toSet()

            val imagesToRemove = product.images.filter { it.id in (existingImageIds - requestImageIds) }
            imagesToRemove.forEach { image ->
                val key = image.url.substringAfterLast("/")
                r2Service.deleteFile(key)
            }
            if (imagesToRemove.isNotEmpty()) {
                imageRepository.deleteAllById(imagesToRemove.map { it.id })
            }

            val newImages = request.images.filter { it.id == 0L || it.id !in existingImageIds }
            if (newImages.isNotEmpty()) {
                val imageEntities = newImages.map { dto ->
                    ImageEntity(
                        url = dto.url,
                        product = savedProduct
                    )
                }
                imageRepository.saveAll(imageEntities)
            }
        }

        return productRepository.findById(request.id!!)
            .orElseThrow { EntityNotFoundException("Produto não encontrado: ${request.id}") }
            .toProductDTO()
    }

    override fun delete(id: Long) {
        val product = productRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Produto não encontrado: $id") }

        product.images.forEach { image ->
            val key = image.url.substringAfterLast("/")
            r2Service.deleteFile(key)
        }

        productRepository.delete(product)
    }


}
