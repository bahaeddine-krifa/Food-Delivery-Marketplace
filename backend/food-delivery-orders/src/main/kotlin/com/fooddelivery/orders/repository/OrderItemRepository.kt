package com.fooddelivery.orders.repository

import com.fooddelivery.orders.domain.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OrderItemRepository : JpaRepository<OrderItem, UUID> {

    fun findByOrderId(orderId: UUID): List<OrderItem>

    fun deleteByOrderId(orderId: UUID)
}