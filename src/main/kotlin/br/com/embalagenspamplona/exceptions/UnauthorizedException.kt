package br.com.embalagenspamplona.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class UnauthorizedException(exception: Exception): RuntimeException(exception.message, exception.cause)