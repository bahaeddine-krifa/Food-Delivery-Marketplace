package com.fooddelivery.orders.service

import com.fooddelivery.common.enums.OrderStatus
import com.fooddelivery.common.enums.UserRole
import com.fooddelivery.common.exception.AccessDeniedException
import com.fooddelivery.common.exception.BusinessException
import com.fooddelivery.common.exception.ResourceNotFoundException
import com.fooddelivery.orders.domain.Order
import com.fooddelivery.orders.domain.OrderItem
import com.fooddelivery.orders.domain.OrderStatusHistory
import com.fooddelivery.orders.dto.CreateOrderRequest
import com.fooddelivery.orders.dto.OrderItemResponse
import com.fooddelivery.orders.dto.OrderResponse
import com.fooddelivery.orders.dto.OrderStatusHistoryResponse
import com.fooddelivery.orders.dto.OrderSummaryResponse
import com.fooddelivery.orders.dto.UpdateOrderStatusRequest
import com.fooddelivery.common.enums.PaymentStatus
import com.fooddelivery.orders.repository.OrderRepository
import com.fooddelivery.orders.repository.OrderItemRepository
import com.fooddelivery.orders.repository.OrderStatusHistoryRepository
import com.fooddelivery.payments.service.PaymentService
import com.fooddelivery.products.domain.Product
import com.fooddelivery.products.repository.ProductRepository
import com.fooddelivery.restaurants.domain.Restaurant
import com.fooddelivery.restaurants.repository.RestaurantRepository
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
import java.util.UUID

