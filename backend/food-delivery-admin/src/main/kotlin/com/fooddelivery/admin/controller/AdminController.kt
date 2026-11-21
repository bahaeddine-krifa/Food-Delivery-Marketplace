package com.fooddelivery.admin.controller

import com.fooddelivery.admin.dto.DashboardStatsResponse
import com.fooddelivery.admin.dto.UpdateUserStatusRequest
import com.fooddelivery.admin.dto.UpdateUserRoleRequest
import com.fooddelivery.admin.dto.UserListResponse
import com.fooddelivery.admin.dto.RestaurantApplicationListResponse
import com.fooddelivery.admin.dto.RestaurantListResponse
import com.fooddelivery.admin.dto.UpdateRestaurantStatusRequest
import com.fooddelivery.admin.dto.OrderOverviewResponse
import com.fooddelivery.admin.dto.DeliveryOverviewResponse
import com.fooddelivery.admin.dto.PaymentOverviewResponse
import com.fooddelivery.admin.dto.SystemConfigResponse
import com.fooddelivery.admin.dto.UpdateConfigRequest
import com.fooddelivery.admin.service.AdminService
import com.fooddelivery.common.dto.ApiResponse
import com.fooddelivery.common.enums.ApplicationStatus
import com.fooddelivery.common.enums.OrderStatus
import com.fooddelivery.common.enums.RestaurantStatus
import com.fooddelivery.common.enums.UserRole
import com.fooddelivery.common.enums.UserStatus
import com.fooddelivery.common.security.CurrentUser
import com.fooddelivery.reviews.dto.ReviewResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Admin panel endpoints")
@PreAuthorize("hasRole('ADMIN')")
class AdminController(
    private val adminService: AdminService
) {

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard statistics")
    fun getDashboardStats(): ResponseEntity<ApiResponse<DashboardStatsResponse>> {
        val stats = adminService.getDashboardStats()
        return ResponseEntity.ok(ApiResponse.ok(stats))
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users with filters")
    fun getUsers(
        @RequestParam(required = false) role: UserRole?,
        @RequestParam(required = false) status: UserStatus?,
        @PageableDefault(size = 20, sort = ["createdAt,desc"]) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<UserListResponse>>> {
        val users = adminService.getUsers(role, status, pageable)
        return ResponseEntity.ok(ApiResponse.ok(users))
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get user by ID")
    fun getUser(@PathVariable userId: UUID): ResponseEntity<ApiResponse<UserListResponse>> {
        val user = adminService.getUserById(userId)
        return ResponseEntity.ok(ApiResponse.ok(user))
    }

    @PutMapping("/users/{userId}/status")
    @Operation(summary = "Update user status")
    fun updateUserStatus(
        @PathVariable userId: UUID,
        @Valid @RequestBody request: UpdateUserStatusRequest
    ): ResponseEntity<ApiResponse<UserListResponse>> {
        val user = adminService.updateUserStatus(userId, request)
        return ResponseEntity.ok(ApiResponse.ok(user, "User status updated successfully"))
    }

    @PutMapping("/users/{userId}/role")
    @Operation(summary = "Update user role")
    fun updateUserRole(
        @PathVariable userId: UUID,
        @Valid @RequestBody request: UpdateUserRoleRequest
    ): ResponseEntity<ApiResponse<UserListResponse>> {
        val user = adminService.updateUserRole(userId, request)
        return ResponseEntity.ok(ApiResponse.ok(user, "User role updated successfully"))
    }

    @GetMapping("/restaurant-applications")
    @Operation(summary = "Get restaurant applications")
    fun getRestaurantApplications(
        @RequestParam(required = false) status: ApplicationStatus?,
        @PageableDefault(size = 20, sort = ["submittedAt,desc"]) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<RestaurantApplicationListResponse>>> {
        val applications = adminService.getRestaurantApplications(status, pageable)
        return ResponseEntity.ok(ApiResponse.ok(applications))
    }

    @PostMapping("/restaurant-applications/{applicationId}/approve")
    @Operation(summary = "Approve restaurant application")
    fun approveRestaurantApplication(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable applicationId: UUID
    ): ResponseEntity<ApiResponse<com.fooddelivery.restaurants.domain.Restaurant>> {
        val restaurant = adminService.approveRestaurantApplication(applicationId, currentUser.id)
        return ResponseEntity.ok(ApiResponse.ok(restaurant, "Restaurant application approved"))
    }

    @PostMapping("/restaurant-applications/{applicationId}/reject")
    @Operation(summary = "Reject restaurant application")
    fun rejectRestaurantApplication(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable applicationId: UUID,
        @RequestParam reason: String
    ): ResponseEntity<ApiResponse<com.fooddelivery.restaurants.domain.RestaurantApplication>> {
        val application = adminService.rejectRestaurantApplication(applicationId, currentUser.id, reason)
        return ResponseEntity.ok(ApiResponse.ok(application, "Restaurant application rejected"))
    }

    @GetMapping("/restaurants")
    @Operation(summary = "Get all restaurants")
    fun getRestaurants(
        @RequestParam(required = false) status: RestaurantStatus?,
        @PageableDefault(size = 20, sort = ["createdAt,desc"]) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<RestaurantListResponse>>> {
        val restaurants = adminService.getRestaurants(status, pageable)
        return ResponseEntity.ok(ApiResponse.ok(restaurants))
    }

    @PutMapping("/restaurants/{restaurantId}/status")
    @Operation(summary = "Update restaurant status")
    fun updateRestaurantStatus(
        @PathVariable restaurantId: UUID,
        @Valid @RequestBody request: UpdateRestaurantStatusRequest
    ): ResponseEntity<ApiResponse<RestaurantListResponse>> {
        val restaurant = adminService.updateRestaurantStatus(restaurantId, request)
        return ResponseEntity.ok(ApiResponse.ok(restaurant, "Restaurant status updated successfully"))
    }

    @GetMapping("/orders")
    @Operation(summary = "Get all orders")
    fun getOrders(
        @RequestParam(required = false) status: OrderStatus?,
        @PageableDefault(size = 20, sort = ["createdAt,desc"]) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<OrderOverviewResponse>>> {
        val orders = adminService.getOrders(status, pageable)
        return ResponseEntity.ok(ApiResponse.ok(orders))
    }

    @GetMapping("/deliveries")
    @Operation(summary = "Get all deliveries")
    fun getDeliveries(
        @RequestParam(required = false) status: com.fooddelivery.common.enums.DeliveryStatus?,
        @PageableDefault(size = 20, sort = ["createdAt,desc"]) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<DeliveryOverviewResponse>>> {
        val deliveries = adminService.getDeliveries(status, pageable)
        return ResponseEntity.ok(ApiResponse.ok(deliveries))
    }

    @GetMapping("/payments")
    @Operation(summary = "Get all payments")
    fun getPayments(
        @RequestParam(required = false) status: com.fooddelivery.common.enums.PaymentStatus?,
        @PageableDefault(size = 20, sort = ["createdAt,desc"]) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<PaymentOverviewResponse>>> {
        val payments = adminService.getPayments(status, pageable)
        return ResponseEntity.ok(ApiResponse.ok(payments))
    }

    @GetMapping("/reviews/flagged")
    @Operation(summary = "Get flagged reviews")
    fun getFlaggedReviews(
        @PageableDefault(size = 20, sort = ["createdAt,desc"]) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<ReviewResponse>>> {
        val reviews = adminService.getFlaggedReviews(pageable)
        return ResponseEntity.ok(ApiResponse.ok(reviews))
    }

    @GetMapping("/config")
    @Operation(summary = "Get system configuration")
    fun getSystemConfig(): ResponseEntity<ApiResponse<List<SystemConfigResponse>>> {
        val config = adminService.getSystemConfig()
        return ResponseEntity.ok(ApiResponse.ok(config))
    }

    @PutMapping("/config/{key}")
    @Operation(summary = "Update system configuration")
    fun updateSystemConfig(
        @PathVariable key: String,
        @Valid @RequestBody request: UpdateConfigRequest
    ): ResponseEntity<ApiResponse<SystemConfigResponse>> {
        val config = adminService.updateSystemConfig(key, request)
        return ResponseEntity.ok(ApiResponse.ok(config, "Configuration updated successfully"))
    }
}