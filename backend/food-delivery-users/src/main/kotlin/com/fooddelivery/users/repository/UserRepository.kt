package com.fooddelivery.users.repository

import com.fooddelivery.common.enums.UserRole
import com.fooddelivery.common.enums.UserStatus
import com.fooddelivery.users.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID> {

    fun findByEmail(email: String): User?

    fun existsByEmail(email: String): Boolean

    fun findByEmailVerificationToken(token: String): User?

    fun findByPasswordResetToken(token: String): User?

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.status = :status")
    fun findByRoleAndStatus(role: UserRole, status: UserStatus, pageable: Pageable): Page<User>

    @Query("SELECT u FROM User u WHERE u.lockedUntil IS NOT NULL AND u.lockedUntil < :now")
    fun findLockedAccounts(now: OffsetDateTime): List<User>

    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :cutoff AND u.status = :status")
    fun findInactiveUsers(cutoff: OffsetDateTime, status: UserStatus, pageable: Pageable): Page<User>

    fun countByRole(role: UserRole): Long

    fun countByStatus(status: UserStatus): Long

    @Query("SELECT u FROM User u WHERE u.role = :role")
    fun findByRole(role: UserRole, pageable: Pageable): Page<User>

    @Query("SELECT u FROM User u WHERE u.status = :status")
    fun findByStatus(status: UserStatus, pageable: Pageable): Page<User>

    // Override to return Page<User> instead of List<User>
    override fun findAll(pageable: Pageable): Page<User>
}