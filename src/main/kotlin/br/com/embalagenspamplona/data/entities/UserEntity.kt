package br.com.embalagenspamplona.data.entities

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: Long? = 0L,

    @Column(name = "name", nullable = false, length = 200)
    var name: String? = "",
    @Column(name = "last_name", length = 50)
    var lastName: String? = "",

    @Column(name="email",nullable = false, unique = true, length = 150)
    var email: String? = "",

    @Column(name = "gender")
    var gender: String? = "",

    @Column(name = "birthday", length = 10)
    var birthday: String? = "",

    @Column(name="password",nullable = false)
    private var password: String,

    @Column(name="phone",length = 20)
    var phone: String? = null,

    @Size(min = 11, max = 14)
    @Column(name = "cpf_cnpj")
    var cpfCnpj: String? = null,
    /*@Column(length = 18)
    val cnpj: String? = null,*/

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val addresses: MutableList<AddressEntity> = mutableListOf(),

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val cart: CartEntity? = CartEntity(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val orders: MutableList<OrderEntity> = mutableListOf(),

    @Column(name = "active", nullable = false)
    val active: Boolean = true,

    @Column(name = "email_verified", nullable = false)
    val emailVerified: Boolean = false,

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id", foreignKey = ForeignKey(name = "fk_role_id"))
    val role: RoleEntity = RoleEntity(),

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: ZonedDateTime? = null
) : UserDetails, Serializable {
    override fun getAuthorities(): Collection<GrantedAuthority?> {
        val listRoles = mutableSetOf<RoleEntity>()
        listRoles.add(this.role)
        return listRoles
    }

    override fun getPassword(): String {
        return this.password
    }

    override fun getUsername(): String? {
        return this.name ?: this.email
    }
}
