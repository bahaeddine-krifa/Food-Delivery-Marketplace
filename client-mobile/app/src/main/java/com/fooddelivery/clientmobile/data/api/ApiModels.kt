package com.fooddelivery.clientmobile.data.api

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

// ==================== COMMON RESPONSE WRAPPER ====================

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String? = null,
    val timestamp: String = "",
    @SerializedName("requestId") val requestId: String? = null
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(true, data)
        fun <T> error(message: String): ApiResponse<T> = ApiResponse(false, null, message)
    }
}

@Serializable
data class PageResponse<T>(
    val content: List<T>,
    val pageable: PageableInfo,
    val totalElements: Long,
    val totalPages: Int,
    val first: Boolean,
    val last: Boolean,
    val numberOfElements: Int,
    val empty: Boolean
)

@Serializable
data class PageableInfo(
    val pageNumber: Int,
    val pageSize: Int,
    val sort: SortInfo,
    val offset: Long,
    val paged: Boolean,
    val unpaged: Boolean
)

@Serializable
data class SortInfo(
    val sorted: Boolean,
    val unsorted: Boolean,
    val empty: Boolean
)

// ==================== AUTH DTOs ====================

@Serializable
data class RegisterRequest(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("phone") val phone: String? = null,
    @SerialName("role") val role: UserRole = UserRole.CUSTOMER
)

@Serializable
data class LoginRequest(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("rememberMe") val rememberMe: Boolean = false
)

@Serializable
data class RefreshRequest(
    @SerialName("refreshToken") val refreshToken: String
)

@Serializable
data class LogoutRequest(
    @SerialName("refreshToken") val refreshToken: String
)

@Serializable
data class ForgotPasswordRequest(
    @SerialName("email") val email: String
)

@Serializable
data class ResetPasswordRequest(
    @SerialName("token") val token: String,
    @SerialName("newPassword") val newPassword: String
)

@Serializable
data class ChangePasswordRequest(
    @SerialName("currentPassword") val currentPassword: String,
    @SerialName("newPassword") val newPassword: String
)

@Serializable
data class VerifyEmailRequest(
    @SerialName("token") val token: String
)

@Serializable
data class AuthResponse(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
    @SerialName("tokenType") val tokenType: String = "Bearer",
    @SerialName("expiresIn") val expiresIn: Long,
    @SerialName("user") val user: UserProfileResponse
)

@Serializable
data class UserProfileResponse(
    val id: String,
    @SerialName("email") val email: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("phone") val phone: String?,
    @SerialName("avatarUrl") val avatarUrl: String?,
    @SerialName("role") val role: UserRole,
    @SerialName("status") val status: UserStatus,
    @SerialName("emailVerified") val emailVerified: Boolean,
    @SerialName("createdAt") val createdAt: String
)

@Serializable
data class TokenResponse(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
    @SerialName("tokenType") val tokenType: String = "Bearer",
    @SerialName("expiresIn") val expiresIn: Long
)

// ==================== ENUMS ====================

@Serializable
enum class UserRole(@SerialName("value") val value: String) {
    CUSTOMER("CUSTOMER"),
    DELIVERY_PERSON("DELIVERY_PERSON"),
    RESTAURANT_OWNER("RESTAURANT_OWNER"),
    ADMIN("ADMIN")
}

@Serializable
enum class UserStatus(@SerialName("value") val value: String) {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    SUSPENDED("SUSPENDED"),
    PENDING_VERIFICATION("PENDING_VERIFICATION"),
    DELETED("DELETED")
}

@Serializable
enum class OrderStatus(@SerialName("value") val value: String) {
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED"),
    PREPARING("PREPARING"),
    READY("READY"),
    ASSIGNED("ASSIGNED"),
    PICKED_UP("PICKED_UP"),
    DELIVERING("DELIVERING"),
    DELIVERED("DELIVERED"),
    CANCELLED("CANCELLED")
}

