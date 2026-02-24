package br.com.embalagenspamplona.loja.repository.datasource.local

import br.com.embalagenspamplona.loja.data.entities.SegmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SegmentRepository : JpaRepository<SegmentEntity, Long> {

    fun findSegmentEntityById(id: Long): MutableList<SegmentEntity>

    fun findByTitleContainingIgnoreCase(title: String): List<SegmentEntity>

    @Query("SELECT s FROM SegmentEntity s LEFT JOIN FETCH s.categories WHERE s.id = :id")
    fun findByIdWithProducts(id: Long): SegmentEntity?

    fun existsByTitleIgnoreCase(title: String): Boolean
}
