package com.fooddelivery.auth.config

import com.fooddelivery.auth.service.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // Match all API paths, actuator, swagger, error
            .securityMatcher("/api/**", "/actuator/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/error")
            .cors { it.configurationSource(corsConfigurationSource()) }
            // Disable CSRF globally (stateless JWT API)
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    // Auth endpoints - fully public (register, login, refresh, logout)
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    // Public endpoints
                    .requestMatchers("/api/v1/public/**").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                    .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                    .requestMatchers("/error").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/restaurants/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                    // Everything else requires authentication
                    .anyRequest().authenticated()
            }
            // JWT filter for token-based authentication
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling { exceptions ->
                exceptions
                    .authenticationEntryPoint { request, response, authException ->
                        response.status = 401
                        response.contentType = "application/json"
                        response.writer.write("""{"success":false,"error":{"code":"UNAUTHORIZED","message":"Authentication required"},"meta":{"timestamp":"${java.time.OffsetDateTime.now()}","requestId":"${request.getHeader("X-Request-ID") ?: java.util.UUID.randomUUID()}"}}""")
                    }
                    .accessDeniedHandler { request, response, accessDeniedException ->
                        response.status = 403
                        response.contentType = "application/json"
                        response.writer.write("""{"success":false,"error":{"code":"FORBIDDEN","message":"Access denied"},"meta":{"timestamp":"${java.time.OffsetDateTime.now()}","requestId":"${request.getHeader("X-Request-ID") ?: java.util.UUID.randomUUID()}"}}""")
                    }
            }
            // Disable default authentication mechanisms (we use JWT)
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .headers { it.disable() }
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf("http://localhost:3000", "http://localhost:3001","http://localhost:3002","http://localhost:3005", "http://localhost")
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            exposedHeaders = listOf("Authorization", "X-Request-ID")
            allowCredentials = true
            maxAge = 3600L
        }
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder(12)

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager
}