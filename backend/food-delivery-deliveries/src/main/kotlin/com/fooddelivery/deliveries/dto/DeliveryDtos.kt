package com.fooddelivery.deliveries.dto

import com.fooddelivery.common.enums.DeliveryStatus
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.util.UUID

data class AssignDeliveryRequest(
    @NotNull
    val deliveryPersonId: UUID
)

data class UpdateDeliveryStatusRequest(
    @NotNull
    val status: DeliveryStatus,

    @Size(max = 500)
    val reason: String? = null,

    val pickupLatitude: BigDecimal? = null,
    val pickupLongitude: BigDecimal? = null,
    val deliveryLatitude: BigDecimal? = null,
    val deliveryLongitude: BigDecimal? = null,

    val pickupPhotoUrl: String? = null,
    val deliveryPhotoUrl: String? = null,
    val customerSignatureUrl: String? = null
)

data class DeliveryResponse(
    val id: UUID,
    val orderId: UUID,
    val orderNumber: String,
    val deliveryPersonId: UUID?,
    val deliveryPersonName: String?,
    val status: DeliveryStatus,
    val pickupAddress: String,
    val deliveryAddress: String,
    val pickupLatitude: BigDecimal?,
    val pickupLongitude: BigDecimal?,
    val deliveryLatitude: BigDecimal?,
    val deliveryLongitude: BigDecimal?,
    val pickupCode: String,
    val deliveryCode: String,
    val pickupPhotoUrl: String?,
    val deliveryPhotoUrl: String?,
    val customerSignatureUrl: String?,
    val distanceKm: BigDecimal?,
    val estimatedDurationMinutes: Int?,
    val actualDurationMinutes: Int?,
    val assignedAt: java.time.OffsetDateTime?,
    val acceptedAt: java.time.OffsetDateTime?,
    val pickedUpAt: java.time.OffsetDateTime?,
    val deliveredAt: java.time.OffsetDateTime?,
    val createdAt: java.time.OffsetDateTime,
    val updatedAt: java.time.OffsetDateTime
) {
    companion object {
        fun fromEntity(delivery: com.fooddelivery.deliveries.domain.Delivery): DeliveryResponse {
            return DeliveryResponse(
                id = delivery.id,
                orderId = delivery.order?.id ?: UUID.randomUUID(),
                orderNumber = delivery.order?.orderNumber ?: "Unknown",
                deliveryPersonId = delivery.deliveryPerson?.id,
                deliveryPersonName = delivery.deliveryPerson?.let { "${it.firstName} ${it.lastName}" },
                status = delivery.status,
                pickupAddress = delivery.pickupAddress?.getFullAddress() ?: "Unknown",
                deliveryAddress = delivery.deliveryAddress?.getFullAddress() ?: "Unknown",
                pickupLatitude = delivery.pickupLatitude,
                pickupLongitude = delivery.pickupLongitude,
                deliveryLatitude = delivery.deliveryLatitude,
                deliveryLongitude = delivery.deliveryLongitude,
                pickupCode = delivery.pickupCode,
                deliveryCode = delivery.deliveryCode,
                pickupPhotoUrl = delivery.pickupPhotoUrl,
                deliveryPhotoUrl = delivery.deliveryPhotoUrl,
                customerSignatureUrl = delivery.customerSignatureUrl,
                distanceKm = delivery.distanceKm,
                estimatedDurationMinutes = delivery.estimatedDurationMinutes,
                actualDurationMinutes = delivery.actualDurationMinutes,
                assignedAt = delivery.assignedAt,
                acceptedAt = delivery.acceptedAt,
                pickedUpAt = delivery.pickedUpAt,
                deliveredAt = delivery.deliveredAt,
                createdAt = delivery.createdAt,
                updatedAt = delivery.updatedAt
            )
        }
    }
}

data class DeliverySummaryResponse(
    val id: UUID,
    val orderNumber: String,
    val status: DeliveryStatus,
    val pickupAddress: String,
    val deliveryAddress: String,
    val distanceKm: BigDecimal?,
    val estimatedDurationMinutes: Int?,
    val assignedAt: java.time.OffsetDateTime?,
    val createdAt: java.time.OffsetDateTime
) {
    companion object {
        fun fromEntity(delivery: com.fooddelivery.deliveries.domain.Delivery): DeliverySummaryResponse {
            return DeliverySummaryResponse(
                id = delivery.id,
                orderNumber = delivery.order?.orderNumber ?: "Unknown",
                status = delivery.status,
                pickupAddress = delivery.pickupAddress?.getFullAddress() ?: "Unknown",
                deliveryAddress = delivery.deliveryAddress?.getFullAddress() ?: "Unknown",
                distanceKm = delivery.distanceKm,
                estimatedDurationMinutes = delivery.estimatedDurationMinutes,
                assignedAt = delivery.assignedAt,
                createdAt = delivery.createdAt
            )
        }
    }
}

data class NearbyDeliveryRequest(
    @NotNull
    val latitude: BigDecimal,

    @NotNull
    val longitude: BigDecimal,

    val radiusKm: Double = 5.0
)