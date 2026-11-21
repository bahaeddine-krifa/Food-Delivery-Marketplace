package com.fooddelivery.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.time.Duration

@Component
@ConfigurationProperties(prefix = "app.jwt")
class JwtProperties {
    var secret: String = "your-256-bit-secret-key-here-must-be-at-least-32-characters-long"
    var accessTokenExpiration: Duration = Duration.ofMinutes(15)
    var refreshTokenExpiration: Duration = Duration.ofDays(7)
    var rememberMeExpiration: Duration = Duration.ofDays(30)
    var issuer: String = "food-delivery-platform"
    var audience: String = "food-delivery-clients"
}