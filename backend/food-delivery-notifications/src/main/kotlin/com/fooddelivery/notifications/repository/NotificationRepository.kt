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

    fun findByUser_Id(userId: UUID, pageable: Pageable): Page<Notification>

    fun findByUser_IdAndIsRead(userId: UUID, isRead: Boolean, pageable: Pageable): Page<Notification>

    fun countByUser_IdAndIsRead(userId: UUID, isRead: Boolean): Long

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    fun findUnreadByUserId(userId: UUID, pageable: Pageable): Page<Notification>

    fun findByUser_IdAndType(userId: UUID, type: NotificationType, pageable: Pageable): Page<Notification>

    fun findByRelatedEntityTypeAndRelatedEntityId(relatedEntityType: String, relatedEntityId: UUID): List<Notification>
}