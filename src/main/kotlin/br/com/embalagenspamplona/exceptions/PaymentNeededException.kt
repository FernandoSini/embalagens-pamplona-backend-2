package br.com.embalagenspamplona.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
class PaymentNeededException(e: Exception): RuntimeException(e.message,e.cause)