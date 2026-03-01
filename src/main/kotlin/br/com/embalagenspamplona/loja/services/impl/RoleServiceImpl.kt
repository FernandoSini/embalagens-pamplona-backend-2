package br.com.embalagenspamplona.loja.services.impl

import br.com.embalagenspamplona.loja.data.entities.RoleEntity
import br.com.embalagenspamplona.loja.repository.datasource.local.RoleRepository
import br.com.embalagenspamplona.loja.services.RoleService
import org.springframework.stereotype.Service

@Service
class RoleServiceImpl(private val roleRepository: RoleRepository): RoleService

{
    override fun createRole(role: RoleEntity): RoleEntity {
        return roleRepository.save(role)
    }

    override fun findRoleById(id: Long): RoleEntity {
        return roleRepository.findById(id).orElseThrow {
            IllegalArgumentException("Role com id $id não encontrada")
        }
    }
}