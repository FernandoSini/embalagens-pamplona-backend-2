package br.com.embalagenspamplona.loja.services

import br.com.embalagenspamplona.loja.data.entities.RoleEntity

interface RoleService {

    fun createRole(role: RoleEntity): RoleEntity
    fun findRoleById(long: Long): RoleEntity
}