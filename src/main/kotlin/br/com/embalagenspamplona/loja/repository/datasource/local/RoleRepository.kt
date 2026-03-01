package br.com.embalagenspamplona.loja.repository.datasource.local

import br.com.embalagenspamplona.loja.data.entities.RoleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository: JpaRepository<RoleEntity, Long> {
}