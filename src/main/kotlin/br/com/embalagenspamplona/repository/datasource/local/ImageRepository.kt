package br.com.embalagenspamplona.repository.datasource.local

import br.com.embalagenspamplona.data.entities.ImageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ImageRepository : JpaRepository<ImageEntity, Long> {
}
