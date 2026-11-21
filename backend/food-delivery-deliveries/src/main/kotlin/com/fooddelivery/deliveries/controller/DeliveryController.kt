package com.fooddelivery.deliveries.controller

import com.fooddelivery.common.dto.ApiResponse
import com.fooddelivery.common.enums.DeliveryStatus
import com.fooddelivery.common.security.CurrentUser
import com.fooddelivery.deliveries.dto.AssignDeliveryRequest
import com.fooddelivery.deliveries.dto.DeliveryResponse
import com.fooddelivery.deliveries.dto.DeliverySummaryResponse
import com.fooddelivery.deliveries.dto.UpdateDeliveryStatusRequest
import com.fooddelivery.deliveries.service.DeliveryService
import com.fooddelivery.orders.repository.OrderRepository
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
@RequestMapping("/api/v1/deliveries")
@Tag(name = "Deliveries", description = "Delivery management endpoints")
class DeliveryController(
    private val deliveryService: DeliveryService,
    private val orderRepository: OrderRepository
) {

    @PostMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create delivery for an order (admin)")
    fun createDelivery(
        @PathVariable orderId: UUID,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<DeliveryResponse>> {
        val order = orderRepository.findById(orderId)
            .orElseThrow { com.fooddelivery.common.exception.ResourceNotFoundException("Order", orderId) }
        val delivery = deliveryService.createDeliveryForOrder(order)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(DeliveryResponse.fromEntity(delivery), httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/{deliveryId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('DELIVERY_PERSON') or hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Get delivery details by ID")
    fun getDelivery(
        @PathVariable deliveryId: UUID,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<DeliveryResponse>> {
        val delivery = deliveryService.getDeliveryById(deliveryId)
        return ResponseEntity.ok(ApiResponse.ok(delivery, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('DELIVERY_PERSON') or hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Get delivery by order ID")
    fun getDeliveryByOrder(
        @PathVariable orderId: UUID,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<DeliveryResponse>> {
        val delivery = deliveryService.getDeliveryByOrderId(orderId)
        return ResponseEntity.ok(ApiResponse.ok(delivery, httpRequest.getHeader("X-Request-ID")))
    }

    @PostMapping("/{deliveryId}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign a delivery person to a delivery")
    fun assignDeliveryPerson(
        @PathVariable deliveryId: UUID,
        @Valid @RequestBody request: AssignDeliveryRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<DeliveryResponse>> {
        val delivery = deliveryService.assignDeliveryPerson(deliveryId, request.deliveryPersonId)
        return ResponseEntity.ok(ApiResponse.ok(delivery, httpRequest.getHeader("X-Request-ID")))
    }

    @PutMapping("/{deliveryId}/status")
    @PreAuthorize("hasRole('DELIVERY_PERSON') or hasRole('ADMIN')")
    @Operation(summary = "Update delivery status")
    fun updateDeliveryStatus(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable deliveryId: UUID,
        @Valid @RequestBody request: UpdateDeliveryStatusRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<DeliveryResponse>> {
        val delivery = deliveryService.updateDeliveryStatus(deliveryId, request, currentUser.id, currentUser.role)
        return ResponseEntity.ok(ApiResponse.ok(delivery, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/my-deliveries")
    @PreAuthorize("hasRole('DELIVERY_PERSON')")
    @Operation(summary = "Get deliveries for the current delivery person")
    fun getMyDeliveries(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @RequestParam(required = false) status: DeliveryStatus?,
        @PageableDefault(size = 20, sort = ["createdAt"], direction = org.springframework.data.domain.Sort.Direction.DESC) pageable: Pageable,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Page<DeliverySummaryResponse>>> {
        val deliveries = deliveryService.getDeliveriesForDeliveryPerson(currentUser.id, status, pageable)
        return ResponseEntity.ok(ApiResponse.ok(deliveries, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('DELIVERY_PERSON') or hasRole('ADMIN')")
    @Operation(summary = "Get available deliveries for assignment")
    fun getAvailableDeliveries(
        @PageableDefault(size = 20) pageable: Pageable,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Page<DeliverySummaryResponse>>> {
        val deliveries = deliveryService.getAvailableDeliveries(pageable)
        return ResponseEntity.ok(ApiResponse.ok(deliveries, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all deliveries (admin)")
    fun getAllDeliveries(
        @RequestParam(required = false) status: DeliveryStatus?,
        @PageableDefault(size = 20, sort = ["createdAt"], direction = org.springframework.data.domain.Sort.Direction.DESC) pageable: Pageable,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Page<DeliverySummaryResponse>>> {
        val deliveries = deliveryService.getDeliveries(status, pageable)
        return ResponseEntity.ok(ApiResponse.ok(deliveries, httpRequest.getHeader("X-Request-ID")))
    }
}