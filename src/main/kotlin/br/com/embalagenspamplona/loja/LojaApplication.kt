package br.com.embalagenspamplona.loja

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile
import java.util.TimeZone

@Profile("dev | prod | dev-local")
@SpringBootApplication
class LojaApplication

fun main(args: Array<String>) {
    runApplication<LojaApplication>(*args)
}
@PostConstruct
fun init(){
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
}
