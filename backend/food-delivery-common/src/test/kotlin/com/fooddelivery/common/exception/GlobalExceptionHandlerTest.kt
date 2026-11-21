package com.fooddelivery.common.exception

import com.fooddelivery.common.TestApplication
import com.fooddelivery.common.dto.ApiResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest(classes = [TestApplication::class, GlobalExceptionHandlerTest.TestConfig::class])
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Configuration
    @Import(TestController::class, TestSecurityConfig::class)
    class TestConfig

    @EnableWebSecurity
    class TestSecurityConfig {
        @Bean
        fun filterChain(http: HttpSecurity): SecurityFilterChain {
            return http
                .csrf { it.disable() }
                .authorizeHttpRequests { it.anyRequest().permitAll() }
                .build()
        }
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should handle BusinessException`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/test/business-exception")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.error.code").value("TEST_ERROR"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.error.message").value("Test error"))
    }

    @Test
    fun `should handle ResourceNotFoundException`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/test/not-found")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("$.error.code").value("NOT_FOUND"))
    }
}