package com.fooddelivery.users.domain

import com.fooddelivery.common.domain.AuditableEntity
import com.fooddelivery.common.enums.AddressType
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(
    name = "addresses",
    indexes = [
        Index(name = "idx_addresses_user_id", columnList = "user_id"),
        Index(name = "idx_addresses_user_default", columnList = "user_id,is_default"),
        Index(name = "idx_addresses_coordinates", columnList = "latitude,longitude")
    ]
)
class Address : AuditableEntity() {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null

    @Column(nullable = false, length = 50)
    var label: String = ""

    @Column(name = "street", nullable = false, length = 200)
    var street: String = ""

    @Column(name = "building", length = 50)
    var building: String? = null

    @Column(name = "floor", length = 20)
    var floor: String? = null

    @Column(name = "apartment", length = 20)
    var apartment: String? = null

    @Column(name = "city", nullable = false, length = 100)
    var city: String = ""

    @Column(name = "state", nullable = false, length = 100)
    var state: String = ""

    @Column(name = "postal_code", nullable = false, length = 20)
    var postalCode: String = ""

    @Column(name = "country", nullable = false, length = 2)
    var country: String = "TN"

    @Column(name = "latitude", precision = 10, scale = 8)
    var latitude: BigDecimal? = null

    @Column(name = "longitude", precision = 11, scale = 8)
    var longitude: BigDecimal? = null

    @Column(name = "instructions", columnDefinition = "TEXT")
    var instructions: String? = null

    @Column(name = "is_default", nullable = false)
    var isDefault: Boolean = false

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false, length = 20)
    var addressType: AddressType = AddressType.HOME

    fun getFullAddress(): String {
        val parts = mutableListOf(street)
        building?.let { parts.add(it) }
        floor?.let { parts.add("Floor $it") }
        apartment?.let { parts.add("Apt $it") }
        parts.add("$city, $state $postalCode")
        return parts.joinToString(", ")
    }
}