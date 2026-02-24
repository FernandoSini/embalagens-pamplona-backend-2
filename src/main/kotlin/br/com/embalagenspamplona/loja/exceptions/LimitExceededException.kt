package br.com.embalagenspamplona.loja.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)
class LimitExceededException(exception: Exception): RuntimeException(exception.message, exception.cause)