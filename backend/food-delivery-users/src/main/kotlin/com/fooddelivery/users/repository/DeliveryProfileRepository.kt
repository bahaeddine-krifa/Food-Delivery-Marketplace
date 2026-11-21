package com.fooddelivery.users.repository

import com.fooddelivery.common.enums.VerificationStatus
import com.fooddelivery.users.domain.DeliveryProfile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DeliveryProfileRepository : JpaRepository<DeliveryProfile, UUID> {

    fun findByUserId(userId: UUID): DeliveryProfile?

    fun findByBackgroundCheckStatus(status: VerificationStatus, pageable: Pageable): Page<DeliveryProfile>

    @Query("SELECT d FROM DeliveryProfile d WHERE d.isAvailable = true AND d.backgroundCheckStatus = 'APPROVED'")
    fun findAvailableDeliveryPersons(pageable: Pageable): Page<DeliveryProfile>
}