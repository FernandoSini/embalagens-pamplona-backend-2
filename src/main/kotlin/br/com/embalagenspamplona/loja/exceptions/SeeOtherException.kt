package br.com.embalagenspamplona.loja.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.SEE_OTHER)
class SeeOtherException (exception: Exception): RuntimeException(exception.message, exception.cause)