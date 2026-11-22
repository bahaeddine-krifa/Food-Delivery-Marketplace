package com.fooddelivery.deliverymobile.data.api

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

// ==================== COMMON API RESPONSE ====================

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T?,
    @SerializedName("message") val message: String?,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("requestId") val requestId: String?
)

data class PageResponse<T>(
    @SerializedName("content") val content: List<T>,
    @SerializedName("pageable") val pageable: Pageable,
    @SerializedName("totalElements") val totalElements: Long,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("last") val last: Boolean,
    @SerializedName("first") val first: Boolean,
    @SerializedName("number") val number: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("numberOfElements") val numberOfElements: Int,
    @SerializedName("empty") val empty: Boolean
)

data class Pageable(
    @SerializedName("pageNumber") val pageNumber: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("sort") val sort: Sort,
    @SerializedName("offset") val offset: Long,
    @SerializedName("paged") val paged: Boolean,
    @SerializedName("unpaged") val unpaged: Boolean
)

data class Sort(
    @SerializedName("sorted") val sorted: Boolean,
    @SerializedName("unsorted") val unsorted: Boolean,
    @SerializedName("empty") val empty: Boolean
)

// ==================== AUTH DTOs ====================

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("rememberMe") val rememberMe: Boolean = false
)

data class AuthResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("tokenType") val tokenType: String,
    @SerializedName("expiresIn") val expiresIn: Long,
    @SerializedName("user") val user: UserProfileResponse
)

data class TokenResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("tokenType") val tokenType: String,
    @SerializedName("expiresIn") val expiresIn: Long
)

data class RefreshRequest(
    @SerializedName("refreshToken") val refreshToken: String
)

data class LogoutRequest(
    @SerializedName("refreshToken") val refreshToken: String
)

data class ForgotPasswordRequest(
    @SerializedName("email") val email: String
)

data class ResetPasswordRequest(
    @SerializedName("token") val token: String,
    @SerializedName("newPassword") val newPassword: String
)

data class ChangePasswordRequest(
    @SerializedName("currentPassword") val currentPassword: String,
    @SerializedName("newPassword") val newPassword: String
)

data class UserProfileResponse(
    @SerializedName("id") val id: UUID,
    @SerializedName("email") val email: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?,
    @SerializedName("role") val role: UserRole,
    @SerializedName("status") val status: UserStatus,
    @SerializedName("emailVerified") val emailVerified: Boolean,
    @SerializedName("createdAt") val createdAt: OffsetDateTime
)

enum class UserRole {
    @SerializedName("CUSTOMER") CUSTOMER,
    @SerializedName("DELIVERY_PERSON") DELIVERY_PERSON,
    @SerializedName("RESTAURANT_OWNER") RESTAURANT_OWNER,
    @SerializedName("ADMIN") ADMIN
}

enum class UserStatus {
    @SerializedName("ACTIVE") ACTIVE,
    @SerializedName("INACTIVE") INACTIVE,
    @SerializedName("SUSPENDED") SUSPENDED,
    @SerializedName("PENDING_VERIFICATION") PENDING_VERIFICATION,
    @SerializedName("DELETED") DELETED
}

// ==================== DELIVERY DTOs ====================

data class DeliveryResponse(
    @SerializedName("id") val id: UUID,
    @SerializedName("orderId") val orderId: UUID,
    @SerializedName("orderNumber") val orderNumber: String,
    @SerializedName("deliveryPersonId") val deliveryPersonId: UUID?,
    @SerializedName("deliveryPersonName") val deliveryPersonName: String?,
    @SerializedName("status") val status: DeliveryStatus,
    @SerializedName("pickupAddress") val pickupAddress: String,
    @SerializedName("deliveryAddress") val deliveryAddress: String,
    @SerializedName("pickupLatitude") val pickupLatitude: BigDecimal?,
    @SerializedName("pickupLongitude") val pickupLongitude: BigDecimal?,
    @SerializedName("deliveryLatitude") val deliveryLatitude: BigDecimal?,
    @SerializedName("deliveryLongitude") val deliveryLongitude: BigDecimal?,
    @SerializedName("pickupCode") val pickupCode: String,
    @SerializedName("deliveryCode") val deliveryCode: String,
    @SerializedName("pickupPhotoUrl") val pickupPhotoUrl: String?,
    @SerializedName("deliveryPhotoUrl") val deliveryPhotoUrl: String?,
    @SerializedName("customerSignatureUrl") val customerSignatureUrl: String?,
    @SerializedName("distanceKm") val distanceKm: BigDecimal?,
    @SerializedName("estimatedDurationMinutes") val estimatedDurationMinutes: Int?,
    @SerializedName("actualDurationMinutes") val actualDurationMinutes: Int?,
    @SerializedName("assignedAt") val assignedAt: OffsetDateTime?,
    @SerializedName("acceptedAt") val acceptedAt: OffsetDateTime?,
    @SerializedName("pickedUpAt") val pickedUpAt: OffsetDateTime?,
    @SerializedName("deliveredAt") val deliveredAt: OffsetDateTime?,
    @SerializedName("createdAt") val createdAt: OffsetDateTime,
    @SerializedName("updatedAt") val updatedAt: OffsetDateTime
)

