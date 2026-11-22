package com.fooddelivery.deliverymobile.domain.model

import java.time.OffsetDateTime
import java.util.UUID

data class User(
    val id: UUID,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String?,
    val avatarUrl: String?,
    val role: UserRole,
    val status: UserStatus,
    val emailVerified: Boolean,
    val createdAt: OffsetDateTime
) {
    val fullName: String
        get() = "$firstName $lastName".trim()

    val initials: String
        get() = (firstName.take(1) + lastName.take(1)).uppercase()

    val isDeliveryPerson: Boolean
        get() = role == UserRole.DELIVERY_PERSON

    val isActive: Boolean
        get() = status == UserStatus.ACTIVE
}

enum class UserRole {
    CUSTOMER,
    DELIVERY_PERSON,
    RESTAURANT_OWNER,
    ADMIN
}

enum class UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    PENDING_VERIFICATION,
    DELETED
}

data class AuthState(
    val user: User? = null,
    val accessToken: String? = null,
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    companion object {
        val initial = AuthState()
    }
}