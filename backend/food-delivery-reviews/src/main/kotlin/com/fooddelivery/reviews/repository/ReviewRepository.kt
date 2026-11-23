package com.fooddelivery.reviews.repository

import com.fooddelivery.common.enums.ReviewTargetType
import com.fooddelivery.reviews.domain.Review
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ReviewRepository : JpaRepository<Review, UUID> {

    fun findByOrderId(orderId: UUID): Review?

    fun findByCustomerId(customerId: UUID, pageable: Pageable): Page<Review>

    fun findByTargetTypeAndTargetId(targetType: ReviewTargetType, targetId: UUID, pageable: Pageable): Page<Review>

    fun findByTargetTypeAndTargetIdAndIsVerified(targetType: ReviewTargetType, targetId: UUID, isVerified: Boolean, pageable: Pageable): Page<Review>

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.targetType = :targetType AND r.targetId = :targetId AND r.isVerified = true")
    fun getAverageRating(targetType: ReviewTargetType, targetId: UUID): Double?

    @Query("SELECT COUNT(r) FROM Review r WHERE r.targetType = :targetType AND r.targetId = :targetId AND r.isVerified = true")
    fun getReviewCount(targetType: ReviewTargetType, targetId: UUID): Long

    fun existsByCustomerIdAndOrderId(customerId: UUID, orderId: UUID): Boolean

    fun findByTargetTypeAndIsFlagged(targetType: ReviewTargetType, isFlagged: Boolean, pageable: Pageable): Page<Review>

    fun countByIsFlagged(isFlagged: Boolean): Long

    fun findByIsFlaggedTrue(pageable: Pageable): Page<Review>
}