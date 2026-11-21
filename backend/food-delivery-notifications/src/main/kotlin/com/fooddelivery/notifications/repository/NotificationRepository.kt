package com.fooddelivery.notifications.repository

import com.fooddelivery.common.enums.NotificationType
import com.fooddelivery.notifications.domain.Notification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NotificationRepository : JpaRepository<Notification, UUID> {

    fun findByUserId(userId: UUID, pageable: Pageable): Page<Notification>

    fun findByUserIdAndIsRead(userId: UUID, isRead: Boolean, pageable: Pageable): Page<Notification>

    fun countByUserIdAndIsRead(userId: UUID, isRead: Boolean): Long

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.isRead = false")
    fun findUnreadByUserId(userId: UUID, pageable: Pageable): Page<Notification>

    fun findByUserIdAndType(userId: UUID, type: NotificationType, pageable: Pageable): Page<Notification>

    fun findByRelatedEntity(relatedEntityType: String, relatedEntityId: UUID): List<Notification>
}