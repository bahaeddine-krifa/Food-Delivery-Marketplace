package com.fooddelivery.users.domain

import com.fooddelivery.common.domain.AuditableEntity
import com.fooddelivery.common.enums.UserRole
import com.fooddelivery.common.enums.UserStatus
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "users",
    uniqueConstraints = [UniqueConstraint(name = "uk_users_email", columnNames = ["email"])],
    indexes = [
        Index(name = "idx_users_role_status", columnList = "role,status"),
        Index(name = "idx_users_email_verification_token", columnList = "email_verification_token"),
        Index(name = "idx_users_password_reset_token", columnList = "password_reset_token")
    ]
)
class User : AuditableEntity() {

    @Column(nullable = false, unique = true, length = 255)
    var email: String = ""

    @Column(name = "password_hash", nullable = false, length = 255)
    var passwordHash: String = ""

    @Column(name = "first_name", nullable = false, length = 100)
    var firstName: String = ""

    @Column(name = "last_name", nullable = false, length = 100)
    var lastName: String = ""

    @Column(length = 20)
    var phone: String? = null

    @Column(name = "avatar_url", length = 500)
    var avatarUrl: String? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var role: UserRole = UserRole.CUSTOMER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: UserStatus = UserStatus.PENDING_VERIFICATION

    @Column(name = "email_verified", nullable = false)
    var emailVerified: Boolean = false

    @Column(name = "email_verification_token", length = 255)
    var emailVerificationToken: String? = null

    @Column(name = "password_reset_token", length = 255)
    var passwordResetToken: String? = null

    @Column(name = "password_reset_expires")
    var passwordResetExpires: OffsetDateTime? = null

    @Column(name = "failed_login_attempts", nullable = false)
    var failedLoginAttempts: Int = 0

    @Column(name = "locked_until")
    var lockedUntil: OffsetDateTime? = null

    @Column(name = "last_login_at")
    var lastLoginAt: OffsetDateTime? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferences")
    var preferences: String = "{}"

    fun getFullName(): String = "$firstName $lastName"

    fun isAccountLocked(): Boolean = lockedUntil != null && lockedUntil!! > OffsetDateTime.now()

    fun incrementFailedAttempts(): Int {
        failedLoginAttempts++
        if (failedLoginAttempts >= 5) {
            lockedUntil = OffsetDateTime.now().plusMinutes(15)
        }
        return failedLoginAttempts
    }

    fun resetFailedAttempts() {
        failedLoginAttempts = 0
        lockedUntil = null
    }
}