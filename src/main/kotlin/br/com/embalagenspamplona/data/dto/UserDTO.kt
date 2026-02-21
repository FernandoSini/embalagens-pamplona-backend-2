package br.com.embalagenspamplona.data.dto

import jakarta.persistence.Id
import java.time.LocalDateTime

data class UserDTO(
    val id: Long? = null,
    val name: String?=null,
    val lastName:String?=null,
    val email: String?=null,
    val phone: String? = null,
    val birthday: String? = null,
    val gender: String? = null,
    val cpfCnpj: String? = null,
    //val companyName: String? = null,
 /*   val addresses: List<AddressDTO> = emptyList(),
    val emailVerified: Boolean = false,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null*/
)
