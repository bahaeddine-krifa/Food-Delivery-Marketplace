package com.fooddelivery.payments.domain

import com.fooddelivery.common.domain.AuditableEntity
import com.fooddelivery.common.enums.PaymentMethod
import com.fooddelivery.common.enums.PaymentStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "payments",
    uniqueConstraints = [UniqueConstraint(name = "uk_payments_order_id", columnNames = ["order_id"])],
    indexes = [
        Index(name = "idx_payments_status", columnList = "status"),
        Index(name = "idx_payments_transaction_ref", columnList = "transaction_reference")
    ]
)
class Payment : AuditableEntity() {

    @Column(name = "order_id", nullable = false, unique = true, columnDefinition = "UUID")
    var orderId: UUID = UUID.randomUUID()

    @Column(nullable = false, precision = 10, scale = 2)
    var amount: BigDecimal = BigDecimal.ZERO

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var method: PaymentMethod = PaymentMethod.CASH_ON_DELIVERY

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: PaymentStatus = PaymentStatus.PENDING

    @Column(name = "transaction_reference", length = 100)
    var transactionReference: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "gateway_response")
    var gatewayResponse: String? = null

    @Column(name = "paid_at")
    var paidAt: OffsetDateTime? = null

    @Column(name = "refunded_at")
    var refundedAt: OffsetDateTime? = null

    @Column(name = "refund_reason", columnDefinition = "TEXT")
    var refundReason: String? = null
}