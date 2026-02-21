package br.com.embalagenspamplona.repository.datasource.local

import br.com.embalagenspamplona.data.entities.PromotionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PromotionRepository: JpaRepository<PromotionEntity, Long> {
}