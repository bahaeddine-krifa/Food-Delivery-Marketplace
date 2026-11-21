package com.fooddelivery.clientmobile.presentation.checkout

import androidx.hilt.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.fooddelivery.clientmobile.data.repository.OrderRepository
import com.fooddelivery.clientmobile.data.repository.AddressRepository
import com.fooddelivery.clientmobile.domain.model.Address
import com.fooddelivery.clientmobile.domain.model.Cart
import com.fooddelivery.clientmobile.domain.model.CreateOrderRequest
import com.fooddelivery.clientmobile.domain.model.Order
import com.fooddelivery.clientmobile.domain.model.OrderItemRequest
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
@ActivityRetainedScoped
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val addressRepository: AddressRepository
) {

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses = _addresses.asStateFlow()

    private val _selectedAddress = MutableStateFlow<Address?>(null)
    val selectedAddress = _selectedAddress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isPlacingOrder = MutableStateFlow(false)
    val isPlacingOrder = _isPlacingOrder.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _orderResult = MutableStateFlow<Result<Order>?>(null)
    val orderResult = _orderResult.asStateFlow()

    private val _customerNotes = MutableStateFlow("")
    val customerNotes = _customerNotes.asStateFlow()

    fun loadAddresses() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = addressRepository.getAddresses(page = 0, size = 20)
            when (result) {
                is Result.Success -> {
                    val pageResponse = result.getOrNull()
                    pageResponse?.let { response ->
                        _addresses.value = response.content.map { mapToDomain(it) }
                        // Auto-select default address
                        _selectedAddress.value = response.content.firstOrNull { it.isDefault }?.let { mapToDomain(it) }
                            ?: response.content.firstOrNull()?.let { mapToDomain(it) }
                    }
                }
                is Result.Failure -> {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to load addresses"
                }
            }
            _isLoading.value = false
        }
    }

    fun selectAddress(address: Address) {
        _selectedAddress.value = address
    }

    fun setCustomerNotes(notes: String) {
        _customerNotes.value = notes
    }

    fun placeOrder(cart: Cart) {
        if (_selectedAddress.value == null) {
            _error.value = "Please select a delivery address"
            return
        }
        if (cart.isEmpty) {
            _error.value = "Cart is empty"
            return
        }

        _isPlacingOrder.value = true
        _error.value = null
        _orderResult.value = null

        viewModelScope.launch {
            val address = _selectedAddress.value!!
            val items = cart.items.map { cartItem ->
                OrderItemRequest(
                    productId = cartItem.product.id,
                    quantity = cartItem.quantity,
                    notes = cartItem.notes
                )
            }

            val request = CreateOrderRequest(
                restaurantId = cart.restaurantId!!,
                addressId = address.id,
                idempotencyKey = UUID.randomUUID().toString(),
                customerNotes = _customerNotes.value.ifBlank { null },
                items = items
            )

            val result = orderRepository.createOrder(request)
            _isPlacingOrder.value = false
            _orderResult.value = result

            when (result) {
                is Result.Success -> {
                    // Order placed successfully
                }
                is Result.Failure -> {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to place order"
                }
            }
        }
    }

    fun clearOrderResult() {
        _orderResult.value = null
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