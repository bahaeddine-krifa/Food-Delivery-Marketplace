package com.fooddelivery.auth.controller

import com.fooddelivery.auth.dto.AuthResponse
import com.fooddelivery.auth.dto.ChangePasswordRequest
import com.fooddelivery.auth.dto.ForgotPasswordRequest
import com.fooddelivery.auth.dto.LoginRequest
import com.fooddelivery.auth.dto.LogoutRequest
import com.fooddelivery.auth.dto.RefreshRequest
import com.fooddelivery.auth.dto.RegisterRequest
import com.fooddelivery.auth.dto.ResetPasswordRequest
import com.fooddelivery.auth.dto.TokenResponse
import com.fooddelivery.auth.dto.UserProfileResponse
import com.fooddelivery.auth.service.AuthService
import com.fooddelivery.auth.service.JwtService
import com.fooddelivery.common.dto.ApiResponse
import com.fooddelivery.common.enums.UserRole
import com.fooddelivery.users.domain.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
class AuthController(
    private val authService: AuthService,
    private val jwtService: JwtService
) {

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Register a new user account with email verification")
    fun register(@Valid @RequestBody request: RegisterRequest, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val result = authService.register(
            request.email,
            request.password,
            request.firstName,
            request.lastName,
            request.phone,
            request.role
        )
        val response = toAuthResponse(result)
        return ResponseEntity.status(201).body(ApiResponse.created(response, httpRequest.getHeader("X-Request-ID")))
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and return access + refresh tokens")
    fun login(@Valid @RequestBody request: LoginRequest, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val result = authService.login(request.email, request.password, request.rememberMe)
        val response = toAuthResponse(result)
        return ResponseEntity.ok(ApiResponse.ok(response, httpRequest.getHeader("X-Request-ID")))
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Get new access token using refresh token")
    fun refresh(@Valid @RequestBody request: RefreshRequest, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        val (accessToken, refreshToken) = authService.refreshToken(request.refreshToken)
        val expiresIn = jwtService.jwtProperties.accessTokenExpiration.toSeconds()
        val response = TokenResponse(accessToken, refreshToken, "Bearer", expiresIn)
        return ResponseEntity.ok(ApiResponse.ok(response, httpRequest.getHeader("X-Request-ID")))
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Revoke refresh token (blacklist)")
    fun logout(@Valid @RequestBody request: LogoutRequest, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<Unit>> {
        authService.logout(request.refreshToken)
        return ResponseEntity.ok(ApiResponse.ok(Unit, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Verify user email with token from email link")
    fun verifyEmail(@RequestParam token: String, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<Unit>> {
        authService.verifyEmail(token)
        return ResponseEntity.ok(ApiResponse.ok(Unit, httpRequest.getHeader("X-Request-ID")))
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset", description = "Send password reset email")
    fun forgotPassword(@Valid @RequestBody request: ForgotPasswordRequest, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<Unit>> {
        authService.requestPasswordReset(request.email)
        return ResponseEntity.ok(ApiResponse.ok(Unit, httpRequest.getHeader("X-Request-ID")))
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password with token from email")
    fun resetPassword(@Valid @RequestBody request: ResetPasswordRequest, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<Unit>> {
        authService.resetPassword(request.token, request.newPassword)
        return ResponseEntity.ok(ApiResponse.ok(Unit, httpRequest.getHeader("X-Request-ID")))
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change password for authenticated user")
    fun changePassword(
        @Valid @RequestBody request: ChangePasswordRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        val userId = getCurrentUserId(httpRequest)
        authService.changePassword(userId, request.currentPassword, request.newPassword)
        return ResponseEntity.ok(ApiResponse.ok(Unit, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get authenticated user profile")
    @PreAuthorize("isAuthenticated()")
    fun getCurrentUser(httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<UserProfileResponse>> {
        val userId = getCurrentUserId(httpRequest)
        val user = authService.getCurrentUser(userId)
        val response = UserProfileResponse(
            id = user.id,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            phone = user.phone,
            avatarUrl = user.avatarUrl,
            role = user.role,
            status = user.status,
            emailVerified = user.emailVerified,
            createdAt = user.createdAt
        )
        return ResponseEntity.ok(ApiResponse.ok(response, httpRequest.getHeader("X-Request-ID")))
    }

    private fun toAuthResponse(result: AuthService.AuthResult): AuthResponse {
        val expiresIn = jwtService.jwtProperties.accessTokenExpiration.toSeconds()
        return AuthResponse(
            accessToken = result.accessToken,
            refreshToken = result.refreshToken,
            tokenType = "Bearer",
            expiresIn = expiresIn,
            user = UserProfileResponse(
                id = result.user.id,
                email = result.user.email,
                firstName = result.user.firstName,
                lastName = result.user.lastName,
                phone = result.user.phone,
                avatarUrl = result.user.avatarUrl,
                role = result.user.role,
                status = result.user.status,
                emailVerified = result.user.emailVerified,
                createdAt = result.user.createdAt
            )
        )
    }

    private fun getCurrentUserId(httpRequest: HttpServletRequest): java.util.UUID {
        val authHeader = httpRequest.getHeader("Authorization")
            ?: throw IllegalStateException("No authorization header")
        val token = authHeader.removePrefix("Bearer ").trim()
        return jwtService.extractUserId(token)
    }
}