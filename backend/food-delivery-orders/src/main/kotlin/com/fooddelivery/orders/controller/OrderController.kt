package com.fooddelivery.orders.controller

import com.fooddelivery.common.dto.ApiResponse
import com.fooddelivery.common.enums.OrderStatus
import com.fooddelivery.common.enums.UserRole
import com.fooddelivery.common.security.CurrentUser
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
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order management endpoints")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create a new order")
    fun createOrder(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @Valid @RequestBody request: CreateOrderRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<OrderResponse>> {
        val order = orderService.createOrder(currentUser.id, request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(order, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Get orders for the current user")
    fun getOrders(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @RequestParam(required = false) status: OrderStatus?,
        @PageableDefault(size = 20, sort = ["createdAt"], direction = org.springframework.data.domain.Sort.Direction.DESC) pageable: Pageable,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Page<OrderSummaryResponse>>> {
        val orders = orderService.getOrders(currentUser.id, currentUser.role, status, pageable)
        return ResponseEntity.ok(ApiResponse.ok(orders, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('RESTAURANT_OWNER') or hasRole('DELIVERY_PERSON') or hasRole('ADMIN')")
    @Operation(summary = "Get order details by ID")
    fun getOrder(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable orderId: UUID,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<OrderResponse>> {
        val order = orderService.getOrderById(currentUser.id, orderId, currentUser.role)
        return ResponseEntity.ok(ApiResponse.ok(order, httpRequest.getHeader("X-Request-ID")))
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('DELIVERY_PERSON') or hasRole('ADMIN')")
    @Operation(summary = "Update order status")
    fun updateOrderStatus(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable orderId: UUID,
        @Valid @RequestBody request: UpdateOrderStatusRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<OrderResponse>> {
        val order = orderService.updateOrderStatus(orderId, request, currentUser.id, currentUser.role)
        return ResponseEntity.ok(ApiResponse.ok(order, httpRequest.getHeader("X-Request-ID")))
    }

    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Cancel an order")
    fun cancelOrder(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable orderId: UUID,
        @RequestParam(required = false) reason: String?,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<OrderResponse>> {
        val order = orderService.cancelOrder(orderId, currentUser.id, currentUser.role, reason)
        return ResponseEntity.ok(ApiResponse.ok(order, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/{orderId}/history")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Get order status history")
    fun getOrderHistory(
        @PathVariable orderId: UUID,
        @PageableDefault(size = 20) pageable: Pageable,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Page<OrderStatusHistoryResponse>>> {
        val history = orderService.getOrderStatusHistory(orderId, pageable)
        return ResponseEntity.ok(ApiResponse.ok(history, httpRequest.getHeader("X-Request-ID")))
    }
}