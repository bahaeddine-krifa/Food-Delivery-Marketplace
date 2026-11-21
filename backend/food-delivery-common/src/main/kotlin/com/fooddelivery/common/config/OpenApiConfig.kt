package com.fooddelivery.common.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(Info()
                .title("Food Delivery Platform API")
                .version("1.0.0")
                .description("""
                    ## Food Delivery Platform REST API

                    A comprehensive food delivery marketplace platform with four applications:
                    - **Customer Mobile App** - Browse restaurants, place orders, track deliveries
                    - **Delivery Mobile App** - Manage deliveries, navigation, earnings
                    - **Restaurant Web Portal** - Manage menu, orders, analytics
                    - **Admin Dashboard** - Platform management, analytics, user management

                    ### Authentication
                    All endpoints (except public ones) require JWT Bearer token authentication.
                    Use `/api/v1/auth/login` to obtain access and refresh tokens.

                    ### Rate Limiting
                    - Auth endpoints: 10 requests/minute
                    - API endpoints: 100 requests/minute

                    ### Pagination
                    Default page size: 20, Max page size: 100
                    Use `page` (0-based), `size`, `sort` parameters.

                    ### Error Format
                    All errors follow RFC 7807 Problem Details format.
                """.trimIndent())
                .contact(Contact()
                    .name("Food Delivery Platform Team")
                    .email("dev@fooddelivery.example.com")
                    .url("https://github.com/fooddelivery/platform"))
                .license(License()
                    .name("Proprietary")
                    .url("https://fooddelivery.example.com/license"))
            )
            .servers(listOf(
                Server().url("http://localhost:8080").description("Development Server"),
                Server().url("https://api.fooddelivery.example.com").description("Production Server")
            ))
            .addSecurityItem(SecurityRequirement().addList("bearerAuth"))
            .components(Components()
                .addSecuritySchemes("bearerAuth", SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT Authorization header using the Bearer scheme. Example: \"Authorization: Bearer {token}\""))
            )
    }

    @Bean
    fun authApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Authentication")
            .pathsToMatch("/api/v1/auth/**")
            .build()
    }

    @Bean
    fun userApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Users")
            .pathsToMatch("/api/v1/users/**")
            .build()
    }

    @Bean
    fun publicApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Public")
            .pathsToMatch("/api/v1/restaurants/**", "/api/v1/categories/**", "/api/v1/products/**")
            .build()
    }

    @Bean
    fun adminApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Admin")
            .pathsToMatch("/api/v1/admin/**")
            .build()
    }
}