data class DeliverySummaryResponse(
    @SerializedName("id") val id: UUID,
    @SerializedName("orderNumber") val orderNumber: String,
    @SerializedName("status") val status: DeliveryStatus,
    @SerializedName("pickupAddress") val pickupAddress: String,
    @SerializedName("deliveryAddress") val deliveryAddress: String,
    @SerializedName("distanceKm") val distanceKm: BigDecimal?,
    @SerializedName("estimatedDurationMinutes") val estimatedDurationMinutes: Int?,
    @SerializedName("assignedAt") val assignedAt: OffsetDateTime?,
    @SerializedName("createdAt") val createdAt: OffsetDateTime
)

enum class DeliveryStatus(
    @SerializedName("value") val value: String
) {
    @SerializedName("PENDING_ASSIGNMENT") PENDING_ASSIGNMENT("PENDING_ASSIGNMENT"),
    @SerializedName("ASSIGNED") ASSIGNED("ASSIGNED"),
    @SerializedName("ACCEPTED") ACCEPTED("ACCEPTED"),
    @SerializedName("PICKED_UP") PICKED_UP("PICKED_UP"),
    @SerializedName("DELIVERING") DELIVERING("DELIVERING"),
    @SerializedName("DELIVERED") DELIVERED("DELIVERED"),
    @SerializedName("CANCELLED") CANCELLED("CANCELLED"),
    @SerializedName("FAILED") FAILED("FAILED")
}

data class UpdateDeliveryStatusRequest(
    @SerializedName("status") val status: DeliveryStatus,
    @SerializedName("reason") val reason: String? = null,
    @SerializedName("pickupLatitude") val pickupLatitude: BigDecimal? = null,
    @SerializedName("pickupLongitude") val pickupLongitude: BigDecimal? = null,
    @SerializedName("deliveryLatitude") val deliveryLatitude: BigDecimal? = null,
    @SerializedName("deliveryLongitude") val deliveryLongitude: BigDecimal? = null,
    @SerializedName("pickupPhotoUrl") val pickupPhotoUrl: String? = null,
    @SerializedName("deliveryPhotoUrl") val deliveryPhotoUrl: String? = null,
    @SerializedName("customerSignatureUrl") val customerSignatureUrl: String? = null
)

data class AssignDeliveryRequest(
    @SerializedName("deliveryPersonId") val deliveryPersonId: UUID
)

// ==================== ORDER DTOs (minimal for delivery) ====================

data class OrderSummaryForDelivery(
    @SerializedName("id") val id: UUID,
    @SerializedName("orderNumber") val orderNumber: String,
    @SerializedName("totalAmount") val totalAmount: BigDecimal,
    @SerializedName("customerName") val customerName: String,
    @SerializedName("customerPhone") val customerPhone: String,
    @SerializedName("restaurantName") val restaurantName: String,
    @SerializedName("restaurantPhone") val restaurantPhone: String
)

data class AddressResponse(
    @SerializedName("id") val id: UUID,
    @SerializedName("street") val street: String,
    @SerializedName("building") val building: String?,
    @SerializedName("floor") val floor: String?,
    @SerializedName("apartment") val apartment: String?,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("postalCode") val postalCode: String,
    @SerializedName("country") val country: String,
    @SerializedName("latitude") val latitude: BigDecimal?,
    @SerializedName("longitude") val longitude: BigDecimal?,
    @SerializedName("instructions") val instructions: String?,
    @SerializedName("isDefault") val isDefault: Boolean,
    @SerializedName("addressType") val addressType: AddressType,
    @SerializedName("fullAddress") val fullAddress: String
)

enum class AddressType {
    @SerializedName("HOME") HOME,
    @SerializedName("WORK") WORK,
    @SerializedName("OTHER") OTHER
}