package com.fooddelivery.auth.service

import com.fooddelivery.auth.config.JwtProperties
import com.fooddelivery.common.enums.UserRole
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import javax.crypto.SecretKey
import java.time.Instant
import java.util.Date
import java.util.UUID

@Service
class JwtService(private val properties: JwtProperties) {

    private val signingKey: SecretKey = Keys.hmacShaKeyFor(properties.secret.toByteArray())
    val jwtProperties: JwtProperties = properties

    fun generateAccessToken(
        userId: UUID,
        email: String,
        role: UserRole
    ): String {
        val now = Instant.now()
        val expiry = now.plus(properties.accessTokenExpiration)
        return Jwts.builder()
            .subject(userId.toString())
            .issuer(properties.issuer)
            .audience().add(properties.audience).and()
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .claim("email", email)
            .claim("role", role.name)
            .claim("type", "access")
            .signWith(signingKey)
            .compact()
    }

    fun generateRefreshToken(
        userId: UUID,
        rememberMe: Boolean = false
    ): String {
        val now = Instant.now()
        val expiration = if (rememberMe) properties.rememberMeExpiration else properties.refreshTokenExpiration
        val expiry = now.plus(expiration)
        return Jwts.builder()
            .subject(userId.toString())
            .issuer(properties.issuer)
            .audience().add(properties.audience).and()
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .claim("type", "refresh")
            .signWith(signingKey)
            .compact()
    }

    fun validateToken(token: String): Claims {
        try {
            return Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(properties.issuer)
                .requireAudience(properties.audience)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (ex: JwtException) {
            throw JwtException("Invalid token: ${ex.message}")
        }
    }

    fun extractUserId(token: String): UUID {
        val claims = validateToken(token)
        return UUID.fromString(claims.subject)
    }

    fun extractEmail(token: String): String {
        val claims = validateToken(token)
        return claims.get("email", String::class.java)
    }

    fun extractRole(token: String): UserRole {
        val claims = validateToken(token)
        val roleValue = claims.get("role", String::class.java)
        return UserRole.fromValue(roleValue)
    }

    fun extractUserRole(token: String): UserRole = extractRole(token)

    fun isAccessToken(token: String): Boolean {
        val claims = validateToken(token)
        return claims.get("type", String::class.java) == "access"
    }

    fun isRefreshToken(token: String): Boolean {
        val claims = validateToken(token)
        return claims.get("type", String::class.java) == "refresh"
    }

    fun getExpiration(token: String): Instant {
        val claims = validateToken(token)
        return claims.expiration.toInstant()
    }

    fun isTokenExpired(token: String): Boolean {
        try {
            val claims = validateToken(token)
            return claims.expiration.before(Date.from(Instant.now()))
        } catch (ex: JwtException) {
            return true
        }
    }
}