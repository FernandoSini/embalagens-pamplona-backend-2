package br.com.embalagenspamplona.loja.services

import br.com.embalagenspamplona.loja.data.dto.SegmentDTO
import br.com.embalagenspamplona.loja.data.dto.SegmentWithProductsDTO

import java.util.UUID

interface SegmentService {
    
    fun findAll(): List<SegmentDTO>
    
    fun findById(id: Long): SegmentDTO
    
    fun create(request: SegmentDTO): SegmentDTO
    
    fun update( request: SegmentDTO): SegmentDTO
    
    fun delete(id: Long)

}
