package com.fooddelivery.reviews.domain

import com.fooddelivery.common.domain.AuditableEntity
import com.fooddelivery.common.enums.ReviewTargetType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID

@Entity
@Table(
    name = "reviews",
    indexes = [
        Index(name = "idx_reviews_target", columnList = "target_type,target_id"),
        Index(name = "idx_reviews_customer", columnList = "customer_id,created_at DESC"),
        Index(name = "idx_reviews_order", columnList = "order_id")
    ]
)
class Review : AuditableEntity() {

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    var customer: com.fooddelivery.users.domain.User? = null

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    var order: com.fooddelivery.orders.domain.Order? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    var targetType: ReviewTargetType = ReviewTargetType.RESTAURANT

    @Column(name = "target_id", nullable = false)
    var targetId: UUID = UUID.randomUUID()

    @Column(nullable = false)
    var rating: Int = 5

    @Column(columnDefinition = "TEXT")
    var comment: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ratings_breakdown")
    var ratingsBreakdown: String? = null

    @Column(name = "is_verified", nullable = false)
    var isVerified: Boolean = false

    @Column(name = "is_flagged", nullable = false)
    var isFlagged: Boolean = false

    @Column(name = "flag_reason", columnDefinition = "TEXT")
    var flagReason: String? = null

    @Column(name = "response", columnDefinition = "TEXT")
    var response: String? = null

    @Column(name = "responded_at")
    var respondedAt: java.time.OffsetDateTime? = null

    @Column(name = "helpful_count", nullable = false)
    var helpfulCount: Int = 0
}