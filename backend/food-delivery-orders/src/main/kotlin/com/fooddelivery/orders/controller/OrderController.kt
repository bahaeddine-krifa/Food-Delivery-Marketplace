package com.fooddelivery.orders.controller

import com.fooddelivery.auth.service.JwtService
import com.fooddelivery.common.dto.ApiResponse
import com.fooddelivery.common.enums.OrderStatus
import com.fooddelivery.common.enums.UserRole
import com.fooddelivery.orders.dto.CreateOrderRequest
import com.fooddelivery.orders.dto.OrderItemResponse
import com.fooddelivery.orders.dto.OrderResponse
import com.fooddelivery.orders.dto.OrderStatusHistoryResponse
import com.fooddelivery.orders.dto.OrderSummaryResponse
import com.fooddelivery.orders.dto.UpdateOrderStatusRequest
import com.fooddelivery.orders.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order management endpoints")
class OrderController(
    private val orderService: OrderService,
    private val jwtService: JwtService
) {

    private fun getCurrentUserId(httpRequest: HttpServletRequest): UUID {
        val authHeader = httpRequest.getHeader("Authorization")
            ?: throw IllegalStateException("No authorization header")
        val token = authHeader.removePrefix("Bearer ").trim()
        return jwtService.extractUserId(token)
    }

    private fun getCurrentUserRole(httpRequest: HttpServletRequest): UserRole {
        val authHeader = httpRequest.getHeader("Authorization")
            ?: throw IllegalStateException("No authorization header")
        val token = authHeader.removePrefix("Bearer ").trim()
        return jwtService.extractUserRole(token)
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create a new order")
    fun createOrder(
        httpRequest: HttpServletRequest,
        @Valid @RequestBody request: CreateOrderRequest
    ): ResponseEntity<ApiResponse<OrderResponse>> {
        val userId = getCurrentUserId(httpRequest)
        val order = orderService.createOrder(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(order, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Get orders for the current user")
    fun getOrders(
        httpRequest: HttpServletRequest,
        @RequestParam(required = false) status: OrderStatus?,
        @PageableDefault(size = 20, sort = ["createdAt,desc"]) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<OrderSummaryResponse>>> {
        val userId = getCurrentUserId(httpRequest)
        val userRole = getCurrentUserRole(httpRequest)
        val orders = orderService.getOrders(userId, userRole, status, pageable)
        return ResponseEntity.ok(ApiResponse.ok(orders, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('RESTAURANT_OWNER') or hasRole('DELIVERY_PERSON') or hasRole('ADMIN')")
    @Operation(summary = "Get order details by ID")
    fun getOrder(
        httpRequest: HttpServletRequest,
        @PathVariable orderId: UUID
    ): ResponseEntity<ApiResponse<OrderResponse>> {
        val userId = getCurrentUserId(httpRequest)
        val userRole = getCurrentUserRole(httpRequest)
        val order = orderService.getOrderById(userId, orderId, userRole)
        return ResponseEntity.ok(ApiResponse.ok(order, httpRequest.getHeader("X-Request-ID")))
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('DELIVERY_PERSON') or hasRole('ADMIN')")
    @Operation(summary = "Update order status")
    fun updateOrderStatus(
        httpRequest: HttpServletRequest,
        @PathVariable orderId: UUID,
        @Valid @RequestBody request: UpdateOrderStatusRequest
    ): ResponseEntity<ApiResponse<OrderResponse>> {
        val userId = getCurrentUserId(httpRequest)
        val userRole = getCurrentUserRole(httpRequest)
        val order = orderService.updateOrderStatus(orderId, request, userId, userRole)
        return ResponseEntity.ok(ApiResponse.ok(order, httpRequest.getHeader("X-Request-ID")))
    }

    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Cancel an order")
    fun cancelOrder(
        httpRequest: HttpServletRequest,
        @PathVariable orderId: UUID,
        @RequestParam(required = false) reason: String?
    ): ResponseEntity<ApiResponse<OrderResponse>> {
        val userId = getCurrentUserId(httpRequest)
        val userRole = getCurrentUserRole(httpRequest)
        val order = orderService.cancelOrder(orderId, userId, userRole, reason)
        return ResponseEntity.ok(ApiResponse.ok(order, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/{orderId}/history")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Get order status history")
    fun getOrderHistory(
        httpRequest: HttpServletRequest,
        @PathVariable orderId: UUID,
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<OrderStatusHistoryResponse>>> {
        val history = orderService.getOrderStatusHistory(orderId, pageable)
        return ResponseEntity.ok(ApiResponse.ok(history, httpRequest.getHeader("X-Request-ID")))
    }
}