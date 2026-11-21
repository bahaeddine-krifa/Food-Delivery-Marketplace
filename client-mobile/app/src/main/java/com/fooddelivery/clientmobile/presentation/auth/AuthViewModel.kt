package com.fooddelivery.clientmobile.presentation.auth

import androidx.hilt.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.fooddelivery.clientmobile.data.repository.AuthRepository
import com.fooddelivery.clientmobile.domain.model.AuthTokens
import com.fooddelivery.clientmobile.domain.model.User
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@ActivityRetainedScoped
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) {

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _authTokens = MutableStateFlow<AuthTokens?>(null)
    val authTokens = _authTokens.asStateFlow()

    fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String?
    ) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            val result = authRepository.register(
                com.fooddelivery.clientmobile.data.api.ApiModels.RegisterRequest(
                    email = email,
                    password = password,
                    firstName = firstName,
                    lastName = lastName,
                    phone = phone,
                    role = com.fooddelivery.clientmobile.data.api.ApiModels.UserRole.CUSTOMER
                )
            )
            _isLoading.value = false
            when (result) {
                is Result.Success -> {
                    val authResponse = result.getOrNull()
                    authResponse?.let { response ->
                        _authTokens.value = AuthTokens(
                            accessToken = response.accessToken,
                            refreshToken = response.refreshToken,
                            expiresIn = response.expiresIn
                        )
                        loadCurrentUser()
                    }
                }
                is Result.Failure -> {
                    _error.value = result.exceptionOrNull()?.message ?: "Registration failed"
                }
            }
        }
    }

    fun login(email: String, password: String, rememberMe: Boolean = false) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            val result = authRepository.login(
                com.fooddelivery.clientmobile.data.api.ApiModels.LoginRequest(
                    email = email,
                    password = password,
                    rememberMe = rememberMe
                )
            )
            _isLoading.value = false
            when (result) {
                is Result.Success -> {
                    val authResponse = result.getOrNull()
                    authResponse?.let { response ->
                        _authTokens.value = AuthTokens(
                            accessToken = response.accessToken,
                            refreshToken = response.refreshToken,
                            expiresIn = response.expiresIn
                        )
                        loadCurrentUser()
                    }
                }
                is Result.Failure -> {
                    _error.value = result.exceptionOrNull()?.message ?: "Login failed"
                }
            }
        }
    }

    fun logout() {
        _isLoading.value = true
        viewModelScope.launch {
            authRepository.logout()
            _user.value = null
            _isAuthenticated.value = false
            _authTokens.value = null
            _isLoading.value = false
        }
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            val result = authRepository.getCurrentUser()
            when (result) {
                is Result.Success -> {
                    val userProfile = result.getOrNull()
                    userProfile?.let { profile ->
                        _user.value = User(
                            id = java.util.UUID.fromString(profile.id),
                            email = profile.email,
                            firstName = profile.firstName,
                            lastName = profile.lastName,
                            phone = profile.phone,
                            avatarUrl = profile.avatarUrl,
                            role = com.fooddelivery.clientmobile.domain.model.UserRole.valueOf(profile.role.value),
                            status = com.fooddelivery.clientmobile.domain.model.UserStatus.valueOf(profile.status.value),
                            emailVerified = profile.emailVerified,
                            createdAt = java.time.OffsetDateTime.parse(profile.createdAt)
                        )
                        _isAuthenticated.value = true
                    }
                }
                is Result.Failure -> {
                    _isAuthenticated.value = false
                    _user.value = null
                }
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}