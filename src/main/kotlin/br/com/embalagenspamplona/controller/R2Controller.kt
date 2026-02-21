package br.com.embalagenspamplona.controller

import br.com.embalagenspamplona.data.dto.ApiResponse
import br.com.embalagenspamplona.services.R2Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Map
import java.util.UUID


@RestController
@RequestMapping("/api/storage")
class R2Controller(private val r2Service: R2Service) {


    @PostMapping("/upload-url")
    fun getUploadUrl(@RequestParam originalName: String?): ResponseEntity<ApiResponse<MutableMap<String, String>>> {
        if (!originalName.isNullOrBlank()) {
            var extension: String = "";
            if (originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }
            val safeName: String = UUID.randomUUID().toString() + extension;
            val url: String = r2Service.generatePresignedUploadUrl(safeName) ?: ""

            return ResponseEntity.ok<ApiResponse<MutableMap<String, String>>>(
                ApiResponse.success(Map.of<String, String>("uploadUrl", url)))
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Nome do arquivo inválido"))
        }
    }
}