@Service
@Transactional
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val orderStatusHistoryRepository: OrderStatusHistoryRepository,
    private val paymentService: PaymentService,
    private val userRepository: UserRepository,
    private val restaurantRepository: RestaurantRepository,
    private val productRepository: ProductRepository,
    private val addressRepository: AddressRepository
) {

    private val ACTIVE_STATUSES = setOf(
        OrderStatus.PENDING, OrderStatus.ACCEPTED, OrderStatus.PREPARING,
        OrderStatus.READY, OrderStatus.ASSIGNED, OrderStatus.PICKED_UP, OrderStatus.DELIVERING
    )

    private val TAX_RATE = BigDecimal("0.19") // 19% VAT

    fun createOrder(customerId: UUID, request: CreateOrderRequest): OrderResponse {
        // Check idempotency
        val existingOrder = orderRepository.findByIdempotencyKey(request.idempotencyKey)
        if (existingOrder != null) {
            return OrderResponse.fromEntity(existingOrder)
        }

        val customer = findUserById(customerId, "Customer")

        val restaurant = restaurantRepository.findById(request.restaurantId)
            .orElseThrow { ResourceNotFoundException("Restaurant", request.restaurantId) }

        if (restaurant.status != com.fooddelivery.common.enums.RestaurantStatus.APPROVED) {
            throw BusinessException("Restaurant is not accepting orders", "RESTAURANT_UNAVAILABLE")
        }

        val address = addressRepository.findById(request.addressId)
            .orElseThrow { ResourceNotFoundException("Address", request.addressId) }

        if (address.user?.id != customerId) {
            throw BusinessException("Address does not belong to customer", "INVALID_ADDRESS")
        }

        // Validate and calculate order items
        var subtotal = BigDecimal.ZERO
        val orderItems = mutableListOf<OrderItem>()

        for (itemRequest in request.items) {
            val product = productRepository.findById(itemRequest.productId)
                .orElseThrow { ResourceNotFoundException("Product", itemRequest.productId) }

            if (product.restaurant?.id != request.restaurantId) {
                throw BusinessException("Product does not belong to the specified restaurant", "INVALID_PRODUCT")
            }

            if (!product.isAvailable) {
                throw BusinessException("Product '${product.name}' is not available", "PRODUCT_UNAVAILABLE")
            }

            val unitPrice = product.price
            val lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.quantity.toLong()))

            val orderItem = OrderItem().apply {
                this.product = product
                this.productNameSnapshot = product.name
                this.unitPriceSnapshot = unitPrice
                this.quantity = itemRequest.quantity
                this.notes = itemRequest.notes
                this.lineTotal = lineTotal
            }

            orderItems.add(orderItem)
            subtotal = subtotal.add(lineTotal)
        }

        if (subtotal < restaurant.minimumOrderAmount) {
            throw BusinessException("Order minimum is ${restaurant.minimumOrderAmount}", "BELOW_MINIMUM_ORDER")
        }

        val taxAmount = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP)
        val deliveryFee = restaurant.deliveryFee
        val discountAmount = BigDecimal.ZERO // TODO: Apply promotions
        val totalAmount = subtotal.add(taxAmount).add(deliveryFee).subtract(discountAmount)

        val order = Order().apply {
            this.orderNumber = generateOrderNumber()
            this.customer = customer
            this.restaurant = restaurant
            this.address = address
            this.status = OrderStatus.PENDING
            this.subtotal = subtotal
            this.taxAmount = taxAmount
            this.deliveryFee = deliveryFee
            this.discountAmount = discountAmount
            this.totalAmount = totalAmount
            this.customerNotes = request.customerNotes
            this.idempotencyKey = request.idempotencyKey
            this.estimatedPreparationMinutes = restaurant.preparationTimeMinutes
            this.estimatedDeliveryMinutes = 20 // Default delivery time
            this.items = orderItems
        }

        orderItems.forEach { it.order = order }
        val savedOrder = orderRepository.save(order)

        // Create payment record
        paymentService.createPaymentForOrder(savedOrder.id, savedOrder.totalAmount)

        // Record status history
        recordStatusChange(savedOrder, null, OrderStatus.PENDING, customerId, "Order placed")

        return OrderResponse.fromEntity(savedOrder)
    }

    fun getOrderById(userId: UUID, orderId: UUID, userRole: UserRole): OrderResponse {
        val order = orderRepository.findById(orderId)
            .orElseThrow { ResourceNotFoundException("Order", orderId) }

        // Authorization check
        when (userRole) {
            UserRole.CUSTOMER -> {
                if (order.customer?.id != userId) {
                    throw AccessDeniedException("You can only view your own orders")
                }
            }
            UserRole.RESTAURANT_OWNER -> {
                if (order.restaurant?.owner?.id != userId) {
                    throw AccessDeniedException("You can only view orders for your restaurants")
                }
            }
            UserRole.DELIVERY_PERSON -> {
                // Delivery person can see assigned orders - check via delivery module
            }
            UserRole.ADMIN -> {
                // Admin can see all
            }
        }

        return OrderResponse.fromEntity(order)
    }

    fun getOrders(
        userId: UUID,
        userRole: UserRole,
        status: OrderStatus?,
        pageable: Pageable
    ): Page<OrderSummaryResponse> {
        return when (userRole) {
            UserRole.CUSTOMER -> {
                if (status != null) {
                    orderRepository.findByCustomerIdAndStatusIn(userId, listOf(status), pageable)
                } else {
                    orderRepository.findByCustomerId(userId, pageable)
                }
            }
            UserRole.RESTAURANT_OWNER -> {
                val restaurantIds = restaurantRepository.findByOwnerId(userId).map { it.id }
                if (status != null) {
                    // Simplified - would need a custom query for multiple restaurants
                    orderRepository.findByRestaurantIdAndStatusIn(restaurantIds.first(), listOf(status), pageable)
                } else {
                    orderRepository.findByRestaurantId(restaurantIds.first(), pageable)
                }
            }
            UserRole.ADMIN -> {
                if (status != null) {
                    orderRepository.findByStatus(status, pageable)
                } else {
                    orderRepository.findAll(pageable)
                }
            }
            else -> throw BusinessException("Unauthorized", "UNAUTHORIZED")
        }.map { OrderSummaryResponse.fromEntity(it) }
    }

    fun updateOrderStatus(
        orderId: UUID,
        request: UpdateOrderStatusRequest,
        changedBy: UUID,
        userRole: UserRole
    ): OrderResponse {
        val order = orderRepository.findById(orderId)
            .orElseThrow { ResourceNotFoundException("Order", orderId) }

        val currentStatus = order.status
        val newStatus = request.status

        // Validate state transition
        validateTransition(currentStatus, newStatus, userRole)

        // Update status and timestamps
        order.status = newStatus
        when (newStatus) {
            OrderStatus.ACCEPTED -> order.confirmedAt = OffsetDateTime.now()
            OrderStatus.PREPARING -> order.preparingAt = OffsetDateTime.now()
            OrderStatus.READY -> order.readyAt = OffsetDateTime.now()
            OrderStatus.PICKED_UP -> order.pickedUpAt = OffsetDateTime.now()
            OrderStatus.DELIVERED -> order.deliveredAt = OffsetDateTime.now()
            OrderStatus.CANCELLED -> {
                order.cancelledAt = OffsetDateTime.now()
                order.cancellationReason = request.reason
                order.cancelledBy = changedBy
            }
            OrderStatus.PENDING, OrderStatus.REJECTED, OrderStatus.ASSIGNED, OrderStatus.DELIVERING -> {
                // No specific timestamp updates for these statuses
            }
        }

        val savedOrder = orderRepository.save(order)

        // Record status history
        recordStatusChange(savedOrder, currentStatus, newStatus, changedBy, request.reason)

        // Handle side effects
        handleStatusChangeSideEffects(savedOrder, newStatus)

        return OrderResponse.fromEntity(savedOrder)
    }

    fun cancelOrder(orderId: UUID, userId: UUID, userRole: UserRole, reason: String?): OrderResponse {
        val order = orderRepository.findById(orderId)
            .orElseThrow { ResourceNotFoundException("Order", orderId) }

        // Authorization
        val canCancel = when (userRole) {
            UserRole.CUSTOMER -> order.customer?.id == userId && order.status.canBeCancelledByCustomer()
            UserRole.RESTAURANT_OWNER -> order.restaurant?.owner?.id == userId && order.status.canBeCancelledByRestaurant()
            UserRole.ADMIN -> true
            else -> false
        }

        if (!canCancel) {
            // Check if user owns the order but can't cancel due to status, or doesn't own it
            val ownsOrder = when (userRole) {
                UserRole.CUSTOMER -> order.customer?.id == userId
                UserRole.RESTAURANT_OWNER -> order.restaurant?.owner?.id == userId
                else -> false
            }
            if (!ownsOrder) {
                throw com.fooddelivery.common.exception.AccessDeniedException("You can only cancel your own orders")
            }
            throw BusinessException("Order cannot be cancelled in current status", "INVALID_CANCELLATION")
        }

        return updateOrderStatus(orderId, UpdateOrderStatusRequest(OrderStatus.CANCELLED, reason), userId, userRole)
    }

    fun getOrderStatusHistory(orderId: UUID, pageable: Pageable): Page<OrderStatusHistoryResponse> {
        val order = orderRepository.findById(orderId)
            .orElseThrow { ResourceNotFoundException("Order", orderId) }

        return orderStatusHistoryRepository.findByOrderId(orderId, pageable)
            .map { OrderStatusHistoryResponse.fromEntity(it) }
    }

    private fun findUserById(userId: UUID, entityName: String): User {
        return userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException(entityName, userId) }
    }

    private fun validateTransition(
        from: OrderStatus,
        to: OrderStatus,
        userRole: UserRole
    ) {
        val validTransitions = when (userRole) {
            UserRole.CUSTOMER -> mapOf(
                OrderStatus.PENDING to setOf(OrderStatus.CANCELLED)
            )
            UserRole.RESTAURANT_OWNER -> mapOf(
                OrderStatus.PENDING to setOf(OrderStatus.ACCEPTED, OrderStatus.CANCELLED),
                OrderStatus.ACCEPTED to setOf(OrderStatus.PREPARING, OrderStatus.CANCELLED),
                OrderStatus.PREPARING to setOf(OrderStatus.READY, OrderStatus.CANCELLED),
                OrderStatus.READY to setOf(OrderStatus.CANCELLED)
            )
            UserRole.DELIVERY_PERSON -> mapOf(
                OrderStatus.ASSIGNED to setOf(OrderStatus.PICKED_UP),
                OrderStatus.PICKED_UP to setOf(OrderStatus.DELIVERING),
                OrderStatus.DELIVERING to setOf(OrderStatus.DELIVERED)
            )
            UserRole.ADMIN -> OrderStatus.values().associateWith { OrderStatus.values().toSet() }
            else -> emptyMap()
        }

        val allowed = validTransitions[from] ?: emptySet()
        if (to !in allowed) {
            throw BusinessException("Invalid status transition from $from to $to for role $userRole", "INVALID_TRANSITION")
        }
    }

    private fun recordStatusChange(
        order: Order,
        fromStatus: OrderStatus?,
        toStatus: OrderStatus,
        changedBy: UUID,
        reason: String?
    ) {
        val history = OrderStatusHistory().apply {
            this.order = order
            this.fromStatus = fromStatus
            this.toStatus = toStatus
            this.changedBy = changedBy
            this.changeReason = reason
        }
        orderStatusHistoryRepository.save(history)
    }

    private fun handleStatusChangeSideEffects(order: Order, newStatus: OrderStatus) {
        when (newStatus) {
            OrderStatus.READY -> {
                // Trigger delivery assignment (handled by delivery module)
            }
            OrderStatus.DELIVERED -> {
                // Mark payment as paid for COD
                try {
                    val payment = paymentService.getPaymentByOrderId(order.id)
                    if (payment.status != PaymentStatus.PAID) {
                        paymentService.markAsPaid(payment.id)
                    }
                } catch (e: Exception) {
                    // Payment might not exist or already processed
                }
            }
            OrderStatus.CANCELLED -> {
                // Refund if payment was made
                try {
                    val payment = paymentService.getPaymentByOrderId(order.id)
                    if (payment.status == PaymentStatus.PAID) {
                        val customerId = order.customer?.id
                            ?: throw ResourceNotFoundException("Customer for order", order.id)
                        paymentService.initiateRefund(payment.id, "Order cancelled", customerId)
                    }
                } catch (e: Exception) {
                    // Payment might not exist
                }
            }
            OrderStatus.PENDING, OrderStatus.ACCEPTED, OrderStatus.REJECTED, OrderStatus.PREPARING,
            OrderStatus.ASSIGNED, OrderStatus.PICKED_UP, OrderStatus.DELIVERING -> {
                // No side effects for these statuses
            }
        }
    }

    private fun generateOrderNumber(): String {
        val timestamp = System.currentTimeMillis().toString()
        val random = (Math.random() * 1000).toInt()
        return "ORD${timestamp.substring(timestamp.length - 8)}${random.toString().padStart(3, '0')}"
    }
}