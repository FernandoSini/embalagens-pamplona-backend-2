package br.com.embalagenspamplona.loja.controller

import br.com.embalagenspamplona.loja.data.dto.*
import br.com.embalagenspamplona.loja.services.SegmentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID
import javax.swing.text.Segment

@RestController
@RequestMapping("/api/v1/segments")
@Tag(name = "Segmentos", description = "Gerenciamento de segmentos/categorias de produtos")
class SegmentController(
    private val segmentService: SegmentService
) {

    @GetMapping
    @Operation(summary = "Listar todos os segmentos ativos")
    fun findAll(
        @RequestParam(value = "page", defaultValue = "0", required = true) page: Long = 0L,
        @RequestParam(value = "size", defaultValue = "5", required = true) pageSize:Long=0L
    ): ResponseEntity<ApiResponse<List<SegmentDTO>>> {
        val segments = segmentService.findAll()
        return ResponseEntity.ok(ApiResponse.success(segments))
    }


    @GetMapping("/{id}")
    @Operation(summary = "Buscar segmento por ID")
    fun findById(@PathVariable id: Long): ResponseEntity<ApiResponse<SegmentDTO>> {
        val segment = segmentService.findById(id)
        return ResponseEntity.ok(ApiResponse.success(segment))
    }


    @PostMapping("/create-segment")
    @Operation(summary = "Criar novo segmento")
    fun create(@RequestBody request: SegmentDTO): ResponseEntity<ApiResponse<Any>> {
        val segment = segmentService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(intArrayOf(), "Segmento criado com sucesso"))
    }

    @PutMapping("/update-segment")
    @Operation(summary = "Atualizar segmento")
    fun update(
        @RequestBody request: SegmentDTO
    ): ResponseEntity<ApiResponse<Any>> {
        segmentService.update(request)
        return ResponseEntity.ok(ApiResponse.success(intArrayOf(), "Segmento atualizado com sucesso"))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir segmento")
    fun delete(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> {
        segmentService.delete(id)
        return ResponseEntity.ok(ApiResponse.success(Unit, "Segmento excluído com sucesso"))
    }

}
