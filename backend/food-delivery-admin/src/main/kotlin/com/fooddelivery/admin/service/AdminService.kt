package com.fooddelivery.admin.service

import com.fooddelivery.admin.dto.DashboardStatsResponse
import com.fooddelivery.admin.dto.UserListResponse
import com.fooddelivery.admin.dto.UpdateUserStatusRequest
import com.fooddelivery.admin.dto.UpdateUserRoleRequest
import com.fooddelivery.admin.dto.RestaurantApplicationListResponse
import com.fooddelivery.admin.dto.RestaurantListResponse
import com.fooddelivery.admin.dto.UpdateRestaurantStatusRequest
import com.fooddelivery.admin.dto.OrderOverviewResponse
import com.fooddelivery.admin.dto.DeliveryOverviewResponse
import com.fooddelivery.admin.dto.PaymentOverviewResponse
import com.fooddelivery.admin.dto.SystemConfigResponse
import com.fooddelivery.admin.dto.UpdateConfigRequest
import com.fooddelivery.common.enums.ApplicationStatus
import com.fooddelivery.common.enums.OrderStatus
import com.fooddelivery.common.enums.PaymentMethod
import com.fooddelivery.common.enums.PaymentStatus
import com.fooddelivery.common.enums.RestaurantStatus
import com.fooddelivery.common.enums.UserRole
import com.fooddelivery.common.enums.UserStatus
import com.fooddelivery.common.exception.ResourceNotFoundException
import com.fooddelivery.deliveries.domain.Delivery
import com.fooddelivery.deliveries.repository.DeliveryRepository
import com.fooddelivery.orders.domain.Order
import com.fooddelivery.orders.repository.OrderRepository
import com.fooddelivery.payments.domain.Payment
import com.fooddelivery.payments.repository.PaymentRepository
import com.fooddelivery.restaurants.domain.Restaurant
import com.fooddelivery.restaurants.domain.RestaurantApplication
import com.fooddelivery.restaurants.repository.RestaurantApplicationRepository
import com.fooddelivery.restaurants.repository.RestaurantRepository
import com.fooddelivery.reviews.domain.Review
import com.fooddelivery.reviews.repository.ReviewRepository
import com.fooddelivery.reviews.dto.ReviewResponse
import com.fooddelivery.users.domain.User
import com.fooddelivery.users.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

