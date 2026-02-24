package br.com.embalagenspamplona.loja.data.entities

import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "addresses")
class AddressEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="address_id")
    val id: Long? = 0L,

    @Column(name="street",nullable = false, length = 200)
    var street: String="",

    @Column(name="number",nullable = false, length = 20)
    var number: String = "",

    @Column(name="complement",length = 100)
    var complementNumber: String = "",

    @Column(name="neighborhood",nullable = false, length = 100)
    var neighborhood: String="",

    @Column(name="city",nullable = false, length = 100)
    var city: String="",

    @Column(name="state",nullable = false, length = 2)
    var state: String="",

    @Column(name="zipCode",nullable = false, length = 9)
    var zipCode: String="",

    @Column(name="reference",length = 100)
    var reference: String = "",

    @Column(name = "is_default", nullable = false)
    val isDefault: Boolean = true,

    @Column(name="label",length = 50)
    val label: String = "", // Casa, Trabalho, etc.

    @ManyToOne
    @JoinColumn(name = "address_user", referencedColumnName = "user_id")
    val customer: UserEntity? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: ZonedDateTime = ZonedDateTime.now()
) {
    fun copy(
        id: Long? = this.id,
        street: String = this.street,
        number: String = this.number,
        complementNumber: String = this.complementNumber,
        neighborhood: String = this.neighborhood,
        city: String = this.city,
        state: String = this.state,
        zipCode: String = this.zipCode,
        reference: String = this.reference,
        isDefault: Boolean = this.isDefault,
        label: String = this.label,
        customer: UserEntity? = this.customer,
        createdAt: ZonedDateTime = this.createdAt,
        updatedAt: ZonedDateTime = this.updatedAt
    ) = AddressEntity(
        id = id, street = street, number = number, complementNumber = complementNumber,
        neighborhood = neighborhood, city = city, state = state, zipCode = zipCode,
        reference = reference, isDefault = isDefault, label = label,
        customer = customer, createdAt = createdAt, updatedAt = updatedAt
    )
}
