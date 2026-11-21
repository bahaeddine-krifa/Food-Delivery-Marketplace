package com.fooddelivery.notifications.controller

import com.fooddelivery.common.dto.ApiResponse
import com.fooddelivery.common.enums.NotificationType
import com.fooddelivery.common.security.CurrentUser
import com.fooddelivery.notifications.dto.BulkNotificationRequest
import com.fooddelivery.notifications.dto.CreateNotificationRequest
import com.fooddelivery.notifications.dto.MarkAsReadRequest
import com.fooddelivery.notifications.dto.NotificationResponse
import com.fooddelivery.notifications.dto.UnreadCountResponse
import com.fooddelivery.notifications.service.NotificationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Notification management endpoints")
@PreAuthorize("hasRole('CUSTOMER') or hasRole('DELIVERY_PERSON') or hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
class NotificationController(
    private val notificationService: NotificationService
) {

    private fun getCurrentUserId(): UUID {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("No authentication found")
        val principal = authentication.principal
        if (principal is CurrentUser) {
            return principal.id
        }
        throw IllegalStateException("Invalid authentication principal")
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a notification (admin)")
    fun createNotification(
        @Valid @RequestBody request: CreateNotificationRequest
    ): ResponseEntity<ApiResponse<NotificationResponse>> {
        val notification = notificationService.createNotification(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(notification, null))
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create bulk notifications (admin)")
    fun createBulkNotifications(
        @Valid @RequestBody request: BulkNotificationRequest
    ): ResponseEntity<ApiResponse<List<NotificationResponse>>> {
        val notifications = notificationService.createBulkNotifications(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(notifications, null))
    }

    @GetMapping
    @Operation(summary = "Get all notifications for the current user")
    fun getNotifications(
        @PageableDefault(size = 20, sort = ["createdAt,desc"]) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<NotificationResponse>>> {
        val userId = getCurrentUserId()
        val notifications = notificationService.getNotifications(userId, pageable)
        return ResponseEntity.ok(ApiResponse.ok(notifications, null))
    }

    @GetMapping("/unread")
    @Operation(summary = "Get unread notifications for the current user")
    fun getUnreadNotifications(
        @PageableDefault(size = 20, sort = ["createdAt,desc"]) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<NotificationResponse>>> {
        val userId = getCurrentUserId()
        val notifications = notificationService.getUnreadNotifications(userId, pageable)
        return ResponseEntity.ok(ApiResponse.ok(notifications, null))
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count for the current user")
    fun getUnreadCount(): ResponseEntity<ApiResponse<UnreadCountResponse>> {
        val userId = getCurrentUserId()
        val count = notificationService.getUnreadCount(userId)
        return ResponseEntity.ok(ApiResponse.ok(count, null))
    }

    @PutMapping("/read")
    @Operation(summary = "Mark notifications as read")
    fun markAsRead(
        @Valid @RequestBody request: MarkAsReadRequest
    ): ResponseEntity<ApiResponse<Int>> {
        val userId = getCurrentUserId()
        val count = notificationService.markAsRead(userId, request)
        return ResponseEntity.ok(ApiResponse.ok(count, null))
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    fun markAllAsRead(): ResponseEntity<ApiResponse<Int>> {
        val userId = getCurrentUserId()
        val count = notificationService.markAllAsRead(userId)
        return ResponseEntity.ok(ApiResponse.ok(count, null))
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Delete a notification")
    fun deleteNotification(
        @PathVariable notificationId: UUID
    ): ResponseEntity<ApiResponse<Unit>> {
        val userId = getCurrentUserId()
        notificationService.deleteNotification(userId, notificationId)
        return ResponseEntity.ok(ApiResponse.ok(Unit, null))
    }
}