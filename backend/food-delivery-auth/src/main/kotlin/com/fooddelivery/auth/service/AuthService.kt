package com.fooddelivery.auth.service

import com.fooddelivery.auth.config.JwtProperties
import com.fooddelivery.common.enums.UserRole
import com.fooddelivery.common.enums.UserStatus
import com.fooddelivery.common.exception.BusinessException
import com.fooddelivery.common.exception.UnauthorizedException
import com.fooddelivery.users.domain.User
import io.jsonwebtoken.JwtException
import org.springframework.security.authentication.BadCredentialsException
import com.fooddelivery.users.service.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class AuthService(
    private val userService: UserService,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProperties: JwtProperties
) {

    data class AuthResult(
        val user: User,
        val accessToken: String,
        val refreshToken: String
    )

    fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String?,
        role: UserRole
    ): AuthResult {
        val user = userService.register(email, password, firstName, lastName, phone, role)
        userService.createProfile(user.id, role)
        val accessToken = jwtService.generateAccessToken(user.id, user.email, user.role)
        val refreshToken = jwtService.generateRefreshToken(user.id)
        return AuthResult(user, accessToken, refreshToken)
    }

    fun login(email: String, password: String, rememberMe: Boolean = false): AuthResult {
        val user = userService.findByEmail(email.toLowerCase())
            ?: throw BadCredentialsException("Invalid email or password")

        if (!passwordEncoder.matches(password, user.passwordHash)) {
            userService.recordFailedLogin(email)
            throw BadCredentialsException("Invalid email or password")
        }

        if (user.status != UserStatus.ACTIVE) {
            throw BusinessException("Account is not active. Status: ${user.status}", "ACCOUNT_INACTIVE")
        }

        if (user.isAccountLocked()) {
            throw BusinessException("Account is temporarily locked due to failed attempts", "ACCOUNT_LOCKED")
        }

        val accessToken = jwtService.generateAccessToken(user.id, user.email, user.role)
        val refreshToken = jwtService.generateRefreshToken(user.id, rememberMe)
        userService.recordLogin(user.id)

        return AuthResult(user, accessToken, refreshToken)
    }

    fun refreshToken(refreshToken: String): Pair<String, String> {
        try {
            if (!jwtService.isRefreshToken(refreshToken)) {
                throw UnauthorizedException("Invalid token type")
            }

            val userId = jwtService.extractUserId(refreshToken)
            val user = userService.findById(userId)

            if (user.status != UserStatus.ACTIVE) {
                throw BusinessException("Account is not active", "ACCOUNT_INACTIVE")
            }

            val newAccessToken = jwtService.generateAccessToken(user.id, user.email, user.role)
            val newRefreshToken = jwtService.generateRefreshToken(user.id)
            return newAccessToken to newRefreshToken
        } catch (ex: JwtException) {
            throw UnauthorizedException("Invalid refresh token")
        }
    }

    fun logout(refreshToken: String) {
        try {
            // In a production system, you would add the refresh token to a blacklist
            // For now, we just validate it's a valid refresh token
            if (!jwtService.isRefreshToken(refreshToken)) {
                throw UnauthorizedException("Invalid token type")
            }
            // Token blacklisting would be implemented here with Redis
        } catch (ex: JwtException) {
            throw UnauthorizedException("Invalid refresh token")
        }
    }

    fun verifyEmail(token: String): User {
        return userService.verifyEmail(token)
    }

    fun requestPasswordReset(email: String) {
        userService.requestPasswordReset(email.toLowerCase())
    }

    fun resetPassword(token: String, newPassword: String): User {
        return userService.resetPassword(token, newPassword)
    }

    fun changePassword(userId: UUID, currentPassword: String, newPassword: String): User {
        return userService.changePassword(userId, currentPassword, newPassword)
    }

    fun getCurrentUser(userId: UUID): User {
        return userService.findById(userId)
    }
}