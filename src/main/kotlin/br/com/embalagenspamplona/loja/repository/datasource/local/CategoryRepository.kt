package br.com.embalagenspamplona.loja.repository.datasource.local

import br.com.embalagenspamplona.loja.data.entities.CategoryEntity
import br.com.embalagenspamplona.loja.data.entities.SegmentEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : JpaRepository<CategoryEntity, Long> {

    @Query("SELECT c from CategoryEntity c INNER JOIN c.segments s where s.id= :segmentId ")
    fun findCategoryEntityBySegments(
        @Param("segmentId") segmentId: Long,
        pageRequest: Pageable): Page<CategoryEntity>

}