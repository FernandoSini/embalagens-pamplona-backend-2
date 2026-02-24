package br.com.embalagenspamplona.loja.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class ForbiddenException(exception: Exception): RuntimeException(exception.message, exception.cause)