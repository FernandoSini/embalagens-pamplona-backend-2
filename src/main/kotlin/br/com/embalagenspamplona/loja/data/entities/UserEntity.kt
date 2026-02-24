package br.com.embalagenspamplona.loja.data.entities

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Entity
@Table(name = "users")
 class UserEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long? = null,

    @Column(name = "name", nullable = false, length = 200)
    var name: String = "",
    @Column(name = "last_name", length = 50)
    var lastName: String = "",

    @Column(name = "email", nullable = false, unique = true, length = 150)
    var email: String = "",

    @Column(name = "gender")
    var gender: String = "",

    @Column(name = "birthday", length = 10)
    var birthday: String = "",

    @Column(name = "password", nullable = false)
    private var password: String = "",

    @Column(name = "phone", nullable = false, length = 20)
    var phone: String = "",

    @Size(min = 11, max = 14)
    @Column(name = "cpf_cnpj", nullable = false)
    var cpfCnpj: String = "",
    /*@Column(length = 18)
    val cnpj: String? = null,*/

    @OneToMany(mappedBy = "customer", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val addresses: MutableList<AddressEntity> = mutableListOf(),

    @OneToOne(mappedBy = "userEntity", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val cart: CartEntity? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val orders: MutableList<OrderEntity> = mutableListOf(),

    @Column(name = "active", nullable = false)
    val active: Boolean = true,

    @Column(name = "email_verified", nullable = false)
    val emailVerified: Boolean = false,

    @ManyToOne
    @JoinColumn(name = "user_role", referencedColumnName = "role_id", foreignKey = ForeignKey(name = "fk_role_id"))
    val role: RoleEntity? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: ZonedDateTime? = null
) : UserDetails, Serializable {
    override fun getAuthorities(): Collection<GrantedAuthority?> {
        val listRoles = mutableSetOf<RoleEntity>()
        if (this.role != null) {
            listRoles.add(this.role)
        }
        return listRoles

    }

    override fun getPassword(): String {
        return this.password
    }

    override fun getUsername(): String? {
        return this.name ?: this.email
    }

    fun copy(
        id: Long? = this.id,
        name: String = this.name,
        lastName: String = this.lastName,
        email: String = this.email,
        gender: String = this.gender,
        birthday: String = this.birthday,
        password: String = this.password,
        phone: String = this.phone,
        cpfCnpj: String = this.cpfCnpj,
        addresses: MutableList<AddressEntity> = this.addresses,
        cart: CartEntity? = this.cart,
        orders: MutableList<OrderEntity> = this.orders,
        active: Boolean = this.active,
        emailVerified: Boolean = this.emailVerified,
        role: RoleEntity? = this.role,
        createdAt: ZonedDateTime = this.createdAt,
        updatedAt: ZonedDateTime? = this.updatedAt
    ) = UserEntity(
        id = id, name = name, lastName = lastName, email = email,
        gender = gender, birthday = birthday, password = password, phone = phone,
        cpfCnpj = cpfCnpj, addresses = addresses, cart = cart, orders = orders,
        active = active, emailVerified = emailVerified, role = role,
        createdAt = createdAt, updatedAt = updatedAt
    )
}
