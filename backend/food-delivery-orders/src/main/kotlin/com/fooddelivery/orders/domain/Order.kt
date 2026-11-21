package com.fooddelivery.orders.domain

import com.fooddelivery.common.domain.AuditableEntity
import com.fooddelivery.common.enums.OrderStatus
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "orders",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_orders_order_number", columnNames = ["order_number"]),
        UniqueConstraint(name = "uk_orders_idempotency_key", columnNames = ["idempotency_key"])
    ],
    indexes = [
        Index(name = "idx_orders_customer", columnList = "customer_id,created_at DESC"),
        Index(name = "idx_orders_restaurant", columnList = "restaurant_id,status,created_at DESC"),
        Index(name = "idx_orders_status", columnList = "status"),
        Index(name = "idx_orders_active", columnList = "status,created_at"),
        Index(name = "idx_orders_idempotency", columnList = "idempotency_key")
    ]
)
class Order : AuditableEntity() {

    @Column(name = "order_number", nullable = false, unique = true, length = 20)
    var orderNumber: String = ""

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    var customer: com.fooddelivery.users.domain.User? = null

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    var restaurant: com.fooddelivery.restaurants.domain.Restaurant? = null

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    var address: com.fooddelivery.users.domain.Address? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: OrderStatus = OrderStatus.PENDING

    @Column(nullable = false, precision = 10, scale = 2)
    var subtotal: BigDecimal = BigDecimal.ZERO

    @Column(name = "tax_amount", nullable = false, precision = 10, scale = 2)
    var taxAmount: BigDecimal = BigDecimal.ZERO

    @Column(name = "delivery_fee", nullable = false, precision = 10, scale = 2)
    var deliveryFee: BigDecimal = BigDecimal.ZERO

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    var discountAmount: BigDecimal = BigDecimal.ZERO

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    var totalAmount: BigDecimal = BigDecimal.ZERO

    @Column(name = "customer_notes", columnDefinition = "TEXT")
    var customerNotes: String? = null

    @Column(name = "restaurant_notes", columnDefinition = "TEXT")
    var restaurantNotes: String? = null

    @Column(name = "estimated_preparation_minutes")
    var estimatedPreparationMinutes: Int? = null

    @Column(name = "estimated_delivery_minutes")
    var estimatedDeliveryMinutes: Int? = null

    @Column(name = "confirmed_at")
    var confirmedAt: OffsetDateTime? = null

    @Column(name = "preparing_at")
    var preparingAt: OffsetDateTime? = null

    @Column(name = "ready_at")
    var readyAt: OffsetDateTime? = null

    @Column(name = "picked_up_at")
    var pickedUpAt: OffsetDateTime? = null

    @Column(name = "delivered_at")
    var deliveredAt: OffsetDateTime? = null

    @Column(name = "cancelled_at")
    var cancelledAt: OffsetDateTime? = null

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    var cancellationReason: String? = null

    @Column(name = "cancelled_by")
    var cancelledBy: UUID? = null

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 64)
    var idempotencyKey: String = ""

    @OneToMany(mappedBy = "order", cascade = [CascadeType.PERSIST, CascadeType.MERGE], orphanRemoval = true)
    var items: MutableList<OrderItem> = mutableListOf()
}