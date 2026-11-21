package com.fooddelivery.auth.service

import com.fooddelivery.common.enums.UserRole
import com.fooddelivery.common.security.CurrentUser
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.lang.NonNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.UUID

@Component
class JwtAuthenticationFilter(private val jwtService: JwtService) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    override fun doFilterInternal(
        @NonNull request: HttpServletRequest,
        @NonNull response: HttpServletResponse,
        @NonNull filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(7).trim()

        try {
            if (!jwtService.isAccessToken(token)) {
                filterChain.doFilter(request, response)
                return
            }

            val userId = jwtService.extractUserId(token)
            val email = jwtService.extractEmail(token)
            val role = jwtService.extractRole(token)

            val authorities = listOf(SimpleGrantedAuthority("ROLE_${role.value}"))

            val authentication = UsernamePasswordAuthenticationToken(
                CurrentUser(userId, email, role),
                null,
                authorities
            )

            SecurityContextHolder.getContext().authentication = authentication

        } catch (ex: Exception) {
            logger.debug("JWT validation failed: ${ex.message}")
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }
}