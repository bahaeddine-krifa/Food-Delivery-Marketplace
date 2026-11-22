package com.fooddelivery.deliverymobile.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fooddelivery.deliverymobile.data.repository.DeliveryRepository
import com.fooddelivery.deliverymobile.data.repository.Result
import com.fooddelivery.deliverymobile.domain.model.Delivery
import com.fooddelivery.deliverymobile.domain.model.DeliveryStatus
import com.fooddelivery.deliverymobile.domain.model.OrderSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val deliveryRepository: DeliveryRepository
) : ViewModel() {

    private val _delivery = MutableStateFlow<Delivery?>(null)
    private val _orderSummary = MutableStateFlow<OrderSummary?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val delivery: kotlinx.coroutines.flow.StateFlow<Delivery?> = _delivery.asStateFlow()
    val orderSummary: kotlinx.coroutines.flow.StateFlow<OrderSummary?> = _orderSummary.asStateFlow()
    val isLoading: kotlinx.coroutines.flow.StateFlow<Boolean> = _isLoading.asStateFlow()
    val error: kotlinx.coroutines.flow.StateFlow<String?> = _error.asStateFlow()

    val detailUiState = combine(
        delivery, orderSummary, isLoading, error
    ) { del, order, loading, err ->
        DetailUiState(del, order, loading, err)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DetailUiState(null, null, false, null))

    fun loadDelivery(deliveryId: java.util.UUID) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            val deliveryResult = deliveryRepository.getDeliveryById(deliveryId)
            when (deliveryResult) {
                is Result.Success -> {
                    val delivery = deliveryResult.getOrThrow()
                    _delivery.value = mapDelivery(delivery)
                    if (delivery.orderId != null) {
                        loadOrderSummary(delivery.orderId!!)
                    }
                }
                is Result.Failure -> {
                    _error.value = deliveryResult.exceptionOrNull()?.message ?: "Failed to load delivery details"
                }
            }
            _isLoading.value = false
        }
    }

    private suspend fun loadOrderSummary(orderId: java.util.UUID) {
        val result = deliveryRepository.getOrderSummaryForDelivery(orderId)
        when (result) {
            is Result.Success -> _orderSummary.value = mapOrderSummary(result.getOrThrow())
            is Result.Failure -> _error.value = result.exceptionOrNull()?.message ?: "Failed to load order details"
        }
    }

    fun clearError() {
        _error.value = null
    }

    private fun mapDelivery(apiDelivery: com.fooddelivery.deliverymobile.data.api.DeliveryResponse): Delivery {
        return Delivery(
            id = apiDelivery.id,
            orderId = apiDelivery.orderId,
            orderNumber = apiDelivery.orderNumber,
            deliveryPersonId = apiDelivery.deliveryPersonId,
            deliveryPersonName = apiDelivery.deliveryPersonName,
            status = mapStatus(apiDelivery.status),
            pickupAddress = apiDelivery.pickupAddress,
            deliveryAddress = apiDelivery.deliveryAddress,
            pickupLatitude = apiDelivery.pickupLatitude,
            pickupLongitude = apiDelivery.pickupLongitude,
            deliveryLatitude = apiDelivery.deliveryLatitude,
            deliveryLongitude = apiDelivery.deliveryLongitude,
            pickupCode = apiDelivery.pickupCode,
            deliveryCode = apiDelivery.deliveryCode,
            pickupPhotoUrl = apiDelivery.pickupPhotoUrl,
            deliveryPhotoUrl = apiDelivery.deliveryPhotoUrl,
            customerSignatureUrl = apiDelivery.customerSignatureUrl,
            distanceKm = apiDelivery.distanceKm,
            estimatedDurationMinutes = apiDelivery.estimatedDurationMinutes,
            actualDurationMinutes = apiDelivery.actualDurationMinutes,
            assignedAt = apiDelivery.assignedAt,
            acceptedAt = apiDelivery.acceptedAt,
            pickedUpAt = apiDelivery.pickedUpAt,
            deliveredAt = apiDelivery.deliveredAt,
            createdAt = apiDelivery.createdAt,
            updatedAt = apiDelivery.updatedAt
        )
    }

    private fun mapOrderSummary(apiOrder: com.fooddelivery.deliverymobile.data.api.OrderSummaryForDelivery): OrderSummary {
        return OrderSummary(
            id = apiOrder.id,
            orderNumber = apiOrder.orderNumber,
            totalAmount = apiOrder.totalAmount,
            customerName = apiOrder.customerName,
            customerPhone = apiOrder.customerPhone,
            restaurantName = apiOrder.restaurantName,
            restaurantPhone = apiOrder.restaurantPhone
        )
    }

    private fun mapStatus(apiStatus: com.fooddelivery.deliverymobile.data.api.DeliveryStatus): DeliveryStatus {
        return when (apiStatus) {
            com.fooddelivery.deliverymobile.data.api.DeliveryStatus.PENDING_ASSIGNMENT -> DeliveryStatus.PENDING_ASSIGNMENT
            com.fooddelivery.deliverymobile.data.api.DeliveryStatus.ASSIGNED -> DeliveryStatus.ASSIGNED
            com.fooddelivery.deliverymobile.data.api.DeliveryStatus.ACCEPTED -> DeliveryStatus.ACCEPTED
            com.fooddelivery.deliverymobile.data.api.DeliveryStatus.PICKED_UP -> DeliveryStatus.PICKED_UP
            com.fooddelivery.deliverymobile.data.api.DeliveryStatus.DELIVERING -> DeliveryStatus.DELIVERING
            com.fooddelivery.deliverymobile.data.api.DeliveryStatus.DELIVERED -> DeliveryStatus.DELIVERED
            com.fooddelivery.deliverymobile.data.api.DeliveryStatus.CANCELLED -> DeliveryStatus.CANCELLED
            com.fooddelivery.deliverymobile.data.api.DeliveryStatus.FAILED -> DeliveryStatus.FAILED
        }
    }
}

data class DetailUiState(
    val delivery: Delivery?,
    val orderSummary: OrderSummary?,
    val isLoading: Boolean,
    val error: String?
)