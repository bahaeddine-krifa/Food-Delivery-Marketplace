package com.fooddelivery.restaurants.domain

import com.fooddelivery.common.domain.AuditableEntity
import com.fooddelivery.common.enums.RestaurantStatus
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "restaurants",
    uniqueConstraints = [UniqueConstraint(name = "uk_restaurants_slug", columnNames = ["slug"])],
    indexes = [
        Index(name = "idx_restaurants_owner_id", columnList = "owner_id"),
        Index(name = "idx_restaurants_status", columnList = "status"),
        Index(name = "idx_restaurants_status_open", columnList = "status,is_open_now"),
        Index(name = "idx_restaurants_cuisine", columnList = "cuisine_type"),
        Index(name = "idx_restaurants_location", columnList = "address_id")
    ]
)
class Restaurant : AuditableEntity() {

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    var owner: com.fooddelivery.users.domain.User? = null

    @Column(nullable = false, length = 200)
    var name: String = ""

    @Column(nullable = false, unique = true, length = 220)
    var slug: String = ""

    @Column(columnDefinition = "TEXT")
    var description: String? = null

    @Column(nullable = false, length = 20)
    var phone: String = ""

    @Column(length = 255)
    var email: String? = null

    @Column(name = "logo_url", length = 500)
    var logoUrl: String? = null

    @Column(name = "cover_image_url", length = 500)
    var coverImageUrl: String? = null

    @Column(name = "cuisine_type", length = 100)
    var cuisineType: String? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: RestaurantStatus = RestaurantStatus.DRAFT

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    var address: com.fooddelivery.users.domain.Address? = null

    @Column(name = "delivery_radius_km", precision = 5, scale = 2, nullable = false)
    var deliveryRadiusKm: BigDecimal = BigDecimal("5.00")

    @Column(name = "minimum_order_amount", precision = 10, scale = 2, nullable = false)
    var minimumOrderAmount: BigDecimal = BigDecimal.ZERO

    @Column(name = "delivery_fee", precision = 10, scale = 2, nullable = false)
    var deliveryFee: BigDecimal = BigDecimal.ZERO

    @Column(name = "preparation_time_minutes", nullable = false)
    var preparationTimeMinutes: Int = 30

    @Column(name = "average_rating", precision = 3, scale = 2, nullable = false)
    var averageRating: BigDecimal = BigDecimal.ZERO

    @Column(name = "total_reviews", nullable = false)
    var totalReviews: Int = 0

    @Column(name = "is_open_now", nullable = false)
    var isOpenNow: Boolean = false

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    var rejectionReason: String? = null

    @Column(name = "approved_at")
    var approvedAt: OffsetDateTime? = null

    @Column(name = "approved_by")
    var approvedBy: UUID? = null
}