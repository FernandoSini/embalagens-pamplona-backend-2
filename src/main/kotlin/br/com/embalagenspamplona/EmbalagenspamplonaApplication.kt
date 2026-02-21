package br.com.embalagenspamplona

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile
import java.util.TimeZone


@Profile("dev | prod")
@SpringBootApplication
class EmbalagenspamplonaApplication

fun main(args: Array<String>) {
    runApplication<EmbalagenspamplonaApplication>(*args)
}

@PostConstruct
fun init(){
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
}
