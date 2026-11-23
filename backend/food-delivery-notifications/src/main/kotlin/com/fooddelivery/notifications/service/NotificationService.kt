package com.fooddelivery.notifications.service

import com.fooddelivery.common.enums.NotificationPriority
import com.fooddelivery.common.enums.NotificationType
import com.fooddelivery.common.exception.AccessDeniedException
import com.fooddelivery.common.exception.ResourceNotFoundException
import com.fooddelivery.notifications.domain.Notification
import com.fooddelivery.notifications.dto.BulkNotificationRequest
import com.fooddelivery.notifications.dto.CreateNotificationRequest
import com.fooddelivery.notifications.dto.MarkAsReadRequest
import com.fooddelivery.notifications.dto.NotificationResponse
import com.fooddelivery.notifications.dto.UnreadCountResponse
import com.fooddelivery.notifications.repository.NotificationRepository
import com.fooddelivery.users.domain.User
import com.fooddelivery.users.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

@Service
@Transactional
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
    private val emailService: EmailService,
    private val pushService: PushNotificationService
) {

    fun createNotification(request: CreateNotificationRequest): NotificationResponse {
        val user = userRepository.findById(request.userId)
            .orElseThrow { ResourceNotFoundException("User", request.userId) }

        val notification = Notification().apply {
            this.user = user
            this.type = request.type
            this.priority = request.priority
            this.title = request.title
            this.body = request.body
            this.data = request.data
            this.relatedEntityType = request.relatedEntityType
            this.relatedEntityId = request.relatedEntityId
        }

        val saved = notificationRepository.save(notification)

        // Send via configured channels asynchronously
        if (request.sendViaEmail) {
            sendEmailAsync(saved)
        }
        if (request.sendViaPush) {
            sendPushAsync(saved)
        }
        if (request.sendViaSms) {
            sendSmsAsync(saved)
        }

        return NotificationResponse.fromEntity(saved)
    }

    fun createBulkNotifications(request: BulkNotificationRequest): List<NotificationResponse> {
        val users = userRepository.findAllById(request.userIds)
        val notifications = users.map { user ->
            Notification().apply {
                this.user = user
                this.type = request.type
                this.priority = request.priority
                this.title = request.title
                this.body = request.body
                this.data = request.data
            }
        }

        val saved = notificationRepository.saveAll(notifications)

        // Send via configured channels asynchronously
        if (request.sendViaEmail) {
            saved.forEach { sendEmailAsync(it) }
        }
        if (request.sendViaPush) {
            saved.forEach { sendPushAsync(it) }
        }
        if (request.sendViaSms) {
            saved.forEach { sendSmsAsync(it) }
        }

        return saved.map { NotificationResponse.fromEntity(it) }
    }

    fun getNotifications(userId: UUID, pageable: Pageable): Page<NotificationResponse> {
        return notificationRepository.findByUser_Id(userId, pageable)
            .map { NotificationResponse.fromEntity(it) }
    }

    fun getUnreadNotifications(userId: UUID, pageable: Pageable): Page<NotificationResponse> {
        return notificationRepository.findUnreadByUserId(userId, pageable)
            .map { NotificationResponse.fromEntity(it) }
    }

    fun getUnreadCount(userId: UUID): UnreadCountResponse {
        val count = notificationRepository.countByUser_IdAndIsRead(userId, false)
        return UnreadCountResponse(count)
    }

    fun markAsRead(userId: UUID, request: MarkAsReadRequest): Int {
        val notifications = notificationRepository.findAllById(request.notificationIds)
        var updated = 0
        for (notification in notifications) {
            if (notification.user?.id == userId && !notification.isRead) {
                notification.isRead = true
                notification.readAt = OffsetDateTime.now()
                notificationRepository.save(notification)
                updated++
            }
        }
        return updated
    }

    fun markAllAsRead(userId: UUID): Int {
        val unread = notificationRepository.findByUser_IdAndIsRead(userId, false, org.springframework.data.domain.Pageable.unpaged())
        var updated = 0
        for (notification in unread.content) {
            notification.isRead = true
            notification.readAt = OffsetDateTime.now()
            notificationRepository.save(notification)
            updated++
        }
        return updated
    }

    fun deleteNotification(userId: UUID, notificationId: UUID) {
        val notification = notificationRepository.findById(notificationId)
            .orElseThrow { ResourceNotFoundException("Notification", notificationId) }

        if (notification.user?.id != userId) {
            throw AccessDeniedException("You can only delete your own notifications")
        }

        notificationRepository.delete(notification)
    }

    @Async
    fun sendEmailAsync(notification: Notification) {
        try {
            emailService.sendNotificationEmail(notification)
            notification.sentViaEmail = true
            notificationRepository.save(notification)
        } catch (e: Exception) {
            // Log error but don't fail
        }
    }

    @Async
    fun sendPushAsync(notification: Notification) {
        try {
            pushService.sendPushNotification(notification)
            notification.sentViaPush = true
            notificationRepository.save(notification)
        } catch (e: Exception) {
            // Log error but don't fail
        }
    }

    @Async
    fun sendSmsAsync(notification: Notification) {
        try {
            // TODO: Implement SMS sending
            notification.sentViaSms = true
            notificationRepository.save(notification)
        } catch (e: Exception) {
            // Log error but don't fail
        }
    }

    // Convenience methods for common notifications
    fun notifyOrderPlaced(userId: UUID, orderNumber: String, orderId: UUID) {
        createNotification(CreateNotificationRequest(
            userId = userId,
            type = NotificationType.ORDER_PLACED,
            priority = NotificationPriority.HIGH,
            title = "Order Placed",
            body = "Your order $orderNumber has been placed successfully",
            relatedEntityType = "ORDER",
            relatedEntityId = orderId,
            sendViaEmail = true,
            sendViaPush = true
        ))
    }

    fun notifyOrderAccepted(userId: UUID, orderNumber: String, orderId: UUID, prepTime: Int) {
        createNotification(CreateNotificationRequest(
            userId = userId,
            type = NotificationType.ORDER_ACCEPTED,
            priority = NotificationPriority.HIGH,
            title = "Order Accepted",
            body = "Your order $orderNumber has been accepted. Estimated preparation time: $prepTime minutes",
            relatedEntityType = "ORDER",
            relatedEntityId = orderId,
            sendViaEmail = true,
            sendViaPush = true
        ))
    }

    fun notifyOrderReady(userId: UUID, orderNumber: String, orderId: UUID) {
        createNotification(CreateNotificationRequest(
            userId = userId,
            type = NotificationType.ORDER_READY,
            priority = NotificationPriority.HIGH,
            title = "Order Ready",
            body = "Your order $orderNumber is ready for pickup",
            relatedEntityType = "ORDER",
            relatedEntityId = orderId,
            sendViaEmail = true,
            sendViaPush = true
        ))
    }

    fun notifyDeliveryAssigned(userId: UUID, orderNumber: String, orderId: UUID, deliveryPersonName: String) {
        createNotification(CreateNotificationRequest(
            userId = userId,
            type = NotificationType.DELIVERY_ASSIGNED,
            priority = NotificationPriority.HIGH,
            title = "Delivery Assigned",
            body = "$deliveryPersonName will deliver your order $orderNumber",
            relatedEntityType = "ORDER",
            relatedEntityId = orderId,
            sendViaEmail = true,
            sendViaPush = true
        ))
    }

    fun notifyDeliveryDelivered(userId: UUID, orderNumber: String, orderId: UUID) {
        createNotification(CreateNotificationRequest(
            userId = userId,
            type = NotificationType.DELIVERY_DELIVERED,
            priority = NotificationPriority.NORMAL,
            title = "Order Delivered",
            body = "Your order $orderNumber has been delivered. Enjoy your meal!",
            relatedEntityType = "ORDER",
            relatedEntityId = orderId,
            sendViaEmail = true,
            sendViaPush = true
        ))
    }

    fun notifyPaymentReceived(userId: UUID, orderNumber: String, orderId: UUID, amount: java.math.BigDecimal) {
        createNotification(CreateNotificationRequest(
            userId = userId,
            type = NotificationType.PAYMENT_RECEIVED,
            priority = NotificationPriority.NORMAL,
            title = "Payment Received",
            body = "Payment of $amount received for order $orderNumber",
            relatedEntityType = "ORDER",
            relatedEntityId = orderId,
            sendViaEmail = true
        ))
    }
}