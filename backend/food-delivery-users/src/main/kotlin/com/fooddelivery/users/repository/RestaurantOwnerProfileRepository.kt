package com.fooddelivery.users.repository

import com.fooddelivery.users.domain.RestaurantOwnerProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RestaurantOwnerProfileRepository : JpaRepository<RestaurantOwnerProfile, UUID> {

    fun findByUserId(userId: UUID): RestaurantOwnerProfile?

    fun findByTaxId(taxId: String): RestaurantOwnerProfile?
}