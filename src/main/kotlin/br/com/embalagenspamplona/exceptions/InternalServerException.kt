package br.com.embalagenspamplona.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class InternalServerException(exception: Exception): RuntimeException(exception.message,exception.cause) {
}