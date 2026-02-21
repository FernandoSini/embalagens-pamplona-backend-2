package br.com.embalagenspamplona.repository.datasource.local

import br.com.embalagenspamplona.data.entities.SegmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SegmentRepository : JpaRepository<SegmentEntity, Long> {

    fun findSegmentEntityById(id: Long): MutableList<SegmentEntity>

    fun findByActiveTrueOrderByDisplayOrderAsc(): List<SegmentEntity>

    fun findByNameContainingIgnoreCase(name: String): List<SegmentEntity>

    @Query("SELECT s FROM SegmentEntity s LEFT JOIN FETCH s.categories WHERE s.id = :id")
    fun findByIdWithProducts(id: Long): SegmentEntity?

    fun existsByNameIgnoreCase(name: String): Boolean
}
