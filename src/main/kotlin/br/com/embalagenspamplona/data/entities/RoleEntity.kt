package br.com.embalagenspamplona.data.entities

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
data class RoleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = 0L,

    @Column(name = "name")
    private var name: String? = "",
    @Column(name = "description")
    private var description: String? = "",

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private var createdAt: ZonedDateTime? = ZonedDateTime.now(),

    @OneToMany(mappedBy = "role")
    private val list: MutableList<UserEntity> =mutableListOf(),

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private var updatedAt: ZonedDateTime? = null
) : Serializable, GrantedAuthority {
    override fun getAuthority(): String? {
        return name
    }

    fun getDescription(): String? {
        return description;
    }
}