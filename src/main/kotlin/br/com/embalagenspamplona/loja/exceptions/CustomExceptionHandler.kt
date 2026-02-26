package br.com.embalagenspamplona.loja.exceptions

import br.com.embalagenspamplona.loja.data.dto.ErrorResponse
import br.com.embalagenspamplona.loja.data.dto.ValidationError
import jakarta.persistence.EntityNotFoundException
import org.apache.coyote.Response
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Controller
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler

import java.time.ZonedDateTime

//@RestControllerAdvice
@Controller
@ControllerAdvice
class CustomExceptionHandler: ResponseEntityExceptionHandler() {

    private val logger = LoggerFactory.getLogger(CustomExceptionHandler::class.java)

    @ExceptionHandler(exception = [Exception::class, InternalAuthenticationServiceException::class, AuthenticationException::class])
    //@ExceptionHandler(Exception::class)
     fun handleException(e: Exception, request: WebRequest): ResponseEntity<Any> {
        val exceptionResponse = ErrorResponse(
            timestamp = ZonedDateTime.now(),
            message = e.message,
            details = request.getDescription(true)
        )
        return ResponseEntity(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(ex: EntityNotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn("Entity not found: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(message = ex.message ?: "Recurso não encontrado"))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        logger.warn("Invalid argument: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(message = ex.message ?: "Argumento inválido"))
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(ex: IllegalStateException): ResponseEntity<ErrorResponse> {
        logger.warn("Invalid state: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse(message = ex.message ?: "Operação inválida"))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.map { error ->
            ValidationError(
                field = error.field,
                message = error.defaultMessage ?: "Campo inválido"
            )
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    message = "Erro de validação",
                    errors = errors
                )
            )
    }

   /* @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error: ${ex.message}", ex)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(message = "Erro interno do servidor"))
    }*/

    @ExceptionHandler(TooManyRequestsException::class)
    fun handleTooManyRequests(exception: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val exceptionResponse = ErrorResponse(
            message = exception.message,
            request.getDescription(true),
            timestamp = ZonedDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(exceptionResponse)

    }

    @ExceptionHandler(InternalServerException::class)
    fun handlerInternalServerException(exception: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val exceptionResponse = ErrorResponse(
            message = exception.message,
            details = request.getDescription(true),
            timestamp = ZonedDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse)
    }

    @ExceptionHandler(SeeOtherException::class)
    fun handleSeeOtherException(exception: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {

        val exceptionResponse = ErrorResponse(
            message = exception.message,
            details = request.getDescription(true),
            timestamp = ZonedDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.SEE_OTHER).body(exceptionResponse)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(e: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val exceptionResponse = ErrorResponse(
            message = e.message,
            details = request.getDescription(true),
            timestamp = ZonedDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse)
    }

    @ExceptionHandler(BadGatewayException::class)
    fun handleBadGatewayException(e: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val exceptionResponse = ErrorResponse(
            message = e.message,
            details = request.getDescription(true),
            timestamp = ZonedDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(exceptionResponse)
    }

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbiddenException(e: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val exceptionResponse = ErrorResponse(
            message = e.message,
            details = request.getDescription(true),
            timestamp = ZonedDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse)
    }

    @ExceptionHandler(GatewayTimeoutException::class)
    fun handleGatewayTimeoutException(e: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {

        val exceptionResponse = ErrorResponse(
            message = e.message,
            details = request.getDescription(true),
            timestamp = ZonedDateTime.now(),
        )
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(exceptionResponse)
    }

    @ExceptionHandler(LimitExceededException::class)
    fun handleLimitExceededException(e: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val exceptionResponse = ErrorResponse(
            message = e.message,
            details = request.getDescription(true),
            timestamp = ZonedDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED).body(exceptionResponse)

    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(e: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = e.message,
            details = request.getDescription(true),
            timestamp = ZonedDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }
    @ExceptionHandler(RequestTimeoutException::class)
    fun handleRequestTimeoutException(e: Exception, request: WebRequest): ResponseEntity<ErrorResponse>{
        val errorResponse= ErrorResponse(
            message = e.message,
            details = request.getDescription(true),
            timestamp = ZonedDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(errorResponse)
    }

    @ExceptionHandler(ServiceUnavailableException::class)
    fun handleServiceUnavailableException(e: Exception, request: WebRequest): ResponseEntity<ErrorResponse>{
        val errorResponse= ErrorResponse(
            message = e.message,
            details = request.getDescription(true),
            timestamp = ZonedDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(e: Exception, request: WebRequest): ResponseEntity<ErrorResponse>{
        val errorResponse= ErrorResponse(
            message = e.message,
            details = request.getDescription(true),
            timestamp = ZonedDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }
}

