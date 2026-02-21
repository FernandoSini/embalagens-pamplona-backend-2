package br.com.embalagenspamplona.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
class ServiceUnavailableException(exception: Exception) : RuntimeException(exception.message, exception.cause)