package com.fooddelivery.deliveries.service

import com.fooddelivery.common.enums.DeliveryStatus
import com.fooddelivery.common.enums.OrderStatus
import com.fooddelivery.common.exception.BusinessException
import com.fooddelivery.common.exception.ResourceNotFoundException
import com.fooddelivery.deliveries.domain.Delivery
import com.fooddelivery.deliveries.dto.AssignDeliveryRequest
import com.fooddelivery.deliveries.dto.DeliveryResponse
import com.fooddelivery.deliveries.dto.DeliverySummaryResponse
import com.fooddelivery.deliveries.dto.NearbyDeliveryRequest
import com.fooddelivery.deliveries.dto.UpdateDeliveryStatusRequest
import com.fooddelivery.deliveries.repository.DeliveryRepository
import com.fooddelivery.orders.domain.Order
import com.fooddelivery.orders.repository.OrderRepository
import com.fooddelivery.users.domain.Address
import com.fooddelivery.users.domain.User
import com.fooddelivery.users.repository.AddressRepository
import com.fooddelivery.users.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.OffsetDateTime
import java.util.Optional
import java.util.UUID

@Service
@Transactional
class DeliveryService(
    private val deliveryRepository: DeliveryRepository,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val addressRepository: AddressRepository
) {

    fun createDeliveryForOrder(order: Order): Delivery {
        // Restaurant address as pickup
        val restaurantAddress = order.restaurant?.address
            ?: throw ResourceNotFoundException("Restaurant address for order", order.id)

        val distanceKm = calculateDistance(
                restaurantAddress.latitude, restaurantAddress.longitude,
                order.address?.latitude, order.address?.longitude
            )
        val estimatedDuration = if (distanceKm != null) estimateDuration(distanceKm) else null

        val delivery = Delivery().apply {
            this.order = order
            this.deliveryPerson = null
            this.status = DeliveryStatus.PENDING_ASSIGNMENT
            this.pickupAddress = restaurantAddress
            this.deliveryAddress = order.address
            this.pickupLatitude = restaurantAddress.latitude
            this.pickupLongitude = restaurantAddress.longitude
            this.deliveryLatitude = order.address?.latitude
            this.deliveryLongitude = order.address?.longitude
            this.pickupCode = generateCode()
            this.deliveryCode = generateCode()
            this.distanceKm = distanceKm
            this.estimatedDurationMinutes = estimatedDuration
        }

        return deliveryRepository.save(delivery)
    }

    fun getDeliveryByOrderId(orderId: UUID): DeliveryResponse {
        val delivery = deliveryRepository.findByOrderId(orderId)
            ?: throw ResourceNotFoundException("Delivery", orderId)
        return DeliveryResponse.fromEntity(delivery)
    }

    fun getDeliveryById(deliveryId: UUID): DeliveryResponse {
        val delivery = deliveryRepository.findById(deliveryId).orElse(null)
            ?: throw ResourceNotFoundException("Delivery", deliveryId)
        return DeliveryResponse.fromEntity(delivery)
    }

    fun assignDeliveryPerson(deliveryId: UUID, deliveryPersonId: UUID): DeliveryResponse {
        val delivery = deliveryRepository.findById(deliveryId).orElse(null)
            ?: throw ResourceNotFoundException("Delivery", deliveryId)

        if (delivery.status != DeliveryStatus.PENDING_ASSIGNMENT) {
            throw BusinessException("Delivery is not available for assignment", "INVALID_STATUS")
        }

        val deliveryPerson = userRepository.findById(deliveryPersonId)
            .orElseThrow { ResourceNotFoundException("Delivery Person", deliveryPersonId) }

        if (deliveryPerson.role != com.fooddelivery.common.enums.UserRole.DELIVERY_PERSON) {
            throw BusinessException("User is not a delivery person", "INVALID_ROLE")
        }

        val activeDeliveries = deliveryRepository.countByDeliveryPersonIdAndStatus(
            deliveryPersonId, DeliveryStatus.ASSIGNED
        ) + deliveryRepository.countByDeliveryPersonIdAndStatus(
            deliveryPersonId, DeliveryStatus.ACCEPTED
        ) + deliveryRepository.countByDeliveryPersonIdAndStatus(
            deliveryPersonId, DeliveryStatus.PICKED_UP
        ) + deliveryRepository.countByDeliveryPersonIdAndStatus(
            deliveryPersonId, DeliveryStatus.DELIVERING
        )

        if (activeDeliveries >= 3) {
            throw BusinessException("Delivery person has too many active deliveries", "MAX_DELIVERIES_REACHED")
        }

        delivery.deliveryPerson = deliveryPerson
        delivery.status = DeliveryStatus.ASSIGNED
        delivery.assignedAt = OffsetDateTime.now()

        // Update order status
        val order = delivery.order
            ?: throw ResourceNotFoundException("Order for delivery", delivery.id)
        order.status = OrderStatus.ASSIGNED
        orderRepository.save(order)

        return DeliveryResponse.fromEntity(deliveryRepository.save(delivery))
    }

    fun updateDeliveryStatus(
        deliveryId: UUID,
        request: UpdateDeliveryStatusRequest,
        userId: UUID,
        userRole: com.fooddelivery.common.enums.UserRole
    ): DeliveryResponse {
        val delivery = deliveryRepository.findById(deliveryId).orElse(null)
            ?: throw ResourceNotFoundException("Delivery", deliveryId)

        // Authorization check
        if (userRole == com.fooddelivery.common.enums.UserRole.DELIVERY_PERSON) {
            if (delivery.deliveryPerson?.id != userId) {
                throw BusinessException("You can only update your own deliveries", "UNAUTHORIZED")
            }
        }

        validateDeliveryTransition(delivery.status, request.status, userRole)

        delivery.status = request.status
        when (request.status) {
            DeliveryStatus.ACCEPTED -> delivery.acceptedAt = OffsetDateTime.now()
            DeliveryStatus.PICKED_UP -> {
                delivery.pickedUpAt = OffsetDateTime.now()
                request.pickupLatitude?.let { delivery.pickupLatitude = it }
                request.pickupLongitude?.let { delivery.pickupLongitude = it }
                request.pickupPhotoUrl?.let { delivery.pickupPhotoUrl = it }
                // Update order status
                val order = delivery.order
                    ?: throw ResourceNotFoundException("Order for delivery", delivery.id)
                order.status = OrderStatus.PICKED_UP
                orderRepository.save(order)
            }
            DeliveryStatus.DELIVERING -> {
                request.deliveryLatitude?.let { delivery.deliveryLatitude = it }
                request.deliveryLongitude?.let { delivery.deliveryLongitude = it }
            }
            DeliveryStatus.DELIVERED -> {
                delivery.deliveredAt = OffsetDateTime.now()
                request.deliveryLatitude?.let { delivery.deliveryLatitude = it }
                request.deliveryLongitude?.let { delivery.deliveryLongitude = it }
                request.deliveryPhotoUrl?.let { delivery.deliveryPhotoUrl = it }
                request.customerSignatureUrl?.let { delivery.customerSignatureUrl = it }
                delivery.actualDurationMinutes = calculateActualDuration(delivery)
                // Update order status
                val order = delivery.order
                    ?: throw ResourceNotFoundException("Order for delivery", delivery.id)
                order.status = OrderStatus.DELIVERED
                orderRepository.save(order)
            }
            DeliveryStatus.FAILED -> {
                // Handle failed delivery
                val order = delivery.order
                    ?: throw ResourceNotFoundException("Order for delivery", delivery.id)
                order.status = OrderStatus.CANCELLED
                orderRepository.save(order)
            }
            DeliveryStatus.PENDING_ASSIGNMENT, DeliveryStatus.ASSIGNED, DeliveryStatus.CANCELLED -> {
                // No specific timestamp updates for these statuses in this context
            }
        }

        return DeliveryResponse.fromEntity(deliveryRepository.save(delivery))
    }

    fun getDeliveriesForDeliveryPerson(
        deliveryPersonId: UUID,
        status: DeliveryStatus?,
        pageable: Pageable
    ): Page<DeliverySummaryResponse> {
        return if (status != null) {
            deliveryRepository.findByDeliveryPersonIdAndStatus(deliveryPersonId, status, pageable)
        } else {
            deliveryRepository.findByDeliveryPersonId(deliveryPersonId, pageable)
        }.map { DeliverySummaryResponse.fromEntity(it) }
    }

    fun getAvailableDeliveries(pageable: Pageable): Page<DeliverySummaryResponse> {
        return deliveryRepository.findUnassignedDeliveries(DeliveryStatus.PENDING_ASSIGNMENT, pageable)
            .map { DeliverySummaryResponse.fromEntity(it) }
    }

    fun getDeliveries(
        status: DeliveryStatus?,
        pageable: Pageable
    ): Page<DeliverySummaryResponse> {
        return if (status != null) {
            deliveryRepository.findByStatus(status, pageable)
        } else {
            deliveryRepository.findAll(pageable)
        }.map { DeliverySummaryResponse.fromEntity(it) }
    }

    private fun validateDeliveryTransition(
        from: DeliveryStatus,
        to: DeliveryStatus,
        userRole: com.fooddelivery.common.enums.UserRole
    ) {
        val validTransitions = when (userRole) {
            com.fooddelivery.common.enums.UserRole.DELIVERY_PERSON -> mapOf(
                DeliveryStatus.ASSIGNED to setOf(DeliveryStatus.ACCEPTED),
                DeliveryStatus.ACCEPTED to setOf(DeliveryStatus.PICKED_UP, DeliveryStatus.FAILED),
                DeliveryStatus.PICKED_UP to setOf(DeliveryStatus.DELIVERING, DeliveryStatus.FAILED),
                DeliveryStatus.DELIVERING to setOf(DeliveryStatus.DELIVERED, DeliveryStatus.FAILED)
            )
            com.fooddelivery.common.enums.UserRole.ADMIN -> DeliveryStatus.values().associateWith { DeliveryStatus.values().toSet() }
            else -> emptyMap()
        }

        val allowed = validTransitions[from] ?: emptySet()
        if (to !in allowed) {
            throw BusinessException("Invalid delivery status transition from $from to $to", "INVALID_TRANSITION")
        }
    }

    private fun generateCode(): String {
        return (100000 + (Math.random() * 900000).toInt()).toString()
    }

    private fun calculateDistance(
        lat1: BigDecimal?, lon1: BigDecimal?,
        lat2: BigDecimal?, lon2: BigDecimal?
    ): BigDecimal? {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) return null

        val R = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2.toDouble() - lat1.toDouble())
        val dLon = Math.toRadians(lon2.toDouble() - lon1.toDouble())
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1.toDouble())) * Math.cos(Math.toRadians(lat2.toDouble())) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return BigDecimal.valueOf(R * c).setScale(2, RoundingMode.HALF_UP)
    }

    private fun estimateDuration(distanceKm: BigDecimal): Int {
        val avgSpeedKmh = 30.0 // Average delivery speed in km/h
        val hours = distanceKm.toDouble() / avgSpeedKmh
        return (hours * 60).toInt().coerceAtLeast(10)
    }

    private fun calculateActualDuration(delivery: Delivery): Int? {
        if (delivery.acceptedAt != null && delivery.deliveredAt != null) {
            return java.time.Duration.between(delivery.acceptedAt!!, delivery.deliveredAt!!).toMinutes().toInt()
        }
        return null
    }
}