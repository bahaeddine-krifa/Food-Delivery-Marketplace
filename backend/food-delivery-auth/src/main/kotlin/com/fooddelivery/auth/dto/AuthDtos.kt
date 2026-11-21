package com.fooddelivery.auth.dto

import com.fooddelivery.common.enums.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @Email @NotBlank val email: String,
    @Size(min = 8, max = 100) @NotBlank val password: String,
    @NotBlank @Size(max = 100) val firstName: String,
    @NotBlank @Size(max = 100) val lastName: String,
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$") val phone: String? = null,
    val role: UserRole = UserRole.CUSTOMER
)

data class LoginRequest(
    @Email @NotBlank val email: String,
    @NotBlank val password: String,
    val rememberMe: Boolean = false
)

data class RefreshRequest(
    @NotBlank val refreshToken: String
)

data class LogoutRequest(
    @NotBlank val refreshToken: String
)

data class ForgotPasswordRequest(
    @Email @NotBlank val email: String
)

data class ResetPasswordRequest(
    @NotBlank val token: String,
    @Size(min = 8, max = 100) @NotBlank val newPassword: String
)

data class ChangePasswordRequest(
    @NotBlank val currentPassword: String,
    @Size(min = 8, max = 100) @NotBlank val newPassword: String
)

data class VerifyEmailRequest(
    @NotBlank val token: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val user: UserProfileResponse
)

data class UserProfileResponse(
    val id: java.util.UUID,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String?,
    val avatarUrl: String?,
    val role: UserRole,
    val status: com.fooddelivery.common.enums.UserStatus,
    val emailVerified: Boolean,
    val createdAt: java.time.OffsetDateTime
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long
)