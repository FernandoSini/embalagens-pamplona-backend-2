/*
package br.com.embalagenspamplona.loja

import jakarta.annotation.PostConstruct
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import java.util.TimeZone


@Profile("dev | prod | dev-local")
@EnableJpaRepositories(basePackages= arrayOf("br.com.embalagenspamplona.loja"))
@SpringBootApplication
class EmbalagenspamplonaApplication

fun main(args: Array<String>) {
    runApplication<EmbalagenspamplonaApplication>(*args)
}

@PostConstruct
fun init(){
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
}
*/
