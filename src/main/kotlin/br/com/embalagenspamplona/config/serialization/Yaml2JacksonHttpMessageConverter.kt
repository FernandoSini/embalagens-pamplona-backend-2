package br.com.embalagenspamplona.config.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter


class Yaml2JacksonHttpMessageConverter(
    objectMapper: ObjectMapper = ObjectMapper(),
    supportedMediaType: MediaType = MediaType.parseMediaType("application/x-yaml"),
): AbstractJackson2HttpMessageConverter(objectMapper,supportedMediaType)