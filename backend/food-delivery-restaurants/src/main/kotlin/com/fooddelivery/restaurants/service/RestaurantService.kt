package com.fooddelivery.restaurants.service

import com.fooddelivery.common.enums.ApplicationStatus
import com.fooddelivery.common.enums.DayOfWeek
import com.fooddelivery.common.enums.RestaurantStatus
import com.fooddelivery.common.enums.UserRole
import com.fooddelivery.common.exception.BusinessException
import com.fooddelivery.common.exception.ForbiddenException
import com.fooddelivery.common.exception.ResourceNotFoundException
import com.fooddelivery.restaurants.domain.Restaurant
import com.fooddelivery.restaurants.domain.RestaurantApplication
import com.fooddelivery.restaurants.domain.RestaurantHours
import com.fooddelivery.restaurants.repository.RestaurantApplicationRepository
import com.fooddelivery.restaurants.repository.RestaurantHoursRepository
import com.fooddelivery.restaurants.repository.RestaurantRepository
import com.fooddelivery.users.domain.Address
import com.fooddelivery.users.domain.User
import com.fooddelivery.users.repository.AddressRepository
import com.fooddelivery.users.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

@Service
@Transactional
class RestaurantService(
    private val restaurantRepository: RestaurantRepository,
    private val restaurantHoursRepository: RestaurantHoursRepository,
    private val restaurantApplicationRepository: RestaurantApplicationRepository,
    private val addressRepository: AddressRepository,
    private val userRepository: UserRepository
) {

    // Restaurant Application Methods
    fun submitApplication(
        ownerId: UUID,
        proposedName: String,
        proposedCuisine: String?,
        addressId: UUID,
        businessLicenseUrl: String,
        taxIdDocumentUrl: String,
        ownerIdDocumentUrl: String
    ): RestaurantApplication {
        val owner = userRepository.findById(ownerId)
            .orElseThrow { ResourceNotFoundException("User", ownerId) }

        if (owner.role != UserRole.RESTAURANT_OWNER) {
            throw BusinessException("Only restaurant owners can submit applications", "INVALID_ROLE")
        }

        val address = addressRepository.findById(addressId)
            .orElseThrow { ResourceNotFoundException("Address", addressId) }

        if (address.user?.id != ownerId) {
            throw BusinessException("Address does not belong to owner", "ADDRESS_MISMATCH")
        }

        val existingApp = restaurantApplicationRepository.findByOwnerId(ownerId)
        if (existingApp != null && existingApp.status != ApplicationStatus.REJECTED) {
            throw BusinessException("Owner already has a pending or approved application", "DUPLICATE_APPLICATION")
        }

        val application = RestaurantApplication().apply {
            this.owner = owner
            this.proposedName = proposedName
            this.proposedCuisine = proposedCuisine
            this.proposedAddress = address
            this.businessLicenseUrl = businessLicenseUrl
            this.taxIdDocumentUrl = taxIdDocumentUrl
            this.ownerIdDocumentUrl = ownerIdDocumentUrl
            this.status = ApplicationStatus.SUBMITTED
        }

        return restaurantApplicationRepository.save(application)
    }

    fun getMyApplication(ownerId: UUID): RestaurantApplication? {
        return restaurantApplicationRepository.findByOwnerId(ownerId)
    }

    // Admin methods for applications
    fun getApplications(status: ApplicationStatus?, pageable: Pageable): Page<RestaurantApplication> {
        return if (status != null) {
            restaurantApplicationRepository.findByStatus(status, pageable)
        } else {
            restaurantApplicationRepository.findAll(pageable)
        }
    }

    fun getApplicationById(id: UUID): RestaurantApplication {
        return restaurantApplicationRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("RestaurantApplication", id) }
    }

    fun approveApplication(id: UUID, adminId: UUID): Restaurant {
        val application = getApplicationById(id)

        if (application.status != ApplicationStatus.SUBMITTED && application.status != ApplicationStatus.UNDER_REVIEW) {
            throw BusinessException("Application is not in a reviewable state", "INVALID_STATUS")
        }

        val admin = userRepository.findById(adminId)
            .orElseThrow { ResourceNotFoundException("User", adminId) }

        // Create restaurant from application
        val restaurant = Restaurant().apply {
            this.owner = application.owner!!
            this.name = application.proposedName
            this.description = "Welcome to ${application.proposedName}!"
            this.phone = application.owner!!.phone ?: ""
            this.email = application.owner!!.email
            this.cuisineType = application.proposedCuisine
            this.status = RestaurantStatus.APPROVED
            this.address = application.proposedAddress!!
            this.approvedAt = OffsetDateTime.now()
            this.approvedBy = adminId
        }

        val savedRestaurant = restaurantRepository.save(restaurant)

        // Create default hours (closed all week)
        DayOfWeek.values().forEach { day ->
            val hours = RestaurantHours().apply {
                this.restaurant = savedRestaurant
                this.dayOfWeek = day
                this.isClosed = true
            }
            restaurantHoursRepository.save(hours)
        }

        // Update application status
        application.status = ApplicationStatus.APPROVED
        application.reviewedBy = adminId
        application.reviewedAt = OffsetDateTime.now()
        restaurantApplicationRepository.save(application)

        return savedRestaurant
    }

    fun rejectApplication(id: UUID, adminId: UUID, reason: String): RestaurantApplication {
        val application = getApplicationById(id)

        if (application.status != ApplicationStatus.SUBMITTED && application.status != ApplicationStatus.UNDER_REVIEW) {
            throw BusinessException("Application is not in a reviewable state", "INVALID_STATUS")
        }

        val admin = userRepository.findById(adminId)
            .orElseThrow { ResourceNotFoundException("User", adminId) }

        application.status = ApplicationStatus.REJECTED
        application.reviewNotes = reason
        application.reviewedBy = adminId
        application.reviewedAt = OffsetDateTime.now()

        return restaurantApplicationRepository.save(application)
    }

    // Restaurant CRUD
    fun createDraft(ownerId: UUID, name: String, phone: String, addressId: UUID): Restaurant {
        val owner = userRepository.findById(ownerId)
            .orElseThrow { ResourceNotFoundException("User", ownerId) }

        if (owner.role != UserRole.RESTAURANT_OWNER) {
            throw BusinessException("Only restaurant owners can create restaurants", "INVALID_ROLE")
        }

        val address = addressRepository.findById(addressId)
            .orElseThrow { ResourceNotFoundException("Address", addressId) }

        if (address.user?.id != ownerId) {
            throw BusinessException("Address does not belong to owner", "ADDRESS_MISMATCH")
        }

        val restaurant = Restaurant().apply {
            this.owner = owner
            this.name = name
            this.phone = phone
            this.email = owner.email
            this.status = RestaurantStatus.DRAFT
            this.address = address
        }

        return restaurantRepository.save(restaurant)
    }

    fun getMyRestaurants(ownerId: UUID): List<Restaurant> {
        return restaurantRepository.findByOwnerId(ownerId)
    }

    fun getRestaurantById(id: UUID): Restaurant {
        return restaurantRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Restaurant", id) }
    }

    fun getPublicRestaurantById(id: UUID): Restaurant {
        val restaurant = getRestaurantById(id)
        if (restaurant.status != RestaurantStatus.APPROVED) {
            throw ResourceNotFoundException("Restaurant", id)
        }
        return restaurant
    }

    fun updateRestaurant(ownerId: UUID, id: UUID, updates: RestaurantUpdateRequest): Restaurant {
        val restaurant = getRestaurantById(id)

        if (restaurant.owner?.id != ownerId) {
            throw ForbiddenException("You can only update your own restaurants")
        }

        if (restaurant.status != RestaurantStatus.DRAFT && restaurant.status != RestaurantStatus.APPROVED) {
            throw BusinessException("Cannot update restaurant in current status: ${restaurant.status}", "INVALID_STATUS")
        }

        updates.name?.let { restaurant.name = it }
        updates.description?.let { restaurant.description = it }
        updates.phone?.let { restaurant.phone = it }
        updates.email?.let { restaurant.email = it }
        updates.cuisineType?.let { restaurant.cuisineType = it }
        updates.logoUrl?.let { restaurant.logoUrl = it }
        updates.coverImageUrl?.let { restaurant.coverImageUrl = it }
        updates.deliveryRadiusKm?.let { restaurant.deliveryRadiusKm = it }
        updates.minimumOrderAmount?.let { restaurant.minimumOrderAmount = it }
        updates.deliveryFee?.let { restaurant.deliveryFee = it }
        updates.preparationTimeMinutes?.let { restaurant.preparationTimeMinutes = it }

        return restaurantRepository.save(restaurant)
    }

    fun submitForApproval(ownerId: UUID, id: UUID): Restaurant {
        val restaurant = getRestaurantById(id)

        if (restaurant.owner?.id != ownerId) {
            throw ForbiddenException("You can only submit your own restaurants")
        }

        if (restaurant.status != RestaurantStatus.DRAFT) {
            throw BusinessException("Only draft restaurants can be submitted for approval", "INVALID_STATUS")
        }

        restaurant.status = RestaurantStatus.PENDING_APPROVAL
        return restaurantRepository.save(restaurant)
    }

    fun deleteRestaurant(ownerId: UUID, id: UUID) {
        val restaurant = getRestaurantById(id)

        if (restaurant.owner?.id != ownerId) {
            throw ForbiddenException("You can only delete your own restaurants")
        }

        if (restaurant.status != RestaurantStatus.DRAFT && restaurant.status != RestaurantStatus.REJECTED) {
            throw BusinessException("Can only delete draft or rejected restaurants", "INVALID_STATUS")
        }

        restaurantRepository.delete(restaurant)
    }

    // Public listing
    fun getPublicRestaurants(
        cuisine: String?,
        pageable: Pageable
    ): Page<Restaurant> {
        return restaurantRepository.findByCuisine(cuisine, pageable)
    }

    fun searchRestaurants(query: String?, pageable: Pageable): Page<Restaurant> {
        return restaurantRepository.searchRestaurants(query, pageable)
    }

    fun getOpenRestaurants(pageable: Pageable): Page<Restaurant> {
        return restaurantRepository.findOpenRestaurants(pageable)
    }

    // Hours management
    fun getHours(restaurantId: UUID): List<RestaurantHours> {
        return restaurantHoursRepository.findByRestaurantId(restaurantId)
    }

    fun updateHours(ownerId: UUID, restaurantId: UUID, hours: List<RestaurantHoursRequest>): List<RestaurantHours> {
        val restaurant = getRestaurantById(restaurantId)

        if (restaurant.owner?.id != ownerId) {
            throw ForbiddenException("You can only update hours for your own restaurants")
        }

        // Delete existing hours
        restaurantHoursRepository.deleteByRestaurantId(restaurantId)

        // Save new hours
        return hours.map { req ->
            val hour = RestaurantHours().apply {
                this.restaurant = restaurant
                this.dayOfWeek = req.dayOfWeek
                this.openTime = req.openTime
                this.closeTime = req.closeTime
                this.isClosed = req.isClosed
            }
            restaurantHoursRepository.save(hour)
        }
    }

    // Admin methods
    fun adminUpdateStatus(id: UUID, status: RestaurantStatus, adminId: UUID? = null): Restaurant {
        val restaurant = getRestaurantById(id)

        restaurant.status = status
        if (status == RestaurantStatus.APPROVED && adminId != null) {
            restaurant.approvedAt = OffsetDateTime.now()
            restaurant.approvedBy = adminId
        }

        return restaurantRepository.save(restaurant)
    }

    fun adminDeleteRestaurant(id: UUID) {
        val restaurant = getRestaurantById(id)
        restaurantRepository.delete(restaurant)
    }

    data class RestaurantUpdateRequest(
        val name: String? = null,
        val description: String? = null,
        val phone: String? = null,
        val email: String? = null,
        val cuisineType: String? = null,
        val logoUrl: String? = null,
        val coverImageUrl: String? = null,
        val deliveryRadiusKm: java.math.BigDecimal? = null,
        val minimumOrderAmount: java.math.BigDecimal? = null,
        val deliveryFee: java.math.BigDecimal? = null,
        val preparationTimeMinutes: Int? = null
    )

    data class RestaurantHoursRequest(
        val dayOfWeek: com.fooddelivery.common.enums.DayOfWeek,
        val openTime: java.time.LocalTime? = null,
        val closeTime: java.time.LocalTime? = null,
        val isClosed: Boolean = false
    )
}