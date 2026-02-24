package br.com.embalagenspamplona.loja.adapters

import org.modelmapper.ModelMapper

class Mapper {

    fun <O,D> mapTo(from:O, to:Class<D>):D?{
        val modelMapper = ModelMapper()
        return modelMapper.map(from, to)
    }


}