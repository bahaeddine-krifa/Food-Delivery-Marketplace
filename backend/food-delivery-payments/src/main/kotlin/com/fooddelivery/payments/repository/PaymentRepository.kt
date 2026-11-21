package com.fooddelivery.payments.repository

import com.fooddelivery.common.enums.PaymentStatus
import com.fooddelivery.payments.domain.Payment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface PaymentRepository : JpaRepository<Payment, UUID> {

    fun findByOrderId(orderId: UUID): Optional<Payment>

    fun findByStatus(status: PaymentStatus, pageable: Pageable): Page<Payment>

    fun findByTransactionReference(reference: String): Optional<Payment>

    fun countByStatus(status: PaymentStatus): Long
}