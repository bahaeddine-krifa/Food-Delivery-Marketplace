package com.fooddelivery.admin.dto

import com.fooddelivery.common.enums.ApplicationStatus
import com.fooddelivery.common.enums.OrderStatus
import com.fooddelivery.common.enums.RestaurantStatus
import com.fooddelivery.common.enums.UserRole
import com.fooddelivery.common.enums.UserStatus
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

data class DashboardStatsResponse(
    val totalUsers: Long,
    val totalCustomers: Long,
    val totalDeliveryPersons: Long,
    val totalRestaurantOwners: Long,
    val totalRestaurants: Long,
    val pendingRestaurantApplications: Long,
    val totalOrders: Long,
    val ordersToday: Long,
    val revenueToday: BigDecimal,
    val revenueThisMonth: BigDecimal,
    val activeDeliveries: Long,
    val pendingPayments: Long,
    val flaggedReviews: Long
)

data class UserListResponse(
    val id: UUID,
    val email: String,
    val phone: String,
    val firstName: String,
    val lastName: String,
    val role: UserRole,
    val status: UserStatus,
    val emailVerified: Boolean,
    val phoneVerified: Boolean,
    val createdAt: OffsetDateTime,
    val lastLoginAt: OffsetDateTime?
)

data class UpdateUserStatusRequest(
    @NotNull
    val status: UserStatus,

    @Size(max = 500)
    val reason: String? = null
)

data class UpdateUserRoleRequest(
    @NotNull
    val role: UserRole
)

data class RestaurantApplicationListResponse(
    val id: UUID,
    val restaurantName: String,
    val ownerEmail: String,
    val ownerPhone: String,
    val status: ApplicationStatus,
    val submittedAt: OffsetDateTime,
    val reviewedAt: OffsetDateTime?,
    val reviewedBy: UUID?
)

data class RestaurantListResponse(
    val id: UUID,
    val name: String,
    val ownerName: String,
    val ownerEmail: String,
    val status: RestaurantStatus,
    val rating: Double,
    val reviewCount: Int,
    val isOpen: Boolean,
    val createdAt: OffsetDateTime
)

data class UpdateRestaurantStatusRequest(
    @NotNull
    val status: RestaurantStatus,

    @Size(max = 500)
    val reason: String? = null
)

data class OrderOverviewResponse(
    val id: UUID,
    val orderNumber: String,
    val customerName: String,
    val restaurantName: String,
    val status: OrderStatus,
    val totalAmount: BigDecimal,
    val createdAt: OffsetDateTime
)

data class DeliveryOverviewResponse(
    val id: UUID,
    val orderNumber: String,
    val deliveryPersonName: String,
    val status: com.fooddelivery.common.enums.DeliveryStatus,
    val distanceKm: BigDecimal?,
    val createdAt: OffsetDateTime
)

data class PaymentOverviewResponse(
    val id: UUID,
    val orderNumber: String,
    val customerName: String,
    val amount: BigDecimal,
    val method: com.fooddelivery.common.enums.PaymentMethod,
    val status: com.fooddelivery.common.enums.PaymentStatus,
    val createdAt: OffsetDateTime
)

data class SystemConfigResponse(
    val key: String,
    val value: String,
    val description: String,
    val isEditable: Boolean
)

data class UpdateConfigRequest(
    @NotNull
    @Size(max = 1000)
    val value: String
)