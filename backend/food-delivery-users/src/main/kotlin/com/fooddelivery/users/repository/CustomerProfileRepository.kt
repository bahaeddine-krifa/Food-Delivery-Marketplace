package com.fooddelivery.users.repository

import com.fooddelivery.users.domain.CustomerProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CustomerProfileRepository : JpaRepository<CustomerProfile, UUID> {

    fun findByUserId(userId: UUID): CustomerProfile?

    fun findByDefaultAddressId(addressId: UUID): CustomerProfile?
}