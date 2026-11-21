package com.fooddelivery.orders.repository

import com.fooddelivery.orders.domain.OrderStatusHistory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OrderStatusHistoryRepository : JpaRepository<OrderStatusHistory, UUID> {

    @Query("SELECT h FROM OrderStatusHistory h WHERE h.order.id = :orderId ORDER BY h.createdAt DESC")
    fun findByOrderId(orderId: UUID, pageable: Pageable): Page<OrderStatusHistory>
}