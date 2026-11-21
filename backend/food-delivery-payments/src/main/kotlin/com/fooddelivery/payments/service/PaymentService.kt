package com.fooddelivery.payments.service

import com.fooddelivery.common.enums.PaymentMethod
import com.fooddelivery.common.enums.PaymentStatus
import com.fooddelivery.common.exception.BusinessException
import com.fooddelivery.common.exception.ResourceNotFoundException
import com.fooddelivery.payments.domain.Payment
import com.fooddelivery.payments.repository.PaymentRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.Optional
import java.util.UUID

@Service
@Transactional
class PaymentService(
    private val paymentRepository: PaymentRepository
) {

    fun createPaymentForOrder(orderId: UUID, amount: BigDecimal, method: PaymentMethod = PaymentMethod.CASH_ON_DELIVERY): Payment {
        val payment = Payment().apply {
            this.orderId = orderId
            this.amount = amount
            this.method = method
            this.status = PaymentStatus.PENDING
        }
        return paymentRepository.save(payment)
    }

    fun getPaymentByOrderId(orderId: UUID): Payment {
        return paymentRepository.findByOrderId(orderId)
            .orElseThrow { ResourceNotFoundException("Payment", orderId) }
    }

    fun markAsPaid(paymentId: UUID): Payment {
        val payment = paymentRepository.findById(paymentId)
            .orElseThrow { ResourceNotFoundException("Payment", paymentId) }

        if (payment.status == PaymentStatus.PAID) {
            throw BusinessException("Payment already processed", "ALREADY_PAID")
        }

        payment.status = PaymentStatus.PAID
        payment.paidAt = OffsetDateTime.now()
        return paymentRepository.save(payment)
    }

    fun markAsFailed(paymentId: UUID, reason: String? = null): Payment {
        val payment = paymentRepository.findById(paymentId)
            .orElseThrow { ResourceNotFoundException("Payment", paymentId) }

        payment.status = PaymentStatus.FAILED
        payment.gatewayResponse = reason
        return paymentRepository.save(payment)
    }

    fun initiateRefund(paymentId: UUID, reason: String, adminId: UUID): Payment {
        val payment = paymentRepository.findById(paymentId)
            .orElseThrow { ResourceNotFoundException("Payment", paymentId) }

        if (payment.status != PaymentStatus.PAID) {
            throw BusinessException("Can only refund paid payments", "INVALID_STATUS")
        }

        payment.status = PaymentStatus.REFUNDED
        payment.refundedAt = OffsetDateTime.now()
        payment.refundReason = reason
        return paymentRepository.save(payment)
    }

    fun getPayments(status: PaymentStatus?, pageable: Pageable): Page<Payment> {
        return if (status != null) {
            paymentRepository.findByStatus(status, pageable)
        } else {
            paymentRepository.findAll(pageable)
        }
    }

    fun getPaymentById(id: UUID): Payment {
        return paymentRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Payment", id) }
    }
}