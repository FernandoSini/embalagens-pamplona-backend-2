package br.com.embalagenspamplona.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.models.info.Info as ModelInfo
import io.swagger.v3.oas.models.info.Contact as ModelContact
import io.swagger.v3.oas.models.info.License as ModelLicense
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "Embalagens pamplona",
        version = "1.0.0",
        description = "",
       // license = License("Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"),
        contact = Contact(name = "Contact me", email = "sinigagliafernando@gmail.com")
    )
)
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                ModelInfo()
                    .title("Embalagens Pamplona API")
                    .description("API para sistema de e-commerce de embalagens e descartáveis")
                    .version("1.0.0")
                    .contact(
                        ModelContact()
                            .name("Embalagens Pamplona")
                            .email("contato@embalagenspamplona.com.br")
                    )
                    .license(
                        ModelLicense()
                            .name("Proprietary")
                    )
            )
            .servers(
                listOf(
                    Server().url("http://localhost:8080").description("Desenvolvimento"),
                    Server().url("https://api.embalagenspamplona.com.br").description("Produção")
                )
            )
    }
}
