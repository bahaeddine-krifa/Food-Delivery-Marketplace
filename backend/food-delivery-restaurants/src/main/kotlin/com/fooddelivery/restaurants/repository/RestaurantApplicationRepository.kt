package com.fooddelivery.restaurants.repository

import com.fooddelivery.common.enums.ApplicationStatus
import com.fooddelivery.restaurants.domain.RestaurantApplication
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RestaurantApplicationRepository : JpaRepository<RestaurantApplication, UUID> {

    fun findByOwnerId(ownerId: UUID): RestaurantApplication?

    fun findByStatus(status: ApplicationStatus, pageable: Pageable): Page<RestaurantApplication>

    @Query("SELECT r FROM RestaurantApplication r WHERE r.status = 'SUBMITTED' OR r.status = 'UNDER_REVIEW'")
    fun findPendingApplications(pageable: Pageable): Page<RestaurantApplication>

    fun countByStatus(status: ApplicationStatus): Long
}