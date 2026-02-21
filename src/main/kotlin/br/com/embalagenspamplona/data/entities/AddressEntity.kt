package br.com.embalagenspamplona.data.entities

import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "addresses")
data class AddressEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = 0L,

    @Column(nullable = false, length = 200)
    var street: String="",

    @Column(nullable = false, length = 20)
    var number: String = "",

    @Column(length = 100)
    var complementNumber: String? = null,

    @Column(nullable = false, length = 100)
    var neighborhood: String="",

    @Column(nullable = false, length = 100)
    var city: String="",

    @Column(nullable = false, length = 2)
    var state: String="",

    @Column(nullable = false, length = 9)
    var zipCode: String="",

    @Column(length = 100)
    var reference: String? = null,

    @Column(name = "is_default", nullable = false)
    val isDefault: Boolean = true,

    @Column(length = 50)
    val label: String? = null, // Casa, Trabalho, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val customer: UserEntity? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: ZonedDateTime = ZonedDateTime.now()
)
