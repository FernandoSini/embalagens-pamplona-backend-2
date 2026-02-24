package br.com.embalagenspamplona.loja.config.security

import br.com.embalagenspamplona.loja.config.serialization.Yaml2JacksonHttpMessageConverter
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.socket.config.annotation.EnableWebSocket

@Configuration
@EnableWebSocket
class WebConfig: WebMvcConfigurer {
    private val MEDIA_TYPE_YML = MediaType.parseMediaType("application/x-yaml")
    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        super.configureContentNegotiation(configurer)
        configurer.favorParameter(false)
            .ignoreAcceptHeader(false)
            .useRegisteredExtensionsOnly(false)

            .defaultContentType(MediaType.APPLICATION_JSON)
            .mediaType("json", MediaType.APPLICATION_JSON)
            .mediaType("xml", MediaType.APPLICATION_XML)
            .mediaType("yml", MediaType.APPLICATION_YAML)
            .mediaType("yaml", MEDIA_TYPE_YML)
            .mediaType("", MediaType.APPLICATION_OCTET_STREAM)
    }

    override fun extendMessageConverters(converters: List<HttpMessageConverter<*>?>) {
        super.extendMessageConverters(converters)
        converters.toMutableList().apply {
            add(Yaml2JacksonHttpMessageConverter())
        }

    }


    override fun addCorsMappings(registry: CorsRegistry) {
        super.addCorsMappings(registry)
        registry.addMapping("/**")
            .allowedHeaders("GET", "PUT", "POST", "DELETE", "OPTIONS", "PATCH")
    }
}