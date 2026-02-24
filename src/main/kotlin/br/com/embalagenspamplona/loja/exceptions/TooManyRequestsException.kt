package br.com.embalagenspamplona.loja.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
class TooManyRequestsException(exception: Exception): RuntimeException(exception.message,exception.cause)