package br.com.embalagenspamplona.loja.data.dto

import br.com.embalagenspamplona.loja.data.entities.RoleEntity

data class UserDTO(
    val id: Long? = null,
    val name: String?=null,
    val lastName:String?=null,
    val email: String?=null,
    val gender: String? = null,
    val birthday: String? = null,
    val phone: String? = null,
    val cpfCnpj: String? = null,
    val role: RoleDTO?=null
    //val companyName: String? = null,
 /*   val addresses: List<AddressDTO> = emptyList(),
    val emailVerified: Boolean = false,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null*/
)
