package br.com.embalagenspamplona.loja.services

interface R2Service {
    fun generatePresignedUploadUrl(fileName: String): String?
    fun deleteFile(fileName: String)
}