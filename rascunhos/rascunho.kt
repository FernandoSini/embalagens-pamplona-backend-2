/*
 esses codigos eram rascunho somente para entender como funciona mas ja esta dentro do checkout service impl
   * @PostMapping("/criar-intent")
   fun createIntent(@RequestBody request: Any): Map<String, String> {
       val params = PaymentIntentCreateParams.builder()
           .setAmount(1090L)
           .setCurrency("brl")
           .setAutomaticPaymentMethods(
               PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                   .setEnabled(true)
                   .build()
           ).build()
       val intent = PaymentIntent.create(params)
       return mapOf<String, String>("clientSecret" to intent.clientSecret)
   }


   /*somente para checkout sessions que seria ter a pagina tudo configurada no automatico
   e ele redirecionaria para uma outra pagina tipo aquelas que abre o qr code
   @PostMapping("/criar-sessao")
   fun createSession(@RequestBody order: OrderDTO): Map<String, String> {
       val myOrder = orderService.create(order)

       val params = SessionCreateParams.builder()
           .setMode(SessionCreateParams.Mode.PAYMENT)
           .setSuccessUrl("https://www.embalagenspamplona.com.br/checkout/{CHECKOUT_SESSION_ID}")
           .setCancelUrl("https://www.embalagenspamplona.com.br/canceled")
           .setClientReferenceId(myOrder.id.toString())
           .putMetadata("order_number", myOrder.orderNumber.toString() ?: myOrder.id.toString())
           .setAutomaticTax(SessionCreateParams.AutomaticTax.builder().setEnabled(true).build())
           //configura a expiracao da sessao dentro de 2 horas
           .setExpiresAt(Instant.now().plus(2, ChronoUnit.HOURS).epochSecond)

       order.items.forEach {
           params.addLineItem(
               SessionCreateParams.LineItem.builder()
                   .setQuantity(it.quantity)
                   .setPriceData(
                       SessionCreateParams.LineItem.PriceData.builder().setCurrency("brl")
                           .setUnitAmountDecimal(it.price)

                           .setProductData(
                               SessionCreateParams.LineItem.PriceData.ProductData
                                   .builder().setName(it.name).build()
                           ).build()
                   ).build()
           )
               .build()
       }
       val session = Session.create(params.build())
       return mapOf<String, String>("url" to session.url)

   }*/

   //aqui seria pro checkout embedded que é o que eu preciso, como ja tenho o forms pronto
   //eu só precisaria configurar o stripe para receber o pagamento
   //é a ponte entre o envio de dados e o processamento da compra
   @PostMapping("/criar-sessao")
   fun createSessionCheckoutEmbedded(@RequestBody order: OrderDTO): Map<String, String> {
       val myOrder = orderService.create(order)

       val params = SessionCreateParams.builder()
           .setUiMode(SessionCreateParams.UiMode.EMBEDDED)
           .setMode(SessionCreateParams.Mode.PAYMENT)
           .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
           .setPaymentMethodOptions(
               SessionCreateParams.PaymentMethodOptions.builder().setCard(
                   SessionCreateParams.PaymentMethodOptions.Card.builder()
                       .setInstallments(
                           SessionCreateParams.PaymentMethodOptions.Card.Installments.builder()
                               .setEnabled(true)
                               .build()
                       )
                       .build()
               ).build()
           )
           .addPaymentMethodType(SessionCreateParams.PaymentMethodType.PIX)
           .setPaymentMethodOptions(
               SessionCreateParams.PaymentMethodOptions.builder().setPix(
                   SessionCreateParams.PaymentMethodOptions.Pix
                       .builder().setExpiresAfterSeconds(3600L).build()
               ).build()
           )

           .setSuccessUrl("https://www.embalagenspamplona.com.br/checkout/{CHECKOUT_SESSION_ID}")
           .setCancelUrl("https://www.embalagenspamplona.com.br/canceled")
           .setClientReferenceId(myOrder.id.toString())
           .putMetadata("order_number", myOrder.orderNumber.toString() ?: myOrder.id.toString())
           .setAutomaticTax(SessionCreateParams.AutomaticTax.builder().setEnabled(false).build())
           .setTaxIdCollection(
               SessionCreateParams.TaxIdCollection.builder()
                   .setEnabled(true) // Isso obriga o cliente a digitar CPF/CNPJ no Stripe
                   .build()
           )
           //configura a expiracao da sessao dentro de 2 horas
           .setExpiresAt(Instant.now().plus(2, ChronoUnit.HOURS).epochSecond)

       order.items.forEach {
           params.addLineItem(
               SessionCreateParams.LineItem.builder()
                   .setQuantity(it.quantity)

                   .setPriceData(
                       SessionCreateParams.LineItem.PriceData.builder().setCurrency("brl")
                           .setUnitAmountDecimal(it.price)
                           .setProductData(
                               SessionCreateParams.LineItem.PriceData.ProductData
                                   .builder().setName(it.name)
                                   .build()
                           ).build()
                   ).build()
           )
               .build()
       }
       val session = Session.create(params.build())
       return mapOf<String, String>("url" to session.url)

   }

   @GetMapping("/confirmar-pagamento")
   fun confirmPayment(@RequestParam("sessionId") sessionId: String): Any {

       val session: Session = Session.retrieve(sessionId)
       try {
           var path: String
           if (session.paymentStatus == "paid") {
               path = "redirect:/checkout/success"
               return ResponseEntity.status(HttpStatus.OK).body(path)
           } else {
               path = "redirect:/checkout/error?reason=${session.paymentStatus}"
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(path)
           }
       } catch (e: StripeException) {
           session.expire()
           logger.error("houve um erro ao confirmar pagamento: ${e.message}")
           return "redirect:/checkout/error?reason=invalid_session"

       }


   }
}
   *
   * */


//para rodar uma env customizada tipo .env.dev no docker deve-se rodar
docker compse --env-file .env.dev -f compose-dev.yaml up --build