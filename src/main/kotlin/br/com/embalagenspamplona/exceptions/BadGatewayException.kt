package br.com.embalagenspamplona.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_GATEWAY)
class BadGatewayException(exception: Exception) : RuntimeException(exception.message, exception.cause)