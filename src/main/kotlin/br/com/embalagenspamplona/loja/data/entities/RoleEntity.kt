package br.com.embalagenspamplona.loja.data.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.springframework.security.core.GrantedAuthority
import java.io.Serializable
import java.time.ZonedDateTime

@Entity
@Table(name = "roles")
class RoleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private var id: Long = 1L,

    @Column(name = "name")
    private var name: String = "",
    @Column(name = "description")
    private var description: String = "",

    @Column(name = "created_at")
    private var createdAt: ZonedDateTime? = ZonedDateTime.now(),

    @OneToMany(mappedBy = "role")
    private val userList: MutableList<UserEntity> = mutableListOf(),

    @Column(name = "updated_at")
    private var updatedAt: ZonedDateTime? = null
) : GrantedAuthority, Serializable {
    override fun getAuthority(): String {
        return this.name
    }

    fun getDescription(): String {
        return this.description;
    }

    fun addUser(user: UserEntity) {
        userList.add(user)
    }

    override fun toString(): String {
        return this.name
    }

    fun copy(
        id: Long = this.id,
        name: String = this.name,
        description: String = this.description,
        createdAt: ZonedDateTime? = this.createdAt,
        list: MutableList<UserEntity> = this.userList,
        updatedAt: ZonedDateTime? = this.updatedAt
    ) = RoleEntity(
        id = id, name = name, description = description,
        createdAt = createdAt, userList = list, updatedAt = updatedAt
    )
}