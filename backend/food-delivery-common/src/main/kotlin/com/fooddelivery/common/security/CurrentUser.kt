package com.fooddelivery.common.security

import java.util.UUID
import com.fooddelivery.common.enums.UserRole

data class CurrentUser(
    val id: UUID,
    val email: String,
    val role: UserRole
)