package br.com.embalagenspamplona.loja.data.dto

import java.time.LocalDateTime
import java.time.ZonedDateTime

data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun <T> success(data: T, message: String? = null): ApiResponse<T> {
            return ApiResponse(success = true, message = message, data = data)
        }

        fun <T> error(message: String): ApiResponse<T> {
            return ApiResponse(success = false, message = message, data = null)
        }
    }
}

data class PagedResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val size: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)

data class ValidationError(
    val field: String,
    val message: String
)

data class ErrorResponse(
 //   val success: Boolean = false,
    val message: String?,
    val details: String?=null,
    val errors: List<ValidationError>? = null,
    val timestamp: ZonedDateTime = ZonedDateTime.now()
)
