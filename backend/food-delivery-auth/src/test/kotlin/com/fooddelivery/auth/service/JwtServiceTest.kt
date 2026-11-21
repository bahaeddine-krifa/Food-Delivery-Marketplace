package com.fooddelivery.auth.service

import com.fooddelivery.auth.config.JwtProperties
import com.fooddelivery.common.enums.UserRole
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Duration
import java.util.UUID

class JwtServiceTest {

    private lateinit var jwtProperties: JwtProperties
    private lateinit var jwtService: JwtService

    @BeforeEach
    fun setup() {
        jwtProperties = JwtProperties().apply {
            secret = "test-secret-key-for-testing-purposes-only-must-be-32-chars-minimum"
            accessTokenExpiration = Duration.ofMinutes(15)
            refreshTokenExpiration = Duration.ofDays(7)
            rememberMeExpiration = Duration.ofDays(30)
            issuer = "food-delivery-platform"
            audience = "food-delivery-clients"
        }
        jwtService = JwtService(jwtProperties)
    }

    @Test
    fun `should generate and validate access token`() {
        val userId = UUID.randomUUID()
        val email = "test@example.com"
        val role = UserRole.CUSTOMER

        val token = jwtService.generateAccessToken(userId, email, role)

        assert(token.isNotBlank())

        val claims = jwtService.validateToken(token)
        assert(claims.subject == userId.toString())
        assert(claims.get("email", String::class.java) == email)
        assert(claims.get("role", String::class.java) == role.value)
        assert(claims.get("type", String::class.java) == "access")
    }

    @Test
    fun `should generate and validate refresh token`() {
        val userId = UUID.randomUUID()

        val token = jwtService.generateRefreshToken(userId, false)

        assert(token.isNotBlank())

        val claims = jwtService.validateToken(token)
        assert(claims.subject == userId.toString())
        assert(claims.get("type", String::class.java) == "refresh")
    }

    @Test
    fun `should generate remember me refresh token with longer expiration`() {
        val userId = UUID.randomUUID()

        val normalToken = jwtService.generateRefreshToken(userId, false)
        val rememberToken = jwtService.generateRefreshToken(userId, true)

        assert(normalToken != rememberToken)

        val normalClaims = jwtService.validateToken(normalToken)
        val rememberClaims = jwtService.validateToken(rememberToken)

        assert(rememberClaims.expiration.after(normalClaims.expiration))
    }

    @Test
    fun `should extract user id from token`() {
        val userId = UUID.randomUUID()
        val token = jwtService.generateAccessToken(userId, "test@example.com", UserRole.CUSTOMER)

        val extractedId = jwtService.extractUserId(token)

        assert(extractedId == userId)
    }

    @Test
    fun `should extract email from token`() {
        val email = "test@example.com"
        val token = jwtService.generateAccessToken(UUID.randomUUID(), email, UserRole.CUSTOMER)

        val extractedEmail = jwtService.extractEmail(token)

        assert(extractedEmail == email)
    }

    @Test
    fun `should extract role from token`() {
        val role = UserRole.ADMIN
        val token = jwtService.generateAccessToken(UUID.randomUUID(), "test@example.com", role)

        val extractedRole = jwtService.extractRole(token)

        assert(extractedRole == role)
    }

    @Test
    fun `should identify access token`() {
        val accessToken = jwtService.generateAccessToken(UUID.randomUUID(), "test@example.com", UserRole.CUSTOMER)
        val refreshToken = jwtService.generateRefreshToken(UUID.randomUUID())

        assert(jwtService.isAccessToken(accessToken))
        assert(!jwtService.isAccessToken(refreshToken))
    }

    @Test
    fun `should identify refresh token`() {
        val accessToken = jwtService.generateAccessToken(UUID.randomUUID(), "test@example.com", UserRole.CUSTOMER)
        val refreshToken = jwtService.generateRefreshToken(UUID.randomUUID())

        assert(jwtService.isRefreshToken(refreshToken))
        assert(!jwtService.isRefreshToken(accessToken))
    }

    @Test
    fun `should throw exception for invalid token`() {
        assertThrows<io.jsonwebtoken.JwtException> {
            jwtService.validateToken("invalid-token")
        }
    }

    @Test
    fun `should throw exception for expired token`() {
        // Create a token with very short expiration
        val shortLivedProperties = JwtProperties().apply {
            secret = "test-secret-key-for-testing-purposes-only-must-be-32-chars-minimum"
            accessTokenExpiration = Duration.ofMillis(1)
            issuer = "food-delivery-platform"
            audience = "food-delivery-clients"
        }
        val shortLivedService = JwtService(shortLivedProperties)

        val token = shortLivedService.generateAccessToken(UUID.randomUUID(), "test@example.com", UserRole.CUSTOMER)

        Thread.sleep(10) // Wait for expiration

        assertThrows<io.jsonwebtoken.JwtException> {
            shortLivedService.validateToken(token)
        }
    }

    @Test
    fun `should detect expired token`() {
        val shortLivedProperties = JwtProperties().apply {
            secret = "test-secret-key-for-testing-purposes-only-must-be-32-chars-minimum"
            accessTokenExpiration = Duration.ofMillis(1)
            issuer = "food-delivery-platform"
            audience = "food-delivery-clients"
        }
        val shortLivedService = JwtService(shortLivedProperties)

        val token = shortLivedService.generateAccessToken(UUID.randomUUID(), "test@example.com", UserRole.CUSTOMER)

        Thread.sleep(10)

        assert(shortLivedService.isTokenExpired(token))
    }

    @Test
    fun `should get expiration from token`() {
        val userId = UUID.randomUUID()
        val token = jwtService.generateAccessToken(userId, "test@example.com", UserRole.CUSTOMER)

        val expiration = jwtService.getExpiration(token)

        assert(expiration.isAfter(java.time.Instant.now()))
    }
}