package com.fooddelivery.reviews.dto

import com.fooddelivery.common.enums.ReviewTargetType
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class CreateReviewRequest(
    @NotNull
    val orderId: UUID,

    @NotNull
    val targetType: ReviewTargetType,

    @NotNull
    val targetId: UUID,

    @NotNull
    @Min(1)
    @Max(5)
    val rating: Int,

    @Size(max = 2000)
    val comment: String? = null,

    val ratingsBreakdown: String? = null
)

data class UpdateReviewRequest(
    @Min(1)
    @Max(5)
    val rating: Int? = null,

    @Size(max = 2000)
    val comment: String? = null,

    val ratingsBreakdown: String? = null
)

data class RespondToReviewRequest(
    @NotNull
    @Size(max = 2000)
    val response: String
)

data class FlagReviewRequest(
    @NotNull
    @Size(max = 500)
    val reason: String
)

data class ReviewResponse(
    val id: UUID,
    val customerId: UUID,
    val customerName: String,
    val orderId: UUID,
    val orderNumber: String,
    val targetType: ReviewTargetType,
    val targetId: UUID,
    val rating: Int,
    val comment: String?,
    val ratingsBreakdown: String?,
    val isVerified: Boolean,
    val isFlagged: Boolean,
    val flagReason: String?,
    val response: String?,
    val respondedAt: java.time.OffsetDateTime?,
    val helpfulCount: Int,
    val createdAt: java.time.OffsetDateTime,
    val updatedAt: java.time.OffsetDateTime
) {
    companion object {
        fun fromEntity(review: com.fooddelivery.reviews.domain.Review): ReviewResponse {
            return ReviewResponse(
                id = review.id,
                customerId = review.customer?.id ?: UUID.randomUUID(),
                customerName = "${review.customer?.firstName ?: ""} ${review.customer?.lastName ?: ""}".trim(),
                orderId = review.order?.id ?: UUID.randomUUID(),
                orderNumber = review.order?.orderNumber ?: "",
                targetType = review.targetType,
                targetId = review.targetId,
                rating = review.rating,
                comment = review.comment,
                ratingsBreakdown = review.ratingsBreakdown,
                isVerified = review.isVerified,
                isFlagged = review.isFlagged,
                flagReason = review.flagReason,
                response = review.response,
                respondedAt = review.respondedAt,
                helpfulCount = review.helpfulCount,
                createdAt = review.createdAt,
                updatedAt = review.updatedAt
            )
        }
    }
}

data class ReviewSummaryResponse(
    val targetType: ReviewTargetType,
    val targetId: UUID,
    val averageRating: Double,
    val totalReviews: Long,
    val ratingDistribution: Map<Int, Long>
)

data class ReviewStatsResponse(
    val averageRating: Double,
    val totalReviews: Long,
    val ratingDistribution: Map<Int, Long>
)