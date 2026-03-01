package br.com.embalagenspamplona.loja.adapters

import org.modelmapper.ModelMapper
import org.modelmapper.config.Configuration.AccessLevel

class Mapper {
    fun <D> mapTo(from: Any, to: Class<D>): D {
        val modelMapper = ModelMapper().apply {
            configuration.setFieldMatchingEnabled(true)
            configuration.setFieldAccessLevel(AccessLevel.PRIVATE)
            configuration.setMethodAccessLevel(AccessLevel.PUBLIC)
            configuration.setSkipNullEnabled(true)
        }
        return modelMapper.map(from, to)
    }
}