package br.com.embalagenspamplona.loja.config.security.swagger

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.ObjectSchema
import io.swagger.v3.oas.models.media.PasswordSchema
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "Embalagens pamplona",
        version = "1.0.0",
        description = "Embalagens Pamplona api",
        license = License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"),
        contact = Contact(name = "Contact me", email = "embalagenspamplona@gmail.com")
    )
)
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        val loginSchema = ObjectSchema().title("Login Endping").description("Login endpoint")
            .addProperty("Login", StringSchema().example("Username"))
            .addProperty("Password", PasswordSchema().example("Password"))

        return OpenAPI()
            .info(
                io.swagger.v3.oas.models.info.Info()
                    .title("Embalagens Pamplona API")
                    .description("API para sistema de e-commerce de embalagens e descartáveis")
                    .version("1.0.0")
                    .contact(
                        io.swagger.v3.oas.models.info.Contact()
                            .name("Embalagens Pamplona")
                            .email("contato@embalagenspamplona.com.br")
                    )
                    .license(
                        io.swagger.v3.oas.models.info.License()
                            .name("Proprietary")
                    )
            )
            .servers(
                listOf(
                    Server().url("http://localhost:8080").description("Desenvolvimento"),
                    Server().url("https://api.embalagenspamplona.com.br").description("Produção")
                )
            ).components(
                Components().addSchemas(
                    loginSchema.name, loginSchema
                )
            )
    }
}