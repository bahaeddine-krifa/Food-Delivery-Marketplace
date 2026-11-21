package com.fooddelivery.auth.controller

import com.fooddelivery.auth.dto.LoginRequest
import com.fooddelivery.auth.dto.RefreshRequest
import com.fooddelivery.auth.dto.RegisterRequest
import com.fooddelivery.common.FoodDeliveryApplication
import com.fooddelivery.common.enums.UserRole
import com.fooddelivery.common.enums.UserStatus
import com.fooddelivery.users.test.UsersTestApplication
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import com.fooddelivery.users.domain.User
import com.fooddelivery.users.repository.UserRepository
import com.fooddelivery.users.repository.CustomerProfileRepository

@SpringBootTest(classes = [FoodDeliveryApplication::class, UsersTestApplication::class])
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var customerProfileRepository: CustomerProfileRepository

    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        passwordEncoder = BCryptPasswordEncoder(4)
        objectMapper = ObjectMapper().registerModule(com.fasterxml.jackson.module.kotlin.KotlinModule.Builder().build())

        // Clean up
        customerProfileRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun `should register new customer`() {
        val request = RegisterRequest(
            email = "newuser@test.com",
            password = "Password123!",
            firstName = "New",
            lastName = "User",
            phone = "+21699999999",
            role = UserRole.CUSTOMER
        )

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.refreshToken").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.email").value("newuser@test.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.role").value("CUSTOMER"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.status").value("PENDING_VERIFICATION"))
    }

    @Test
    fun `should reject duplicate email registration`() {
        val user = createTestUser("existing@test.com")
        userRepository.save(user)

        val request = RegisterRequest(
            email = "existing@test.com",
            password = "Password123!",
            firstName = "Another",
            lastName = "User",
            role = UserRole.CUSTOMER
        )

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.error.code").value("CONFLICT"))
    }

    @Test
    fun `should login successfully`() {
        val user = createTestUser("login@test.com", UserStatus.ACTIVE)
        userRepository.save(user)

        val request = LoginRequest(
            email = "login@test.com",
            password = "Password123!",
            rememberMe = false
        )

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.refreshToken").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.email").value("login@test.com"))
    }

    @Test
    fun `should reject login with wrong password`() {
        val user = createTestUser("wrongpass@test.com", UserStatus.ACTIVE)
        userRepository.save(user)

        val request = LoginRequest(
            email = "wrongpass@test.com",
            password = "WrongPassword!",
            rememberMe = false
        )

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.error.code").value("INVALID_CREDENTIALS"))
    }

    @Test
    fun `should reject login for inactive user`() {
        val user = createTestUser("inactive@test.com", UserStatus.INACTIVE)
        userRepository.save(user)

        val request = LoginRequest(
            email = "inactive@test.com",
            password = "Password123!",
            rememberMe = false
        )

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.error.code").value("ACCOUNT_INACTIVE"))
    }

    @Test
    fun `should refresh access token`() {
        val user = createTestUser("refresh@test.com", UserStatus.ACTIVE)
        userRepository.save(user)

        // First login to get refresh token
        val loginRequest = LoginRequest(
            email = "refresh@test.com",
            password = "Password123!",
            rememberMe = false
        )

        val loginResponse = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val loginResponseContent = loginResponse.response.contentAsString
        val loginResponseJson = objectMapper.readTree(loginResponseContent)
        val refreshToken = loginResponseJson.get("data").get("refreshToken").asText()

        // Refresh token
        val refreshRequest = RefreshRequest(refreshToken = refreshToken)

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(refreshRequest)))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.refreshToken").exists())
    }

    @Test
    fun `should reject invalid refresh token`() {
        val refreshRequest = RefreshRequest(refreshToken = "invalid-token")

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(refreshRequest)))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
    }

    private fun createTestUser(email: String, status: UserStatus = UserStatus.PENDING_VERIFICATION): User {
        return User().apply {
            this.email = email
            this.passwordHash = passwordEncoder.encode("Password123!")
            this.firstName = "Test"
            this.lastName = "User"
            this.role = UserRole.CUSTOMER
            this.status = status
            this.emailVerified = status == UserStatus.ACTIVE
        }
    }
}