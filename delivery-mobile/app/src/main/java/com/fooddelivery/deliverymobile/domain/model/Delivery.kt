package com.fooddelivery.deliverymobile.domain.model

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

data class Delivery(
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
    val assignedAt: OffsetDateTime?,
    val acceptedAt: OffsetDateTime?,
    val pickedUpAt: OffsetDateTime?,
    val deliveredAt: OffsetDateTime?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
) {

    // Computed properties for UI
    val isActive: Boolean
        get() = status in activeStatuses

    val isCompleted: Boolean
        get() = status in completedStatuses

    val canAccept: Boolean
        get() = status == DeliveryStatus.ASSIGNED

    val canPickUp: Boolean
        get() = status == DeliveryStatus.ACCEPTED

    val canStartDelivering: Boolean
        get() = status == DeliveryStatus.PICKED_UP

    val canComplete: Boolean
        get() = status == DeliveryStatus.DELIVERING

    val canFail: Boolean
        get() = status in failAllowedStatuses

    val nextPossibleStatuses: List<DeliveryStatus>
        get() = when (status) {
            DeliveryStatus.ASSIGNED -> listOf(DeliveryStatus.ACCEPTED, DeliveryStatus.FAILED)
            DeliveryStatus.ACCEPTED -> listOf(DeliveryStatus.PICKED_UP, DeliveryStatus.FAILED)
            DeliveryStatus.PICKED_UP -> listOf(DeliveryStatus.DELIVERING, DeliveryStatus.FAILED)
            DeliveryStatus.DELIVERING -> listOf(DeliveryStatus.DELIVERED, DeliveryStatus.FAILED)
            else -> emptyList()
        }

    val statusDisplayName: String
        get() = when (status) {
            DeliveryStatus.PENDING_ASSIGNMENT -> "Pending Assignment"
            DeliveryStatus.ASSIGNED -> "Assigned"
            DeliveryStatus.ACCEPTED -> "Accepted"
            DeliveryStatus.PICKED_UP -> "Picked Up"
            DeliveryStatus.DELIVERING -> "Delivering"
            DeliveryStatus.DELIVERED -> "Delivered"
            DeliveryStatus.CANCELLED -> "Cancelled"
            DeliveryStatus.FAILED -> "Failed"
        }

    val statusColor: Int
        get() = when (status) {
            DeliveryStatus.PENDING_ASSIGNMENT -> 0xFF9E9E9E.toInt() // Grey
            DeliveryStatus.ASSIGNED -> 0xFF2196F3.toInt() // Blue
            DeliveryStatus.ACCEPTED -> 0xFF4CAF50.toInt() // Green
            DeliveryStatus.PICKED_UP -> 0xFFFF9800.toInt() // Orange
            DeliveryStatus.DELIVERING -> 0xFF3F51B5.toInt() // Indigo
            DeliveryStatus.DELIVERED -> 0xFF4CAF50.toInt() // Green
            DeliveryStatus.CANCELLED -> 0xFF9E9E9E.toInt() // Grey
            DeliveryStatus.FAILED -> 0xFFF44336.toInt() // Red
        }

    companion object {
        private val activeStatuses = setOf(
            DeliveryStatus.ASSIGNED,
            DeliveryStatus.ACCEPTED,
            DeliveryStatus.PICKED_UP,
            DeliveryStatus.DELIVERING
        )

        private val completedStatuses = setOf(
            DeliveryStatus.DELIVERED,
            DeliveryStatus.CANCELLED,
            DeliveryStatus.FAILED
        )

        private val failAllowedStatuses = setOf(
            DeliveryStatus.ASSIGNED,
            DeliveryStatus.ACCEPTED,
            DeliveryStatus.PICKED_UP,
            DeliveryStatus.DELIVERING
        )
    }
}

enum class DeliveryStatus {
    PENDING_ASSIGNMENT,
    ASSIGNED,
    ACCEPTED,
    PICKED_UP,
    DELIVERING,
    DELIVERED,
    CANCELLED,
    FAILED;

    val displayName: String
        get() = when (this) {
            PENDING_ASSIGNMENT -> "Pending Assignment"
            ASSIGNED -> "Assigned"
            ACCEPTED -> "Accepted"
            PICKED_UP -> "Picked Up"
            DELIVERING -> "Delivering"
            DELIVERED -> "Delivered"
            CANCELLED -> "Cancelled"
            FAILED -> "Failed"
        }

    val statusColor: Int
        get() = when (this) {
            PENDING_ASSIGNMENT -> 0xFF9E9E9E.toInt() // Grey
            ASSIGNED -> 0xFF2196F3.toInt() // Blue
            ACCEPTED -> 0xFF4CAF50.toInt() // Green
            PICKED_UP -> 0xFFFF9800.toInt() // Orange
            DELIVERING -> 0xFF3F51B5.toInt() // Indigo
            DELIVERED -> 0xFF4CAF50.toInt() // Green
            CANCELLED -> 0xFF9E9E9E.toInt() // Grey
            FAILED -> 0xFFF44336.toInt() // Red
        }
}

data class DeliverySummary(
    val id: UUID,
    val orderNumber: String,
    val status: DeliveryStatus,
    val pickupAddress: String,
    val deliveryAddress: String,
    val distanceKm: BigDecimal?,
    val estimatedDurationMinutes: Int?,
    val assignedAt: OffsetDateTime?,
    val createdAt: OffsetDateTime
) {
    val isActive: Boolean
        get() = status in setOf(DeliveryStatus.ASSIGNED, DeliveryStatus.ACCEPTED, DeliveryStatus.PICKED_UP, DeliveryStatus.DELIVERING)

    val statusDisplayName: String
        get() = when (status) {
            DeliveryStatus.PENDING_ASSIGNMENT -> "Pending Assignment"
            DeliveryStatus.ASSIGNED -> "Assigned"
            DeliveryStatus.ACCEPTED -> "Accepted"
            DeliveryStatus.PICKED_UP -> "Picked Up"
            DeliveryStatus.DELIVERING -> "Delivering"
            DeliveryStatus.DELIVERED -> "Delivered"
            DeliveryStatus.CANCELLED -> "Cancelled"
            DeliveryStatus.FAILED -> "Failed"
        }
}

data class OrderSummary(
    val id: UUID,
    val orderNumber: String,
    val totalAmount: BigDecimal,
    val customerName: String,
    val customerPhone: String,
    val restaurantName: String,
    val restaurantPhone: String
)

data class Address(
    val id: UUID,
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
    val fullAddress: String
)

enum class AddressType {
    HOME, WORK, OTHER
}