package com.fooddelivery.deliverymobile.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fooddelivery.deliverymobile.data.api.LoginRequest
import com.fooddelivery.deliverymobile.data.repository.AuthRepository
import com.fooddelivery.deliverymobile.data.repository.Result
import com.fooddelivery.deliverymobile.domain.model.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthState.initial)
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String, rememberMe: Boolean = false) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = authRepository.login(LoginRequest(email, password, rememberMe))
            handleAuthResult(result)
        }
    }

    fun checkAuthStatus() {
        viewModelScope.launch {
            val isAuthenticated = authRepository.isAuthenticated()
            if (isAuthenticated) {
                val profileResult = authRepository.getProfile()
                when (profileResult) {
                    is Result.Success -> {
                        val userProfile = profileResult.getOrThrow()
                        _uiState.update { AuthState(
                            user = mapUser(userProfile),
                            accessToken = authRepository.getAccessToken(),
                            isAuthenticated = true,
                            isLoading = false,
                            error = null
                        )}
                    }
                    is Result.Failure -> {
                        authRepository.logout()
                        _uiState.update { AuthState.initial }
                    }
                }
            } else {
                _uiState.update { AuthState.initial }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { AuthState.initial }
        }
    }

    private fun handleAuthResult(result: Result<com.fooddelivery.deliverymobile.data.api.AuthResponse>) {
        when (result) {
            is Result.Success -> {
                val authResponse = result.getOrThrow()
                _uiState.update { AuthState(
                    user = mapUser(authResponse.user),
                    accessToken = authResponse.accessToken,
                    isAuthenticated = true,
                    isLoading = false,
                    error = null
                )}
            }
            is Result.Failure -> {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Login failed"
                )}
            }
        }
    }

    private fun mapUser(apiUser: com.fooddelivery.deliverymobile.data.api.UserProfileResponse): com.fooddelivery.deliverymobile.domain.model.User {
        return com.fooddelivery.deliverymobile.domain.model.User(
            id = apiUser.id,
            email = apiUser.email,
            firstName = apiUser.firstName,
            lastName = apiUser.lastName,
            phone = apiUser.phone,
            avatarUrl = apiUser.avatarUrl,
            role = mapRole(apiUser.role),
            status = mapStatus(apiUser.status),
            emailVerified = apiUser.emailVerified,
            createdAt = apiUser.createdAt
        )
    }

    private fun mapRole(apiRole: com.fooddelivery.deliverymobile.data.api.UserRole): com.fooddelivery.deliverymobile.domain.model.UserRole {
        return when (apiRole) {
            com.fooddelivery.deliverymobile.data.api.UserRole.CUSTOMER -> com.fooddelivery.deliverymobile.domain.model.UserRole.CUSTOMER
            com.fooddelivery.deliverymobile.data.api.UserRole.DELIVERY_PERSON -> com.fooddelivery.deliverymobile.domain.model.UserRole.DELIVERY_PERSON
            com.fooddelivery.deliverymobile.data.api.UserRole.RESTAURANT_OWNER -> com.fooddelivery.deliverymobile.domain.model.UserRole.RESTAURANT_OWNER
            com.fooddelivery.deliverymobile.data.api.UserRole.ADMIN -> com.fooddelivery.deliverymobile.domain.model.UserRole.ADMIN
        }
    }

    private fun mapStatus(apiStatus: com.fooddelivery.deliverymobile.data.api.UserStatus): com.fooddelivery.deliverymobile.domain.model.UserStatus {
        return when (apiStatus) {
            com.fooddelivery.deliverymobile.data.api.UserStatus.ACTIVE -> com.fooddelivery.deliverymobile.domain.model.UserStatus.ACTIVE
            com.fooddelivery.deliverymobile.data.api.UserStatus.INACTIVE -> com.fooddelivery.deliverymobile.domain.model.UserStatus.INACTIVE
            com.fooddelivery.deliverymobile.data.api.UserStatus.SUSPENDED -> com.fooddelivery.deliverymobile.domain.model.UserStatus.SUSPENDED
            com.fooddelivery.deliverymobile.data.api.UserStatus.PENDING_VERIFICATION -> com.fooddelivery.deliverymobile.domain.model.UserStatus.PENDING_VERIFICATION
            com.fooddelivery.deliverymobile.data.api.UserStatus.DELETED -> com.fooddelivery.deliverymobile.domain.model.UserStatus.DELETED
        }
    }
}