package com.fooddelivery.clientmobile.domain.model

import java.time.OffsetDateTime
import java.util.UUID

// ==================== AUTH DOMAIN ====================

data class User(
    val id: UUID,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String?,
    val avatarUrl: String?,
    val role: UserRole,
    val status: UserStatus,
    val emailVerified: Boolean,
    val createdAt: OffsetDateTime
) {
    val fullName: String
        get() = "$firstName $lastName"
}

enum class UserRole {
    CUSTOMER, DELIVERY_PERSON, RESTAURANT_OWNER, ADMIN
}

enum class UserStatus {
    ACTIVE, INACTIVE, SUSPENDED, PENDING_VERIFICATION, DELETED
}

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val tokenType: String = "Bearer"
)

// ==================== RESTAURANT DOMAIN ====================

data class Restaurant(
    val id: UUID,
    val name: String,
    val slug: String,
    val description: String?,
    val phone: String,
    val email: String?,
    val logoUrl: String?,
    val coverImageUrl: String?,
    val cuisineType: String?,
    val status: RestaurantStatus,
    val addressId: UUID,
    val deliveryRadiusKm: Double,
    val minimumOrderAmount: Double,
    val deliveryFee: Double,
    val preparationTimeMinutes: Int,
    val averageRating: Double,
    val totalReviews: Int,
    val isOpenNow: Boolean,
    val rejectionReason: String?,
    val approvedAt: OffsetDateTime?,
    val approvedBy: UUID?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class RestaurantHours(
    val id: UUID,
    val dayOfWeek: DayOfWeek,
    val openTime: String?, // HH:mm format
    val closeTime: String?, // HH:mm format
    val isClosed: Boolean
)

enum class RestaurantStatus {
    DRAFT, PENDING_APPROVAL, APPROVED, REJECTED, SUSPENDED, CLOSED
}

enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

// ==================== CATEGORY DOMAIN ====================

data class Category(
    val id: UUID,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val displayOrder: Int,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

// ==================== PRODUCT DOMAIN ====================

data class Product(
    val id: UUID,
    val restaurantId: UUID,
    val categoryId: UUID?,
    val name: String,
    val description: String?,
    val price: Double,
    val imageUrl: String?,
    val additionalImages: List<String>,
    val isAvailable: Boolean,
    val isVegetarian: Boolean,
    val isVegan: Boolean,
    val isGlutenFree: Boolean,
    val allergens: String?,
    val tags: List<String>,
    val preparationTimeMinutes: Int?,
    val displayOrder: Int,
    val averageRating: Double,
    val totalReviews: Int,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
) {
    val dietaryTags: List<String>
        get() = buildList {
            if (isVegetarian) add("Vegetarian")
            if (isVegan) add("Vegan")
            if (isGlutenFree) add("Gluten Free")
            if (!allergens.isNullOrEmpty()) add("Allergens: $allergens")
            tags.forEach { add(it) }
        }

    val rating: Double
        get() = averageRating

    fun toCartItem(quantity: Int = 1, notes: String? = null): CartItem {
        return CartItem(product = this, quantity = quantity, notes = notes)
    }
}

// ==================== ORDER DOMAIN ====================

data class OrderItem(
    val id: UUID,
    val productId: UUID,
    val productName: String,
    val unitPrice: Double,
    val quantity: Int,
    val notes: String?,
    val lineTotal: Double
)

data class Order(
    val id: UUID,
    val orderNumber: String,
    val restaurantId: UUID,
    val restaurantName: String,
    val customerId: UUID,
    val status: OrderStatus,
    val subtotal: Double,
    val taxAmount: Double,
    val deliveryFee: Double,
    val discountAmount: Double,
    val totalAmount: Double,
    val customerNotes: String?,
    val restaurantNotes: String?,
    val estimatedPreparationMinutes: Int?,
    val estimatedDeliveryMinutes: Int?,
    val confirmedAt: OffsetDateTime?,
    val preparingAt: OffsetDateTime?,
    val readyAt: OffsetDateTime?,
    val pickedUpAt: OffsetDateTime?,
    val deliveredAt: OffsetDateTime?,
    val cancelledAt: OffsetDateTime?,
    val cancellationReason: String?,
    val items: List<OrderItem>,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class OrderSummary(
    val id: UUID,
    val orderNumber: String,
    val restaurantName: String,
    val status: OrderStatus,
    val totalAmount: Double,
    val itemCount: Int,
    val createdAt: OffsetDateTime
)

data class OrderStatusHistory(
    val id: UUID,
    val fromStatus: OrderStatus?,
    val toStatus: OrderStatus,
    val changedBy: UUID,
    val changeReason: String?,
    val metadata: String?,
    val createdAt: OffsetDateTime
)

enum class OrderStatus {
    PENDING, ACCEPTED, REJECTED, PREPARING, READY, ASSIGNED, PICKED_UP, DELIVERING, DELIVERED, CANCELLED
}

data class CreateOrderRequest(
    val restaurantId: UUID,
    val addressId: UUID,
    val idempotencyKey: String,
    val customerNotes: String?,
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    val productId: UUID,
    val quantity: Int,
    val notes: String?
)

// ==================== ADDRESS DOMAIN ====================

data class Address(
    val id: UUID,
    val label: String,
    val street: String,
    val building: String?,
    val floor: String?,
    val apartment: String?,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String,
    val latitude: Double?,
    val longitude: Double?,
    val instructions: String?,
    val isDefault: Boolean,
    val addressType: AddressType,
    val fullAddress: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

enum class AddressType {
    HOME, WORK, OTHER
}

data class CreateAddressRequest(
    val label: String,
    val street: String,
    val building: String?,
    val floor: String?,
    val apartment: String?,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String = "TN",
    val latitude: Double?,
    val longitude: Double?,
    val instructions: String?,
    val isDefault: Boolean = false,
    val addressType: AddressType = AddressType.HOME
)

data class UpdateAddressRequest(
    val label: String? = null,
    val street: String? = null,
    val building: String? = null,
    val floor: String? = null,
    val apartment: String? = null,
    val city: String? = null,
    val state: String? = null,
    val postalCode: String? = null,
    val country: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val instructions: String? = null,
    val isDefault: Boolean? = null,
    val addressType: AddressType? = null
)

// ==================== REVIEW DOMAIN ====================

data class Review(
    val id: UUID,
    val customerId: UUID,
    val customerName: String,
    val orderId: UUID,
    val orderNumber: String,
    val targetType: ReviewTargetType,
    val targetId: UUID,
    val rating: Int,
    val comment: String?,
    val ratingsBreakdown: String?,
    val isVerified: Boolean,
    val isFlagged: Boolean,
    val flagReason: String?,
    val response: String?,
    val respondedAt: OffsetDateTime?,
    val helpfulCount: Int,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class ReviewStats(
    val averageRating: Double,
    val totalReviews: Long,
    val ratingDistribution: Map<Int, Long>
)

enum class ReviewTargetType {
    RESTAURANT, DELIVERY_PERSON, PRODUCT
}

data class CreateReviewRequest(
    val orderId: UUID,
    val targetType: ReviewTargetType,
    val targetId: UUID,
    val rating: Int,
    val comment: String?,
    val ratingsBreakdown: String?
)

data class UpdateReviewRequest(
    val rating: Int? = null,
    val comment: String? = null,
    val ratingsBreakdown: String? = null
)

// ==================== DELIVERY DOMAIN ====================

data class Delivery(
    val id: UUID,
    val orderId: UUID,
    val orderNumber: String,
    val deliveryPersonId: UUID?,
    val deliveryPersonName: String?,
    val status: DeliveryStatus,
    val pickupLat: Double?,
    val pickupLng: Double?,
    val deliveryLat: Double?,
    val deliveryLng: Double?,
    val pickupPhotoUrl: String?,
    val deliveryPhotoUrl: String?,
    val pickupAt: OffsetDateTime?,
    val deliveredAt: OffsetDateTime?,
    val estimatedDeliveryAt: OffsetDateTime?,
    val distanceKm: Double?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

enum class DeliveryStatus {
    ASSIGNED, PICKED_UP, IN_TRANSIT, DELIVERED, FAILED
}

// ==================== PAYMENT DOMAIN ====================

data class Payment(
    val id: UUID,
    val orderId: UUID,
    val orderNumber: String,
    val customerId: UUID,
    val customerName: String,
    val amount: Double,
    val method: PaymentMethod,
    val status: PaymentStatus,
    val transactionId: String?,
    val failureReason: String?,
    val refundedAmount: Double?,
    val refundReason: String?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

enum class PaymentMethod {
    CREDIT_CARD, PAYPAL, APPLE_PAY, GOOGLE_PAY, CASH
}

enum class PaymentStatus {
    PENDING, COMPLETED, FAILED, REFUNDED, PARTIALLY_REFUNDED
}

// ==================== CART DOMAIN ====================

data class CartItem(
    val product: Product,
    val quantity: Int,
    val notes: String? = null
) {
    val totalPrice: Double
        get() = product.price * quantity
}

data class Cart(
    val items: List<CartItem> = emptyList(),
    val restaurantId: UUID? = null
) {
    val itemCount: Int
        get() = items.sumOf { it.quantity }

    val subtotal: Double
        get() = items.sumOf { it.totalPrice }

    val isEmpty: Boolean
        get() = items.isEmpty()

    fun addItem(product: Product, quantity: Int = 1, notes: String? = null): Cart {
        if (restaurantId != null && restaurantId != product.restaurantId) {
            // Clear cart if adding from different restaurant
            return Cart(
                items = listOf(CartItem(product, quantity, notes)),
                restaurantId = product.restaurantId
            )
        }
        val existingIndex = items.indexOfFirst { it.product.id == product.id && it.notes == notes }
        val newItems = if (existingIndex >= 0) {
            items.mapIndexed { index, item ->
                if (index == existingIndex) item.copy(quantity = item.quantity + quantity) else item
            }
        } else {
            items + CartItem(product, quantity, notes)
        }
        return copy(items = newItems, restaurantId = product.restaurantId)
    }

    fun removeItem(productId: UUID, notes: String? = null): Cart {
        val newItems = items.filter { it.product.id != productId || it.notes != notes }
        return copy(items = newItems, restaurantId = if (newItems.isEmpty()) null else restaurantId)
    }

    fun updateQuantity(productId: UUID, quantity: Int, notes: String? = null): Cart {
        if (quantity <= 0) return removeItem(productId, notes)
        val newItems = items.map { item ->
            if (item.product.id == productId && item.notes == notes) item.copy(quantity = quantity) else item
        }
        return copy(items = newItems)
    }

    fun clear(): Cart = copy(items = emptyList(), restaurantId = null)
}