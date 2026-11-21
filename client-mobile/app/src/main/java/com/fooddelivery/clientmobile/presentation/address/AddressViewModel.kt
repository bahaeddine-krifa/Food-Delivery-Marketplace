package com.fooddelivery.clientmobile.presentation.address

import androidx.hilt.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.fooddelivery.clientmobile.data.repository.AddressRepository
import com.fooddelivery.clientmobile.domain.model.Address
import com.fooddelivery.clientmobile.domain.model.CreateAddressRequest
import com.fooddelivery.clientmobile.domain.model.UpdateAddressRequest
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@ActivityRetainedScoped
class AddressViewModel @Inject constructor(
    private val addressRepository: AddressRepository
) {

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses = _addresses.asStateFlow()

    private val _defaultAddress = MutableStateFlow<Address?>(null)
    val defaultAddress = _defaultAddress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _saveResult = MutableStateFlow<Result<Address>?>(null)
    val saveResult = _saveResult.asStateFlow()

    private val _deleteResult = MutableStateFlow<Result<Unit>?>(null)
    val deleteResult = _deleteResult.asStateFlow()

    fun loadAddresses() {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            val addressesResult = addressRepository.getAddresses(page = 0, size = 50)
            val defaultResult = addressRepository.getDefaultAddress()

            when (addressesResult) {
                is Result.Success -> {
                    val pageResponse = addressesResult.getOrNull()
                    pageResponse?.let { response ->
                        _addresses.value = response.content.map { mapToDomain(it) }
                    }
                }
                is Result.Failure -> {
                    _error.value = addressesResult.exceptionOrNull()?.message ?: "Failed to load addresses"
                }
            }

            when (defaultResult) {
                is Result.Success -> {
                    val address = defaultResult.getOrNull()
                    address?.let { _defaultAddress.value = mapToDomain(it) }
                }
                is Result.Failure -> {
                    // Default address is optional
                }
            }

            _isLoading.value = false
        }
    }

    fun createAddress(request: CreateAddressRequest) {
        _isSaving.value = true
        _error.value = null
        _saveResult.value = null

        viewModelScope.launch {
            val result = addressRepository.createAddress(
                com.fooddelivery.clientmobile.data.api.ApiModels.CreateAddressRequest(
                    label = request.label,
                    street = request.street,
                    building = request.building,
                    floor = request.floor,
                    apartment = request.apartment,
                    city = request.city,
                    state = request.state,
                    postalCode = request.postalCode,
                    country = request.country,
                    latitude = request.latitude,
                    longitude = request.longitude,
                    instructions = request.instructions,
                    isDefault = request.isDefault,
                    addressType = com.fooddelivery.clientmobile.data.api.ApiModels.AddressType.valueOf(request.addressType.name)
                )
            )
            _isSaving.value = false
            _saveResult.value = result

            when (result) {
                is Result.Success -> {
                    val newAddress = result.getOrNull()
                    newAddress?.let { address ->
                        _addresses.value = _addresses.value + mapToDomain(address)
                        if (request.isDefault) {
                            _defaultAddress.value = mapToDomain(address)
                        }
                    }
                }
                is Result.Failure -> {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to create address"
                }
            }
        }
    }

    fun updateAddress(addressId: java.util.UUID, request: UpdateAddressRequest) {
        _isSaving.value = true
        _error.value = null
        _saveResult.value = null

        viewModelScope.launch {
            val result = addressRepository.updateAddress(
                addressId.toString(),
                com.fooddelivery.clientmobile.data.api.ApiModels.UpdateAddressRequest(
                    label = request.label,
                    street = request.street,
                    building = request.building,
                    floor = request.floor,
                    apartment = request.apartment,
                    city = request.city,
                    state = request.state,
                    postalCode = request.postalCode,
                    country = request.country,
                    latitude = request.latitude,
                    longitude = request.longitude,
                    instructions = request.instructions,
                    isDefault = request.isDefault,
                    addressType = request.addressType?.let { com.fooddelivery.clientmobile.data.api.ApiModels.AddressType.valueOf(it.name) }
                )
            )
            _isSaving.value = false
            _saveResult.value = result

            when (result) {
                is Result.Success -> {
                    val updatedAddress = result.getOrNull()
                    updatedAddress?.let { address ->
                        val domainAddress = mapToDomain(address)
                        _addresses.value = _addresses.value.map { if (it.id == addressId) domainAddress else it }
                        if (request.isDefault == true) {
                            _defaultAddress.value = domainAddress
                        }
                    }
                }
                is Result.Failure -> {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to update address"
                }
            }
        }
    }

    fun setDefaultAddress(addressId: java.util.UUID) {
        _isSaving.value = true
        _error.value = null

        viewModelScope.launch {
            val result = addressRepository.setDefaultAddress(addressId.toString())
            _isSaving.value = false

            when (result) {
                is Result.Success -> {
                    val updatedAddress = result.getOrNull()
                    updatedAddress?.let { address ->
                        val domainAddress = mapToDomain(address)
                        _addresses.value = _addresses.value.map { if (it.id == addressId) domainAddress else it }
                        _defaultAddress.value = domainAddress
                    }
                }
                is Result.Failure -> {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to set default address"
                }
            }
        }
    }

    fun deleteAddress(addressId: java.util.UUID) {
        _isSaving.value = true
        _error.value = null
        _deleteResult.value = null

        viewModelScope.launch {
            val result = addressRepository.deleteAddress(addressId.toString())
            _isSaving.value = false
            _deleteResult.value = result

            when (result) {
                is Result.Success -> {
                    _addresses.value = _addresses.value.filter { it.id != addressId }
                    if (_defaultAddress.value?.id == addressId) {
                        _defaultAddress.value = _addresses.value.firstOrNull { it.isDefault }
                            ?: _addresses.value.firstOrNull()
                    }
                }
                is Result.Failure -> {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to delete address"
                }
            }
        }
    }

    fun clearSaveResult() {
        _saveResult.value = null
    }

    fun clearDeleteResult() {
        _deleteResult.value = null
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