@Service
@Transactional
class AdminService(
    private val userRepository: UserRepository,
    private val restaurantRepository: RestaurantRepository,
    private val restaurantApplicationRepository: RestaurantApplicationRepository,
    private val orderRepository: OrderRepository,
    private val deliveryRepository: DeliveryRepository,
    private val paymentRepository: PaymentRepository,
    private val reviewRepository: ReviewRepository
) {

    fun getDashboardStats(): DashboardStatsResponse {
        val now = OffsetDateTime.now()
        val startOfDay = now.toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC)
        val startOfMonth = now.toLocalDate().withDayOfMonth(1).atStartOfDay().atOffset(ZoneOffset.UTC)

        val totalUsers = userRepository.count()
        val totalCustomers = userRepository.countByRole(UserRole.CUSTOMER)
        val totalDeliveryPersons = userRepository.countByRole(UserRole.DELIVERY_PERSON)
        val totalRestaurantOwners = userRepository.countByRole(UserRole.RESTAURANT_OWNER)
        val totalRestaurants = restaurantRepository.count()

        val pendingApplications = restaurantApplicationRepository.countByStatus(ApplicationStatus.SUBMITTED)

        val totalOrders = orderRepository.count()
        val ordersToday = orderRepository.countByCreatedAtAfter(startOfDay)

        val revenueToday = orderRepository.sumTotalAmountByCreatedAtAfter(startOfDay) ?: BigDecimal.ZERO
        val revenueThisMonth = orderRepository.sumTotalAmountByCreatedAtAfter(startOfMonth) ?: BigDecimal.ZERO

        val activeDeliveries = deliveryRepository.countByStatusIn(
            listOf(
                com.fooddelivery.common.enums.DeliveryStatus.ASSIGNED,
                com.fooddelivery.common.enums.DeliveryStatus.ACCEPTED,
                com.fooddelivery.common.enums.DeliveryStatus.PICKED_UP,
                com.fooddelivery.common.enums.DeliveryStatus.DELIVERING
            )
        )

        val pendingPayments = paymentRepository.countByStatus(PaymentStatus.PENDING)

        val flaggedReviews = reviewRepository.countByIsFlagged(true)

        return DashboardStatsResponse(
            totalUsers = totalUsers,
            totalCustomers = totalCustomers,
            totalDeliveryPersons = totalDeliveryPersons,
            totalRestaurantOwners = totalRestaurantOwners,
            totalRestaurants = totalRestaurants,
            pendingRestaurantApplications = pendingApplications,
            totalOrders = totalOrders,
            ordersToday = ordersToday,
            revenueToday = revenueToday,
            revenueThisMonth = revenueThisMonth,
            activeDeliveries = activeDeliveries,
            pendingPayments = pendingPayments,
            flaggedReviews = flaggedReviews
        )
    }

    fun getUsers(role: UserRole?, status: UserStatus?, pageable: Pageable): Page<UserListResponse> {
        val users = when {
            role != null && status != null -> userRepository.findByRoleAndStatus(role, status, pageable)
            role != null -> userRepository.findByRole(role, pageable)
            status != null -> userRepository.findByStatus(status, pageable)
            else -> userRepository.findAll(pageable)
        }

        return users.map { user ->
            UserListResponse(
                id = user.id,
                email = user.email,
                phone = user.phone ?: "",
                firstName = user.firstName,
                lastName = user.lastName,
                role = user.role,
                status = user.status,
                emailVerified = user.emailVerified,
                phoneVerified = false, // Not tracked in current User entity
                createdAt = user.createdAt,
                lastLoginAt = user.lastLoginAt
            )
        }
    }

    fun getUserById(userId: UUID): UserListResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User", userId) }

        return UserListResponse(
            id = user.id,
            email = user.email,
            phone = user.phone ?: "",
            firstName = user.firstName,
            lastName = user.lastName,
            role = user.role,
            status = user.status,
            emailVerified = user.emailVerified,
            phoneVerified = false, // Not tracked in current User entity
            createdAt = user.createdAt,
            lastLoginAt = user.lastLoginAt
        )
    }

    fun updateUserStatus(userId: UUID, request: UpdateUserStatusRequest): UserListResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User", userId) }

        user.status = request.status
        userRepository.save(user)

        return getUserById(userId)
    }

    fun updateUserRole(userId: UUID, request: UpdateUserRoleRequest): UserListResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User", userId) }

        user.role = request.role
        userRepository.save(user)

        return getUserById(userId)
    }

    fun getRestaurantApplications(status: ApplicationStatus?, pageable: Pageable): Page<RestaurantApplicationListResponse> {
        val applications = if (status != null) {
            restaurantApplicationRepository.findByStatus(status, pageable)
        } else {
            restaurantApplicationRepository.findAll(pageable)
        }

        return applications.map { app ->
            RestaurantApplicationListResponse(
                id = app.id,
                restaurantName = app.proposedName,
                ownerEmail = app.owner?.email ?: "",
                ownerPhone = app.owner?.phone ?: "",
                status = app.status,
                submittedAt = app.submittedAt,
                reviewedAt = app.reviewedAt,
                reviewedBy = app.reviewedBy
            )
        }
    }

    fun approveRestaurantApplication(applicationId: UUID, adminId: UUID): Restaurant {
        val application = restaurantApplicationRepository.findById(applicationId)
            .orElseThrow { ResourceNotFoundException("Restaurant Application", applicationId) }

        if (application.status != ApplicationStatus.SUBMITTED) {
            throw com.fooddelivery.common.exception.BusinessException("Application already processed", "ALREADY_PROCESSED")
        }

        val admin = userRepository.findById(adminId)
            .orElseThrow { ResourceNotFoundException("Admin", adminId) }

        application.status = ApplicationStatus.APPROVED
        application.reviewedAt = OffsetDateTime.now()
        application.reviewedBy = adminId
        restaurantApplicationRepository.save(application)

        // Create restaurant from application
        val restaurant = Restaurant().apply {
            this.name = application.proposedName
            this.description = application.proposedCuisine
            this.address = application.proposedAddress
            this.phone = application.owner?.phone ?: ""
            this.email = application.owner?.email
            this.owner = admin
            this.status = RestaurantStatus.APPROVED
            this.preparationTimeMinutes = 30
            this.minimumOrderAmount = BigDecimal.ZERO
            this.deliveryFee = BigDecimal.ZERO
            this.cuisineType = application.proposedCuisine
        }

        return restaurantRepository.save(restaurant)
    }

    fun rejectRestaurantApplication(applicationId: UUID, adminId: UUID, reason: String): RestaurantApplication {
        val application = restaurantApplicationRepository.findById(applicationId)
            .orElseThrow { ResourceNotFoundException("Restaurant Application", applicationId) }

        if (application.status != ApplicationStatus.SUBMITTED) {
            throw com.fooddelivery.common.exception.BusinessException("Application already processed", "ALREADY_PROCESSED")
        }

        application.status = ApplicationStatus.REJECTED
        application.reviewedAt = OffsetDateTime.now()
        application.reviewedBy = adminId
        application.reviewNotes = reason

        return restaurantApplicationRepository.save(application)
    }

    fun getRestaurants(status: RestaurantStatus?, pageable: Pageable): Page<RestaurantListResponse> {
        val restaurants = if (status != null) {
            restaurantRepository.findByStatus(status, pageable)
        } else {
            restaurantRepository.findAll(pageable)
        }

        return restaurants.map { restaurant ->
            RestaurantListResponse(
                id = restaurant.id,
                name = restaurant.name,
                ownerName = restaurant.owner?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown",
                ownerEmail = restaurant.owner?.email ?: "",
                status = restaurant.status,
                rating = restaurant.averageRating?.toDouble() ?: 0.0,
                reviewCount = restaurant.totalReviews ?: 0,
                isOpen = false, // isOpenNow is a property, not computed
                createdAt = restaurant.createdAt
            )
        }
    }

    fun updateRestaurantStatus(restaurantId: UUID, request: UpdateRestaurantStatusRequest): RestaurantListResponse {
        val restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow { ResourceNotFoundException("Restaurant", restaurantId) }

        restaurant.status = request.status
        restaurantRepository.save(restaurant)

        return getRestaurants(null, org.springframework.data.domain.Pageable.unpaged())
            .first { it.id == restaurantId }
    }

    fun getOrders(status: OrderStatus?, pageable: Pageable): Page<OrderOverviewResponse> {
        val orders = if (status != null) {
            orderRepository.findByStatus(status, pageable)
        } else {
            orderRepository.findAll(pageable)
        }

        return orders.map { order ->
            OrderOverviewResponse(
                id = order.id,
                orderNumber = order.orderNumber,
                customerName = order.customer?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown",
                restaurantName = order.restaurant?.name ?: "Unknown",
                status = order.status,
                totalAmount = order.totalAmount,
                createdAt = order.createdAt
            )
        }
    }

    fun getDeliveries(status: com.fooddelivery.common.enums.DeliveryStatus?, pageable: Pageable): Page<DeliveryOverviewResponse> {
        val deliveries = if (status != null) {
            deliveryRepository.findByStatus(status, pageable)
        } else {
            deliveryRepository.findAll(pageable)
        }

        return deliveries.map { delivery ->
            DeliveryOverviewResponse(
                id = delivery.id,
                orderNumber = delivery.order?.orderNumber ?: "Unknown",
                deliveryPersonName = delivery.deliveryPerson?.let { "${it.firstName} ${it.lastName}" } ?: "Unassigned",
                status = delivery.status,
                distanceKm = delivery.distanceKm,
                createdAt = delivery.createdAt
            )
        }
    }

    fun getPayments(status: PaymentStatus?, pageable: Pageable): Page<PaymentOverviewResponse> {
        val payments = if (status != null) {
            paymentRepository.findByStatus(status, pageable)
        } else {
            paymentRepository.findAll(pageable)
        }

        return payments.map { payment ->
            val order = orderRepository.findById(payment.orderId).orElse(null)
            val customerName = order?.customer?.let { user -> "${user.firstName} ${user.lastName}" } ?: "Unknown"
            PaymentOverviewResponse(
                id = payment.id,
                orderNumber = order?.orderNumber ?: "Unknown",
                customerName = customerName,
                amount = payment.amount,
                method = payment.method,
                status = payment.status,
                createdAt = payment.createdAt
            )
        }
    }

    fun getFlaggedReviews(pageable: Pageable): Page<ReviewResponse> {
        return reviewRepository.findByIsFlaggedTrue(pageable)
            .map { review -> ReviewResponse.fromEntity(review) }
    }

    fun getSystemConfig(): List<SystemConfigResponse> {
        return listOf(
            SystemConfigResponse("platform.name", "Food Delivery Platform", "Platform display name", true),
            SystemConfigResponse("platform.tax_rate", "0.19", "Default tax rate (VAT)", true),
            SystemConfigResponse("platform.currency", "TND", "Default currency", true),
            SystemConfigResponse("delivery.max_active_per_person", "3", "Max active deliveries per person", true),
            SystemConfigResponse("order.auto_cancel_minutes", "30", "Minutes before auto-cancelling pending orders", true),
            SystemConfigResponse("review.allow_after_days", "30", "Days after delivery to allow reviews", true)
        )
    }

    fun updateSystemConfig(key: String, request: UpdateConfigRequest): SystemConfigResponse {
        // In a real implementation, this would update a configuration table
        return SystemConfigResponse(key, request.value, "Updated configuration", true)
    }
}