package br.com.embalagenspamplona.services

import br.com.embalagenspamplona.data.dto.SegmentDTO
import br.com.embalagenspamplona.data.dto.SegmentWithProductsDTO

import java.util.UUID

interface SegmentService {
    
    fun findAll(): List<SegmentDTO>
    
    fun findById(id: Long): SegmentDTO
    
    fun create(request: SegmentDTO): SegmentDTO
    
    fun update( request: SegmentDTO): SegmentDTO
    
    fun delete(id: Long)

}
