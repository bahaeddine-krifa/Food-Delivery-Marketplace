package com.fooddelivery.notifications.dto

import com.fooddelivery.common.enums.NotificationPriority
import com.fooddelivery.common.enums.NotificationType
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class CreateNotificationRequest(
    @NotNull
    val userId: UUID,

    @NotNull
    val type: NotificationType,

    val priority: NotificationPriority = NotificationPriority.NORMAL,

    @NotNull
    @Size(max = 200)
    val title: String,

    @Size(max = 2000)
    val body: String? = null,

    val data: String? = null,

    val relatedEntityType: String? = null,
    val relatedEntityId: UUID? = null,

    val sendViaEmail: Boolean = false,
    val sendViaPush: Boolean = false,
    val sendViaSms: Boolean = false
)

data class BulkNotificationRequest(
    @NotNull
    val userIds: List<UUID>,

    @NotNull
    val type: NotificationType,

    val priority: NotificationPriority = NotificationPriority.NORMAL,

    @NotNull
    @Size(max = 200)
    val title: String,

    @Size(max = 2000)
    val body: String? = null,

    val data: String? = null,
    val sendViaEmail: Boolean = false,
    val sendViaPush: Boolean = false,
    val sendViaSms: Boolean = false
)

data class NotificationResponse(
    val id: UUID,
    val type: NotificationType,
    val priority: NotificationPriority,
    val title: String,
    val body: String?,
    val data: String?,
    val isRead: Boolean,
    val readAt: java.time.OffsetDateTime?,
    val relatedEntityType: String?,
    val relatedEntityId: UUID?,
    val sentViaEmail: Boolean,
    val sentViaPush: Boolean,
    val sentViaSms: Boolean,
    val createdAt: java.time.OffsetDateTime
) {
    companion object {
        fun fromEntity(notification: com.fooddelivery.notifications.domain.Notification): NotificationResponse {
            return NotificationResponse(
                id = notification.id,
                type = notification.type,
                priority = notification.priority,
                title = notification.title,
                body = notification.body,
                data = notification.data,
                isRead = notification.isRead,
                readAt = notification.readAt,
                relatedEntityType = notification.relatedEntityType,
                relatedEntityId = notification.relatedEntityId,
                sentViaEmail = notification.sentViaEmail,
                sentViaPush = notification.sentViaPush,
                sentViaSms = notification.sentViaSms,
                createdAt = notification.createdAt
            )
        }
    }
}

data class MarkAsReadRequest(
    @NotNull
    val notificationIds: List<UUID>
)

data class UnreadCountResponse(
    val count: Long
)