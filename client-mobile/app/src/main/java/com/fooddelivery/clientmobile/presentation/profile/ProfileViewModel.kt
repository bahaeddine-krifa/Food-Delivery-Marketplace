package com.fooddelivery.clientmobile.presentation.profile

import androidx.hilt.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.fooddelivery.clientmobile.data.repository.AuthRepository
import com.fooddelivery.clientmobile.data.repository.AddressRepository
import com.fooddelivery.clientmobile.domain.model.Address
import com.fooddelivery.clientmobile.domain.model.User
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@ActivityRetainedScoped
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val addressRepository: AddressRepository
) {

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses = _addresses.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isChangingPassword = MutableStateFlow(false)
    val isChangingPassword = _isChangingPassword.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _passwordChangeResult = MutableStateFlow<Result<Unit>?>(null)
    val passwordChangeResult = _passwordChangeResult.asStateFlow()

    fun loadProfile() {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            val userResult = authRepository.getCurrentUser()
            when (userResult) {
                is Result.Success -> {
                    val userProfile = userResult.getOrNull()
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
                    }
                    loadAddresses()
                }
                is Result.Failure -> {
                    _error.value = userResult.exceptionOrNull()?.message ?: "Failed to load profile"
                    _isLoading.value = false
                }
            }
        }
    }

    private fun loadAddresses() {
        viewModelScope.launch {
            val result = addressRepository.getAddresses(page = 0, size = 20)
            when (result) {
                is Result.Success -> {
                    val pageResponse = result.getOrNull()
                    pageResponse?.let { response ->
                        _addresses.value = response.content.map { mapToDomain(it) }
                    }
                }
                is Result.Failure -> {
                    // Addresses are optional
                }
            }
            _isLoading.value = false
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        if (currentPassword.isBlank() || newPassword.isBlank()) {
            _error.value = "Please fill in all fields"
            return
        }
        if (newPassword.length < 8) {
            _error.value = "New password must be at least 8 characters"
            return
        }

        _isChangingPassword.value = true
        _error.value = null
        _passwordChangeResult.value = null

        viewModelScope.launch {
            val result = authRepository.changePassword(
                com.fooddelivery.clientmobile.data.api.ApiModels.ChangePasswordRequest(
                    currentPassword = currentPassword,
                    newPassword = newPassword
                )
            )
            _isChangingPassword.value = false
            _passwordChangeResult.value = result

            when (result) {
                is Result.Success -> {
                    // Password changed successfully
                }
                is Result.Failure -> {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to change password"
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _user.value = null
            _addresses.value = emptyList()
        }
    }

    fun clearPasswordChangeResult() {
        _passwordChangeResult.value = null
    }

    fun clearError() {
        _error.value = null
    }

    private fun mapToDomain(apiAddress: com.fooddelivery.clientmobile.data.api.ApiModels.AddressResponse): Address {
        return Address(
            id = java.util.UUID.fromString(apiAddress.id),
            label = apiAddress.label,
            street = apiAddress.street,
            building = apiAddress.building,
            floor = apiAddress.floor,
            apartment = apiAddress.apartment,
            city = apiAddress.city,
            state = apiAddress.state,
            postalCode = apiAddress.postalCode,
            country = apiAddress.country,
            latitude = apiAddress.latitude,
            longitude = apiAddress.longitude,
            instructions = apiAddress.instructions,
            isDefault = apiAddress.isDefault,
            addressType = com.fooddelivery.clientmobile.domain.model.AddressType.valueOf(apiAddress.addressType.value),
            fullAddress = apiAddress.fullAddress,
            createdAt = java.time.OffsetDateTime.parse(apiAddress.createdAt),
            updatedAt = java.time.OffsetDateTime.parse(apiAddress.updatedAt)
        )
    }
}