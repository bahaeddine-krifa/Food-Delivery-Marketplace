package com.fooddelivery.orders.repository

import com.fooddelivery.common.enums.OrderStatus
import com.fooddelivery.orders.domain.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OrderRepository : JpaRepository<Order, UUID> {

    fun findByOrderNumber(orderNumber: String): Order?

    fun findByIdempotencyKey(idempotencyKey: String): Order?

    fun findByCustomerId(customerId: UUID, pageable: Pageable): Page<Order>

    fun findByRestaurantId(restaurantId: UUID, pageable: Pageable): Page<Order>

    fun findByRestaurantIdAndStatusIn(restaurantId: UUID, statuses: List<OrderStatus>, pageable: Pageable): Page<Order>

    fun findByCustomerIdAndStatusIn(customerId: UUID, statuses: List<OrderStatus>, pageable: Pageable): Page<Order>

    fun findByStatus(status: OrderStatus, pageable: Pageable): Page<Order>

    @Query("SELECT o FROM Order o WHERE o.status IN :activeStatuses")
    fun findActiveOrders(activeStatuses: List<OrderStatus>): List<Order>

    fun countByRestaurantIdAndStatus(restaurantId: UUID, status: OrderStatus): Long

    fun countByCustomerIdAndStatus(customerId: UUID, status: OrderStatus): Long

    override fun findAll(pageable: Pageable): Page<Order>

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.createdAt > :date")
    fun sumTotalAmountByCreatedAtAfter(date: java.time.OffsetDateTime): java.math.BigDecimal?

    fun countByCreatedAtAfter(date: java.time.OffsetDateTime): Long
}