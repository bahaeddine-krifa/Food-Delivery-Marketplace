package com.fooddelivery.deliveries.domain

import com.fooddelivery.common.domain.AuditableEntity
import com.fooddelivery.common.enums.DeliveryStatus
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "deliveries",
    uniqueConstraints = [UniqueConstraint(name = "uk_deliveries_order_id", columnNames = ["order_id"])],
    indexes = [
        Index(name = "idx_deliveries_delivery_person", columnList = "delivery_person_id,status,created_at DESC"),
        Index(name = "idx_deliveries_status", columnList = "status"),
        Index(name = "idx_deliveries_assigned", columnList = "assigned_at")
    ]
)
class Delivery : AuditableEntity() {

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    var order: com.fooddelivery.orders.domain.Order? = null

    @ManyToOne
    @JoinColumn(name = "delivery_person_id")
    var deliveryPerson: com.fooddelivery.users.domain.User? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: DeliveryStatus = DeliveryStatus.PENDING_ASSIGNMENT

    @ManyToOne
    @JoinColumn(name = "pickup_address_id", nullable = false)
    var pickupAddress: com.fooddelivery.users.domain.Address? = null

    @ManyToOne
    @JoinColumn(name = "delivery_address_id", nullable = false)
    var deliveryAddress: com.fooddelivery.users.domain.Address? = null

    @Column(name = "pickup_latitude", precision = 10, scale = 8)
    var pickupLatitude: BigDecimal? = null

    @Column(name = "pickup_longitude", precision = 11, scale = 8)
    var pickupLongitude: BigDecimal? = null

    @Column(name = "delivery_latitude", precision = 10, scale = 8)
    var deliveryLatitude: BigDecimal? = null

    @Column(name = "delivery_longitude", precision = 11, scale = 8)
    var deliveryLongitude: BigDecimal? = null

    @Column(name = "pickup_code", nullable = false, length = 6)
    var pickupCode: String = ""

    @Column(name = "delivery_code", nullable = false, length = 6)
    var deliveryCode: String = ""

    @Column(name = "pickup_photo_url", length = 500)
    var pickupPhotoUrl: String? = null

    @Column(name = "delivery_photo_url", length = 500)
    var deliveryPhotoUrl: String? = null

    @Column(name = "customer_signature_url", length = 500)
    var customerSignatureUrl: String? = null

    @Column(name = "distance_km", precision = 6, scale = 2)
    var distanceKm: BigDecimal? = null

    @Column(name = "estimated_duration_minutes")
    var estimatedDurationMinutes: Int? = null

    @Column(name = "actual_duration_minutes")
    var actualDurationMinutes: Int? = null

    @Column(name = "assigned_at")
    var assignedAt: OffsetDateTime? = null

    @Column(name = "accepted_at")
    var acceptedAt: OffsetDateTime? = null

    @Column(name = "picked_up_at")
    var pickedUpAt: OffsetDateTime? = null

    @Column(name = "delivered_at")
    var deliveredAt: OffsetDateTime? = null
}