@Serializable
enum class RestaurantStatus(@SerialName("value") val value: String) {
    DRAFT("DRAFT"),
    PENDING_APPROVAL("PENDING_APPROVAL"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    SUSPENDED("SUSPENDED"),
    CLOSED("CLOSED")
}

@Serializable
enum class DayOfWeek(@SerialName("value") val value: String) {
    MONDAY("MONDAY"),
    TUESDAY("TUESDAY"),
    WEDNESDAY("WEDNESDAY"),
    THURSDAY("THURSDAY"),
    FRIDAY("FRIDAY"),
    SATURDAY("SATURDAY"),
    SUNDAY("SUNDAY")
}

@Serializable
enum class ReviewTargetType(@SerialName("value") val value: String) {
    RESTAURANT("RESTAURANT"),
    DELIVERY_PERSON("DELIVERY_PERSON"),
    PRODUCT("PRODUCT")
}

@Serializable
enum class AddressType(@SerialName("value") val value: String) {
    HOME("HOME"),
    WORK("WORK"),
    OTHER("OTHER")
}

@Serializable
enum class PaymentMethod(@SerialName("value") val value: String) {
    CREDIT_CARD("CREDIT_CARD"),
    PAYPAL("PAYPAL"),
    APPLE_PAY("APPLE_PAY"),
    GOOGLE_PAY("GOOGLE_PAY"),
    CASH("CASH")
}

@Serializable
enum class PaymentStatus(@SerialName("value") val value: String) {
    PENDING("PENDING"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED"),
    REFUNDED("REFUNDED"),
    PARTIALLY_REFUNDED("PARTIALLY_REFUNDED")
}

// ==================== RESTAURANT DTOs ====================

@Serializable
data class Restaurant(
    val id: String,
    val name: String,
    val slug: String,
    val description: String?,
    val phone: String,
    val email: String?,
    @SerialName("logoUrl") val logoUrl: String?,
    @SerialName("coverImageUrl") val coverImageUrl: String?,
    @SerialName("cuisineType") val cuisineType: String?,
    val status: RestaurantStatus,
    @SerialName("addressId") val addressId: String,
    @SerialName("deliveryRadiusKm") val deliveryRadiusKm: Double,
    @SerialName("minimumOrderAmount") val minimumOrderAmount: Double,
    @SerialName("deliveryFee") val deliveryFee: Double,
    @SerialName("preparationTimeMinutes") val preparationTimeMinutes: Int,
    @SerialName("averageRating") val averageRating: Double,
    @SerialName("totalReviews") val totalReviews: Int,
    @SerialName("isOpenNow") val isOpenNow: Boolean,
    @SerialName("rejectionReason") val rejectionReason: String?,
    @SerialName("approvedAt") val approvedAt: String?,
    @SerialName("approvedBy") val approvedBy: String?,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String
)

@Serializable
data class RestaurantHours(
    val id: String,
    @SerialName("dayOfWeek") val dayOfWeek: DayOfWeek,
    @SerialName("openTime") val openTime: String?,
    @SerialName("closeTime") val closeTime: String?,
    @SerialName("isClosed") val isClosed: Boolean
)

// ==================== CATEGORY DTOs ====================

@Serializable
data class Category(
    val id: String,
    val name: String,
    val description: String?,
    @SerialName("imageUrl") val imageUrl: String?,
    @SerialName("displayOrder") val displayOrder: Int,
    @SerialName("isActive") val isActive: Boolean,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String
)

// ==================== PRODUCT DTOs ====================

@Serializable
data class Product(
    val id: String,
    @SerialName("restaurantId") val restaurantId: String,
    @SerialName("categoryId") val categoryId: String?,
    val name: String,
    val description: String?,
    val price: Double,
    @SerialName("imageUrl") val imageUrl: String?,
    @SerialName("additionalImages") val additionalImages: List<String>?,
    @SerialName("isAvailable") val isAvailable: Boolean,
    @SerialName("isVegetarian") val isVegetarian: Boolean,
    @SerialName("isVegan") val isVegan: Boolean,
    @SerialName("isGlutenFree") val isGlutenFree: Boolean,
    val allergens: String?,
    val tags: List<String>?,
    @SerialName("preparationTimeMinutes") val preparationTimeMinutes: Int?,
    @SerialName("displayOrder") val displayOrder: Int,
    @SerialName("averageRating") val averageRating: Double,
    @SerialName("totalReviews") val totalReviews: Int,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String
)

// ==================== ORDER DTOs ====================

@Serializable
data class OrderItemResponse(
    val id: String,
    @SerialName("productId") val productId: String,
    @SerialName("productName") val productName: String,
    @SerialName("unitPrice") val unitPrice: Double,
    val quantity: Int,
    val notes: String?,
    @SerialName("lineTotal") val lineTotal: Double
)

@Serializable
data class OrderResponse(
    val id: String,
    @SerialName("orderNumber") val orderNumber: String,
    @SerialName("restaurantId") val restaurantId: String,
    @SerialName("restaurantName") val restaurantName: String,
    @SerialName("customerId") val customerId: String,
    val status: OrderStatus,
    val subtotal: Double,
    @SerialName("taxAmount") val taxAmount: Double,
    @SerialName("deliveryFee") val deliveryFee: Double,
    @SerialName("discountAmount") val discountAmount: Double,
    @SerialName("totalAmount") val totalAmount: Double,
    @SerialName("customerNotes") val customerNotes: String?,
    @SerialName("restaurantNotes") val restaurantNotes: String?,
    @SerialName("estimatedPreparationMinutes") val estimatedPreparationMinutes: Int?,
    @SerialName("estimatedDeliveryMinutes") val estimatedDeliveryMinutes: Int?,
    @SerialName("confirmedAt") val confirmedAt: String?,
    @SerialName("preparingAt") val preparingAt: String?,
    @SerialName("readyAt") val readyAt: String?,
    @SerialName("pickedUpAt") val pickedUpAt: String?,
    @SerialName("deliveredAt") val deliveredAt: String?,
    @SerialName("cancelledAt") val cancelledAt: String?,
    @SerialName("cancellationReason") val cancellationReason: String?,
    val items: List<OrderItemResponse>,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String
)

@Serializable
data class OrderSummaryResponse(
    val id: String,
    @SerialName("orderNumber") val orderNumber: String,
    @SerialName("restaurantName") val restaurantName: String,
    val status: OrderStatus,
    @SerialName("totalAmount") val totalAmount: Double,
    @SerialName("itemCount") val itemCount: Int,
    @SerialName("createdAt") val createdAt: String
)

@Serializable
data class OrderStatusHistoryResponse(
    val id: String,
    @SerialName("fromStatus") val fromStatus: OrderStatus?,
    @SerialName("toStatus") val toStatus: OrderStatus,
    @SerialName("changedBy") val changedBy: String,
    @SerialName("changeReason") val changeReason: String?,
    val metadata: String?,
    @SerialName("createdAt") val createdAt: String
)

@Serializable
data class CreateOrderRequest(
    @SerialName("restaurantId") val restaurantId: String,
    @SerialName("addressId") val addressId: String,
    @SerialName("idempotencyKey") val idempotencyKey: String,
    @SerialName("customerNotes") val customerNotes: String? = null,
    val items: List<OrderItemRequest>
)

@Serializable
data class OrderItemRequest(
    @SerialName("productId") val productId: String,
    val quantity: Int,
    val notes: String? = null
)

@Serializable
data class UpdateOrderStatusRequest(
    val status: OrderStatus,
    val reason: String? = null
)

// ==================== ADDRESS DTOs ====================

@Serializable
data class AddressResponse(
    val id: String,
    val label: String,
    val street: String,
    val building: String?,
    val floor: String?,
    val apartment: String?,
    val city: String,
    val state: String,
    @SerialName("postalCode") val postalCode: String,
    val country: String,
    val latitude: Double?,
    val longitude: Double?,
    val instructions: String?,
    @SerialName("isDefault") val isDefault: Boolean,
    @SerialName("addressType") val addressType: AddressType,
    @SerialName("fullAddress") val fullAddress: String,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String
)

@Serializable
data class CreateAddressRequest(
    val label: String,
    val street: String,
    val building: String? = null,
    val floor: String? = null,
    val apartment: String? = null,
    val city: String,
    val state: String,
    @SerialName("postalCode") val postalCode: String,
    val country: String = "TN",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val instructions: String? = null,
    @SerialName("isDefault") val isDefault: Boolean = false,
    @SerialName("addressType") val addressType: AddressType = AddressType.HOME
)

@Serializable
data class UpdateAddressRequest(
    val label: String? = null,
    val street: String? = null,
    val building: String? = null,
    val floor: String? = null,
    val apartment: String? = null,
    val city: String? = null,
    val state: String? = null,
    @SerialName("postalCode") val postalCode: String? = null,
    val country: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val instructions: String? = null,
    @SerialName("isDefault") val isDefault: Boolean? = null,
    @SerialName("addressType") val addressType: AddressType? = null
)

@Serializable
data class SetDefaultAddressRequest(
    @SerialName("addressId") val addressId: String
)

// ==================== REVIEW DTOs ====================

@Serializable
data class ReviewResponse(
    val id: String,
    @SerialName("customerId") val customerId: String,
    @SerialName("customerName") val customerName: String,
    @SerialName("orderId") val orderId: String,
    @SerialName("orderNumber") val orderNumber: String,
    @SerialName("targetType") val targetType: ReviewTargetType,
    @SerialName("targetId") val targetId: String,
    val rating: Int,
    val comment: String?,
    @SerialName("ratingsBreakdown") val ratingsBreakdown: String?,
    @SerialName("isVerified") val isVerified: Boolean,
    @SerialName("isFlagged") val isFlagged: Boolean,
    @SerialName("flagReason") val flagReason: String?,
    val response: String?,
    @SerialName("respondedAt") val respondedAt: String?,
    @SerialName("helpfulCount") val helpfulCount: Int,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String
)

@Serializable
data class ReviewStatsResponse(
    @SerialName("averageRating") val averageRating: Double,
    @SerialName("totalReviews") val totalReviews: Long,
    @SerialName("ratingDistribution") val ratingDistribution: Map<Int, Long>
)

@Serializable
data class CreateReviewRequest(
    @SerialName("orderId") val orderId: String,
    @SerialName("targetType") val targetType: ReviewTargetType,
    @SerialName("targetId") val targetId: String,
    val rating: Int,
    val comment: String? = null,
    @SerialName("ratingsBreakdown") val ratingsBreakdown: String? = null
)

@Serializable
data class UpdateReviewRequest(
    val rating: Int? = null,
    val comment: String? = null,
    @SerialName("ratingsBreakdown") val ratingsBreakdown: String? = null
)

@Serializable
data class RespondToReviewRequest(
    val response: String
)

@Serializable
data class FlagReviewRequest(
    val reason: String
)

// ==================== DELIVERY DTOs ====================

@Serializable
data class DeliveryResponse(
    val id: String,
    @SerialName("orderId") val orderId: String,
    @SerialName("orderNumber") val orderNumber: String,
    @SerialName("deliveryPersonId") val deliveryPersonId: String?,
    @SerialName("deliveryPersonName") val deliveryPersonName: String?,
    val status: DeliveryStatus,
    @SerialName("pickupLat") val pickupLat: Double?,
    @SerialName("pickupLng") val pickupLng: Double?,
    @SerialName("deliveryLat") val deliveryLat: Double?,
    @SerialName("deliveryLng") val deliveryLng: Double?,
    @SerialName("pickupPhotoUrl") val pickupPhotoUrl: String?,
    @SerialName("deliveryPhotoUrl") val deliveryPhotoUrl: String?,
    @SerialName("pickupAt") val pickupAt: String?,
    @SerialName("deliveredAt") val deliveredAt: String?,
    @SerialName("estimatedDeliveryAt") val estimatedDeliveryAt: String?,
    @SerialName("distanceKm") val distanceKm: Double?,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String
)

@Serializable
enum class DeliveryStatus(@SerialName("value") val value: String) {
    ASSIGNED("ASSIGNED"),
    PICKED_UP("PICKED_UP"),
    IN_TRANSIT("IN_TRANSIT"),
    DELIVERED("DELIVERED"),
    FAILED("FAILED")
}

// ==================== PAYMENT DTOs ====================

@Serializable
data class PaymentResponse(
    val id: String,
    @SerialName("orderId") val orderId: String,
    @SerialName("orderNumber") val orderNumber: String,
    @SerialName("customerId") val customerId: String,
    @SerialName("customerName") val customerName: String,
    val amount: Double,
    val method: PaymentMethod,
    val status: PaymentStatus,
    @SerialName("transactionId") val transactionId: String?,
    @SerialName("failureReason") val failureReason: String?,
    @SerialName("refundedAmount") val refundedAmount: Double?,
    @SerialName("refundReason") val refundReason: String?,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String
)