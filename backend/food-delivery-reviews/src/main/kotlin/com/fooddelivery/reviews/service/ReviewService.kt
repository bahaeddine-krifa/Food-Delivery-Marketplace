package com.fooddelivery.reviews.service

import com.fooddelivery.common.enums.OrderStatus
import com.fooddelivery.common.enums.ReviewTargetType
import com.fooddelivery.common.exception.BusinessException
import com.fooddelivery.common.exception.ResourceNotFoundException
import com.fooddelivery.reviews.domain.Review
import com.fooddelivery.reviews.dto.CreateReviewRequest
import com.fooddelivery.reviews.dto.FlagReviewRequest
import com.fooddelivery.reviews.dto.RespondToReviewRequest
import com.fooddelivery.reviews.dto.ReviewResponse
import com.fooddelivery.reviews.dto.ReviewStatsResponse
import com.fooddelivery.reviews.dto.UpdateReviewRequest
import com.fooddelivery.reviews.repository.ReviewRepository
import com.fooddelivery.common.enums.UserRole
import com.fooddelivery.orders.domain.Order
import com.fooddelivery.orders.repository.OrderRepository
import com.fooddelivery.restaurants.domain.Restaurant
import com.fooddelivery.restaurants.repository.RestaurantRepository
import com.fooddelivery.products.domain.Product
import com.fooddelivery.products.repository.ProductRepository
import com.fooddelivery.users.domain.User
import com.fooddelivery.users.domain.DeliveryProfile
import com.fooddelivery.users.repository.UserRepository
import com.fooddelivery.users.repository.DeliveryProfileRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Service
@Transactional
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val orderRepository: OrderRepository,
    private val restaurantRepository: RestaurantRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val deliveryProfileRepository: DeliveryProfileRepository
) {

    fun createReview(customerId: UUID, request: CreateReviewRequest): ReviewResponse {
        val customer = userRepository.findById(customerId)
            .orElseThrow { ResourceNotFoundException("Customer", customerId) }

        val order = orderRepository.findById(request.orderId)
            .orElseThrow { ResourceNotFoundException("Order", request.orderId) }

        // Verify order belongs to customer
        if (order.customer?.id != customerId) {
            throw BusinessException("Order does not belong to customer", "INVALID_ORDER")
        }

        // Verify order is delivered
        if (order.status != OrderStatus.DELIVERED) {
            throw BusinessException("Can only review delivered orders", "ORDER_NOT_DELIVERED")
        }

        // Check if review already exists
        if (reviewRepository.existsByCustomerIdAndOrderId(customerId, request.orderId)) {
            throw BusinessException("Review already exists for this order", "REVIEW_EXISTS")
        }

        // Verify target exists and matches
        validateTarget(request.targetType, request.targetId, order)

        val review = Review().apply {
            this.customer = customer
            this.order = order
            this.targetType = request.targetType
            this.targetId = request.targetId
            this.rating = request.rating
            this.comment = request.comment
            this.ratingsBreakdown = request.ratingsBreakdown
            this.isVerified = true // Verified since order is delivered
        }

        val saved = reviewRepository.save(review)

        // Update target rating aggregates
        updateTargetRating(request.targetType, request.targetId)

        // Notify target owner
        notifyTargetOwner(request.targetType, request.targetId, saved)

        return ReviewResponse.fromEntity(saved)
    }

    fun updateReview(customerId: UUID, reviewId: UUID, request: UpdateReviewRequest): ReviewResponse {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { ResourceNotFoundException("Review", reviewId) }

        if (review.customer?.id != customerId) {
            throw BusinessException("You can only update your own reviews", "UNAUTHORIZED")
        }

        request.rating?.let { review.rating = it }
        request.comment?.let { review.comment = it }
        request.ratingsBreakdown?.let { review.ratingsBreakdown = it }

        val saved = reviewRepository.save(review)
        updateTargetRating(review.targetType, review.targetId)

        return ReviewResponse.fromEntity(saved)
    }

    fun respondToReview(ownerId: UUID, reviewId: UUID, request: RespondToReviewRequest): ReviewResponse {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { ResourceNotFoundException("Review", reviewId) }

        // Verify owner owns the target
        verifyTargetOwnership(ownerId, review.targetType, review.targetId)

        review.response = request.response
        review.respondedAt = java.time.OffsetDateTime.now()

        val saved = reviewRepository.save(review)

        // TODO: Notify customer when NotificationService is available
        // notificationService.createNotification(...)

        return ReviewResponse.fromEntity(saved)
    }

    fun flagReview(adminId: UUID, reviewId: UUID, request: FlagReviewRequest): ReviewResponse {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { ResourceNotFoundException("Review", reviewId) }

        review.isFlagged = true
        review.flagReason = request.reason

        return ReviewResponse.fromEntity(reviewRepository.save(review))
    }

    fun getReviewById(reviewId: UUID): ReviewResponse {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { ResourceNotFoundException("Review", reviewId) }
        return ReviewResponse.fromEntity(review)
    }

    fun getReviewsByCustomer(customerId: UUID, pageable: Pageable): Page<ReviewResponse> {
        return reviewRepository.findByCustomerId(customerId, pageable)
            .map { ReviewResponse.fromEntity(it) }
    }

    fun getReviewsByTarget(targetType: ReviewTargetType, targetId: UUID, pageable: Pageable): Page<ReviewResponse> {
        return reviewRepository.findByTargetAndIsVerified(targetType, targetId, true, pageable)
            .map { ReviewResponse.fromEntity(it) }
    }

    fun getAllReviewsForTarget(targetType: ReviewTargetType, targetId: UUID, pageable: Pageable): Page<ReviewResponse> {
        return reviewRepository.findByTarget(targetType, targetId, pageable)
            .map { ReviewResponse.fromEntity(it) }
    }

    fun getReviewStats(targetType: ReviewTargetType, targetId: UUID): ReviewStatsResponse {
        val avgRating = reviewRepository.getAverageRating(targetType, targetId) ?: 0.0
        val totalReviews = reviewRepository.getReviewCount(targetType, targetId)

        // Get rating distribution
        val distribution = mutableMapOf<Int, Long>()
        for (i in 1..5) {
            distribution[i] = 0L
        }
        // Note: Would need a custom query for full distribution

        return ReviewStatsResponse(
            averageRating = avgRating,
            totalReviews = totalReviews,
            ratingDistribution = distribution
        )
    }

    fun getFlaggedReviews(targetType: ReviewTargetType, pageable: Pageable): Page<ReviewResponse> {
        return reviewRepository.findByTargetTypeAndIsFlagged(targetType, true, pageable)
            .map { ReviewResponse.fromEntity(it) }
    }

    private fun validateTarget(targetType: ReviewTargetType, targetId: UUID, order: Order) {
        when (targetType) {
            ReviewTargetType.RESTAURANT -> {
                val restaurant = restaurantRepository.findById(targetId)
                    .orElseThrow { ResourceNotFoundException("Restaurant", targetId) }
                if (restaurant.id != order.restaurant?.id) {
                    throw BusinessException("Restaurant does not match order", "INVALID_TARGET")
                }
            }
            ReviewTargetType.DELIVERY_PERSON -> {
                val deliveryPerson = userRepository.findById(targetId)
                    .orElseThrow { ResourceNotFoundException("Delivery Person", targetId) }
                if (deliveryPerson.role != com.fooddelivery.common.enums.UserRole.DELIVERY_PERSON) {
                    throw BusinessException("Target is not a delivery person", "INVALID_TARGET")
                }
                // Note: Would need to verify delivery person was assigned to this order
            }
            ReviewTargetType.PRODUCT -> {
                val product = productRepository.findById(targetId)
                    .orElseThrow { ResourceNotFoundException("Product", targetId) }
                val orderHasProduct = order.items?.any { it.product?.id == targetId } == true
                if (!orderHasProduct) {
                    throw BusinessException("Product not in order", "INVALID_TARGET")
                }
            }
        }
    }

    private fun verifyTargetOwnership(ownerId: UUID, targetType: ReviewTargetType, targetId: UUID) {
        when (targetType) {
            ReviewTargetType.RESTAURANT -> {
                val restaurant = restaurantRepository.findById(targetId)
                    .orElseThrow { ResourceNotFoundException("Restaurant", targetId) }
                if (restaurant.owner?.id != ownerId) {
                    throw BusinessException("You don't own this restaurant", "UNAUTHORIZED")
                }
            }
            ReviewTargetType.DELIVERY_PERSON -> {
                // Delivery persons can respond to their own reviews
                if (targetId != ownerId) {
                    throw BusinessException("You can only respond to reviews about yourself", "UNAUTHORIZED")
                }
            }
            ReviewTargetType.PRODUCT -> {
                val product = productRepository.findById(targetId)
                    .orElseThrow { ResourceNotFoundException("Product", targetId) }
                if (product.restaurant?.owner?.id != ownerId) {
                    throw BusinessException("You don't own this product's restaurant", "UNAUTHORIZED")
                }
            }
        }
    }

    private fun updateTargetRating(targetType: ReviewTargetType, targetId: UUID) {
        val avgRating = reviewRepository.getAverageRating(targetType, targetId)
        val reviewCount = reviewRepository.getReviewCount(targetType, targetId)

        when (targetType) {
            ReviewTargetType.RESTAURANT -> {
                restaurantRepository.findById(targetId).ifPresent { restaurant ->
                    restaurant.averageRating = BigDecimal.valueOf(avgRating ?: 0.0)
                    restaurant.totalReviews = reviewCount.toInt()
                    restaurantRepository.save(restaurant)
                }
            }
            ReviewTargetType.PRODUCT -> {
                productRepository.findById(targetId).ifPresent { product ->
                    product.averageRating = BigDecimal.valueOf(avgRating ?: 0.0)
                    product.totalReviews = reviewCount.toInt()
                    productRepository.save(product)
                }
            }
            ReviewTargetType.DELIVERY_PERSON -> {
                deliveryProfileRepository.findByUserId(targetId)?.let { profile ->
                    profile.averageRating = BigDecimal.valueOf(avgRating ?: 0.0)
                    profile.totalDeliveries = reviewCount.toInt()
                    deliveryProfileRepository.save(profile)
                }
            }
        }
    }

    private fun notifyTargetOwner(targetType: ReviewTargetType, targetId: UUID, review: Review) {
        val ownerId = when (targetType) {
            ReviewTargetType.RESTAURANT -> restaurantRepository.findById(targetId).map { it.owner?.id }.orElse(null)
            ReviewTargetType.DELIVERY_PERSON -> targetId
            ReviewTargetType.PRODUCT -> productRepository.findById(targetId).map { it.restaurant?.owner?.id }.orElse(null)
        }

        ownerId?.let { id ->
            // TODO: Send notification when NotificationService is available
            // notificationService.createNotification(
            //     com.fooddelivery.notifications.dto.NotificationDtos.CreateNotificationRequest(
            //         userId = id,
            //         type = NotificationType.REVIEW_RECEIVED,
            //         priority = com.fooddelivery.common.enums.NotificationPriority.NORMAL,
            //         title = "New Review",
            //         body = "You received a ${review.rating}-star review",
            //         relatedEntityType = "REVIEW",
            //         relatedEntityId = review.id,
            //         sendViaEmail = true
            //     )
            // )
        }
    }
}