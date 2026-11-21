package com.fooddelivery.reviews.controller

import com.fooddelivery.common.dto.ApiResponse
import com.fooddelivery.common.enums.ReviewTargetType
import com.fooddelivery.common.security.CurrentUser
import com.fooddelivery.reviews.dto.CreateReviewRequest
import com.fooddelivery.reviews.dto.FlagReviewRequest
import com.fooddelivery.reviews.dto.RespondToReviewRequest
import com.fooddelivery.reviews.dto.ReviewResponse
import com.fooddelivery.reviews.dto.ReviewStatsResponse
import com.fooddelivery.reviews.dto.UpdateReviewRequest
import com.fooddelivery.reviews.service.ReviewService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Reviews", description = "Review management endpoints")
class ReviewController(
    private val reviewService: ReviewService
) {

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create a new review")
    fun createReview(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @Valid @RequestBody request: CreateReviewRequest
    ): ResponseEntity<ApiResponse<ReviewResponse>> {
        val review = reviewService.createReview(currentUser.id, request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(review, "Review created successfully"))
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Update a review")
    fun updateReview(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable reviewId: UUID,
        @Valid @RequestBody request: UpdateReviewRequest
    ): ResponseEntity<ApiResponse<ReviewResponse>> {
        val review = reviewService.updateReview(currentUser.id, reviewId, request)
        return ResponseEntity.ok(ApiResponse.ok(review, "Review updated successfully"))
    }

    @PostMapping("/{reviewId}/respond")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('DELIVERY_PERSON')")
    @Operation(summary = "Respond to a review (owner only)")
    fun respondToReview(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable reviewId: UUID,
        @Valid @RequestBody request: RespondToReviewRequest
    ): ResponseEntity<ApiResponse<ReviewResponse>> {
        val review = reviewService.respondToReview(currentUser.id, reviewId, request)
        return ResponseEntity.ok(ApiResponse.ok(review, "Response added successfully"))
    }

    @PostMapping("/{reviewId}/flag")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Flag a review (admin)")
    fun flagReview(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable reviewId: UUID,
        @Valid @RequestBody request: FlagReviewRequest
    ): ResponseEntity<ApiResponse<ReviewResponse>> {
        val review = reviewService.flagReview(currentUser.id, reviewId, request)
        return ResponseEntity.ok(ApiResponse.ok(review, "Review flagged successfully"))
    }

    @GetMapping("/{reviewId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('RESTAURANT_OWNER') or hasRole('DELIVERY_PERSON') or hasRole('ADMIN')")
    @Operation(summary = "Get review by ID")
    fun getReview(
        @PathVariable reviewId: UUID
    ): ResponseEntity<ApiResponse<ReviewResponse>> {
        val review = reviewService.getReviewById(reviewId)
        return ResponseEntity.ok(ApiResponse.ok(review))
    }

    @GetMapping("/my-reviews")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get current user's reviews")
    fun getMyReviews(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PageableDefault(size = 20, sort = ["createdAt,desc"]) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<ReviewResponse>>> {
        val reviews = reviewService.getReviewsByCustomer(currentUser.id, pageable)
        return ResponseEntity.ok(ApiResponse.ok(reviews))
    }

    @GetMapping("/target")
    @Operation(summary = "Get reviews for a target (restaurant, product, delivery person)")
    fun getReviewsByTarget(
        @RequestParam targetType: ReviewTargetType,
        @RequestParam targetId: UUID,
        @PageableDefault(size = 20, sort = ["createdAt,desc"]) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<ReviewResponse>>> {
        val reviews = reviewService.getReviewsByTarget(targetType, targetId, pageable)
        return ResponseEntity.ok(ApiResponse.ok(reviews))
    }

    @GetMapping("/target/stats")
    @Operation(summary = "Get review statistics for a target")
    fun getReviewStats(
        @RequestParam targetType: ReviewTargetType,
        @RequestParam targetId: UUID
    ): ResponseEntity<ApiResponse<ReviewStatsResponse>> {
        val stats = reviewService.getReviewStats(targetType, targetId)
        return ResponseEntity.ok(ApiResponse.ok(stats))
    }

    @GetMapping("/flagged")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get flagged reviews (admin)")
    fun getFlaggedReviews(
        @RequestParam targetType: ReviewTargetType,
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<ReviewResponse>>> {
        val reviews = reviewService.getFlaggedReviews(targetType, pageable)
        return ResponseEntity.ok(ApiResponse.ok(reviews))
    }
}