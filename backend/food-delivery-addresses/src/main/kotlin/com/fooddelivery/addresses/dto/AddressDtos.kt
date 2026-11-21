package com.fooddelivery.addresses.dto

import com.fooddelivery.common.enums.AddressType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class CreateAddressRequest(
    @NotBlank
    @Size(max = 50)
    val label: String,

    @NotBlank
    @Size(max = 200)
    val street: String,

    @Size(max = 50)
    val building: String? = null,

    @Size(max = 20)
    val floor: String? = null,

    @Size(max = 20)
    val apartment: String? = null,

    @NotBlank
    @Size(max = 100)
    val city: String,

    @NotBlank
    @Size(max = 100)
    val state: String,

    @NotBlank
    @Size(max = 20)
    val postalCode: String,

    @NotBlank
    @Size(min = 2, max = 2)
    val country: String = "TN",

    val latitude: BigDecimal? = null,

    val longitude: BigDecimal? = null,

    @Size(max = 1000)
    val instructions: String? = null,

    val isDefault: Boolean = false,

    @NotNull
    val addressType: AddressType = AddressType.HOME
)

data class UpdateAddressRequest(
    @Size(max = 50)
    val label: String? = null,

    @Size(max = 200)
    val street: String? = null,

    @Size(max = 50)
    val building: String? = null,

    @Size(max = 20)
    val floor: String? = null,

    @Size(max = 20)
    val apartment: String? = null,

    @Size(max = 100)
    val city: String? = null,

    @Size(max = 100)
    val state: String? = null,

    @Size(max = 20)
    val postalCode: String? = null,

    @Size(min = 2, max = 2)
    val country: String? = null,

    val latitude: BigDecimal? = null,

    val longitude: BigDecimal? = null,

    @Size(max = 1000)
    val instructions: String? = null,

    val isDefault: Boolean? = null,

    val addressType: AddressType? = null
)

data class AddressResponse(
    val id: java.util.UUID,
    val label: String,
    val street: String,
    val building: String?,
    val floor: String?,
    val apartment: String?,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String,
    val latitude: BigDecimal?,
    val longitude: BigDecimal?,
    val instructions: String?,
    val isDefault: Boolean,
    val addressType: AddressType,
    val fullAddress: String,
    val createdAt: java.time.OffsetDateTime,
    val updatedAt: java.time.OffsetDateTime
) {
    companion object {
        fun fromEntity(address: com.fooddelivery.users.domain.Address): AddressResponse {
            return AddressResponse(
                id = address.id,
                label = address.label,
                street = address.street,
                building = address.building,
                floor = address.floor,
                apartment = address.apartment,
                city = address.city,
                state = address.state,
                postalCode = address.postalCode,
                country = address.country,
                latitude = address.latitude,
                longitude = address.longitude,
                instructions = address.instructions,
                isDefault = address.isDefault,
                addressType = address.addressType,
                fullAddress = address.getFullAddress(),
                createdAt = address.createdAt,
                updatedAt = address.updatedAt
            )
        }
    }
}

data class SetDefaultAddressRequest(
    @NotNull
    val addressId: java.util.UUID
)