package com.fooddelivery.auth.service

import com.fooddelivery.auth.config.JwtProperties
import com.fooddelivery.common.enums.UserRole
import com.fooddelivery.common.enums.UserStatus
import com.fooddelivery.common.exception.BusinessException
import com.fooddelivery.common.exception.UnauthorizedException
import com.fooddelivery.users.domain.User
import com.fooddelivery.users.service.UserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.justRun
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Duration
import java.time.OffsetDateTime
import java.util.UUID

class AuthServiceTest {

    private lateinit var userService: UserService
    private lateinit var jwtService: JwtService
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var jwtProperties: JwtProperties
    private lateinit var authService: AuthService

    @BeforeEach
    fun setup() {
        userService = mockk()
        jwtService = mockk()
        passwordEncoder = BCryptPasswordEncoder(4) // Low cost for tests
        jwtProperties = JwtProperties().apply {
            secret = "test-secret-key-for-testing-purposes-only-must-be-32-chars-minimum"
            accessTokenExpiration = Duration.ofMinutes(15)
            refreshTokenExpiration = Duration.ofDays(7)
        }
        authService = AuthService(userService, jwtService, passwordEncoder, jwtProperties)
    }

    @Test
    fun `should register new user`() {
        val user = createTestUser()
        every { userService.register(any(), any(), any(), any(), any(), any()) } returns user
        every { userService.createProfile(any(), any()) } returns Unit
        every { jwtService.generateAccessToken(any(), any(), any()) } returns "mock-access-token"
        every { jwtService.generateRefreshToken(any()) } returns "mock-refresh-token"

        val result = authService.register("test@example.com", "Password123!", "John", "Doe", null, UserRole.CUSTOMER)

        assert(result.user.email == "test@example.com")
        assert(result.accessToken == "mock-access-token")
        assert(result.refreshToken == "mock-refresh-token")
        verify { userService.createProfile(user.id, UserRole.CUSTOMER) }
    }

    @Test
    fun `should login successfully`() {
        val user = createTestUser().apply {
            passwordHash = passwordEncoder.encode("Password123!")
            status = UserStatus.ACTIVE
        }
        every { userService.findByEmail("test@example.com") } returns user
        every { userService.recordLogin(user.id) } returns user
        every { jwtService.generateAccessToken(any(), any(), any()) } returns "mock-access-token"
        every { jwtService.generateRefreshToken(any(), any()) } returns "mock-refresh-token"

        val result = authService.login("test@example.com", "Password123!", false)

        assert(result.user.id == user.id)
        assert(result.accessToken == "mock-access-token")
        assert(result.refreshToken == "mock-refresh-token")
    }

    @Test
    fun `should throw exception for invalid password`() {
        val user = createTestUser().apply {
            passwordHash = passwordEncoder.encode("Password123!")
            status = UserStatus.ACTIVE
        }
        every { userService.findByEmail("test@example.com") } returns user
        every { userService.recordFailedLogin(any()) } returns user

        assertThrows<BadCredentialsException> {
            authService.login("test@example.com", "WrongPassword!", false)
        }
    }

    @Test
    fun `should throw exception for inactive user`() {
        val user = createTestUser().apply {
            passwordHash = passwordEncoder.encode("Password123!")
            status = UserStatus.INACTIVE
        }
        every { userService.findByEmail("test@example.com") } returns user

        assertThrows<BusinessException> {
            authService.login("test@example.com", "Password123!", false)
        }
    }

    @Test
    fun `should refresh tokens`() {
        val user = createTestUser().apply { status = UserStatus.ACTIVE }
        val refreshToken = "valid-refresh-token"
        every { jwtService.isRefreshToken(refreshToken) } returns true
        every { jwtService.extractUserId(refreshToken) } returns user.id
        every { userService.findById(user.id) } returns user
        every { jwtService.generateAccessToken(any(), any(), any()) } returns "mock-new-access-token"
        every { jwtService.generateRefreshToken(any()) } returns "mock-new-refresh-token"

        val (newAccessToken, newRefreshToken) = authService.refreshToken(refreshToken)

        assert(newAccessToken == "mock-new-access-token")
        assert(newRefreshToken == "mock-new-refresh-token")
    }

    private fun createTestUser(): User {
        return User().apply {
            id = UUID.randomUUID()
            email = "test@example.com"
            passwordHash = passwordEncoder.encode("Password123!")
            firstName = "John"
            lastName = "Doe"
            role = UserRole.CUSTOMER
            status = UserStatus.PENDING_VERIFICATION
            emailVerified = false
            createdAt = OffsetDateTime.now()
        }
    }
}