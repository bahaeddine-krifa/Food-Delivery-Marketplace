package com.fooddelivery.notifications.domain

import com.fooddelivery.common.domain.AuditableEntity
import com.fooddelivery.common.enums.NotificationPriority
import com.fooddelivery.common.enums.NotificationType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "notifications",
    indexes = [
        Index(name = "idx_notifications_user", columnList = "user_id,created_at DESC"),
        Index(name = "idx_notifications_user_unread", columnList = "user_id,is_read"),
        Index(name = "idx_notifications_type", columnList = "type"),
        Index(name = "idx_notifications_related", columnList = "related_entity_type,related_entity_id")
    ]
)
class Notification : AuditableEntity() {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: com.fooddelivery.users.domain.User? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var type: NotificationType = NotificationType.SYSTEM_ALERT

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var priority: NotificationPriority = NotificationPriority.NORMAL

    @Column(nullable = false, length = 200)
    var title: String = ""

    @Column(columnDefinition = "TEXT")
    var body: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data")
    var data: String? = null

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false

    @Column(name = "read_at")
    var readAt: OffsetDateTime? = null

    @Column(name = "related_entity_type", length = 50)
    var relatedEntityType: String? = null

    @Column(name = "related_entity_id")
    var relatedEntityId: UUID? = null

    @Column(name = "sent_via_email", nullable = false)
    var sentViaEmail: Boolean = false

    @Column(name = "sent_via_push", nullable = false)
    var sentViaPush: Boolean = false

    @Column(name = "sent_via_sms", nullable = false)
    var sentViaSms: Boolean = false
}