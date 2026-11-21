package com.fooddelivery.deliveries.repository

import com.fooddelivery.common.enums.DeliveryStatus
import com.fooddelivery.deliveries.domain.Delivery
import com.fooddelivery.users.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DeliveryRepository : JpaRepository<Delivery, UUID> {

    fun findByOrderId(orderId: UUID): Delivery?

    fun findByDeliveryPersonId(deliveryPersonId: UUID): List<Delivery>

    fun findByDeliveryPersonId(deliveryPersonId: UUID, pageable: Pageable): Page<Delivery>

    fun findByDeliveryPersonIdAndStatusIn(deliveryPersonId: UUID, statuses: List<DeliveryStatus>): List<Delivery>

    fun findByDeliveryPersonIdAndStatus(deliveryPersonId: UUID, status: DeliveryStatus, pageable: Pageable): Page<Delivery>

    fun findByStatus(status: DeliveryStatus, pageable: Pageable): Page<Delivery>

    @Query("SELECT d FROM Delivery d WHERE d.deliveryPerson IS NULL AND d.status = :status")
    fun findUnassignedDeliveries(status: DeliveryStatus, pageable: Pageable): Page<Delivery>

    fun countByDeliveryPersonIdAndStatus(deliveryPersonId: UUID, status: DeliveryStatus): Long

    fun countByStatusIn(statuses: List<DeliveryStatus>): Long
}