package com.fooddelivery.users.repository

import com.fooddelivery.common.enums.AddressType
import com.fooddelivery.users.domain.Address
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AddressRepository : JpaRepository<Address, UUID> {

    fun findByUserId(userId: UUID): List<Address>

    fun findByUserId(userId: UUID, pageable: Pageable): Page<Address>

    fun findByUserIdAndIsDefault(userId: UUID, isDefault: Boolean): Address?

    fun findByUserIdAndAddressType(userId: UUID, addressType: AddressType): List<Address>

    fun findDefaultByUserId(userId: UUID): Address?

    fun countByUserId(userId: UUID): Long
}