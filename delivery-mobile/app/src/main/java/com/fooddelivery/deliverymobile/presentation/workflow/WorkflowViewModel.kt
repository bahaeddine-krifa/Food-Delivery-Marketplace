package com.fooddelivery.deliverymobile.presentation.workflow

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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkflowViewModel @Inject constructor(
    private val deliveryRepository: DeliveryRepository
) : ViewModel() {

    private val _delivery = MutableStateFlow<Delivery?>(null)
    private val _orderSummary = MutableStateFlow<OrderSummary?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _isActionLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _successMessage = MutableStateFlow<String?>(null)

    val delivery: StateFlow<Delivery?> = _delivery.asStateFlow()
    val orderSummary: StateFlow<OrderSummary?> = _orderSummary.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val isActionLoading: StateFlow<Boolean> = _isActionLoading.asStateFlow()
    val error: StateFlow<String?> = _error.asStateFlow()
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    val canAccept = delivery.map { it?.canAccept == true }
    val canPickUp = delivery.map { it?.canPickUp == true }
    val canStartDelivering = delivery.map { it?.canStartDelivering == true }
    val canComplete = delivery.map { it?.canComplete == true }
    val canFail = delivery.map { it?.canFail == true }

    val workflowUiState = combine(
        delivery, orderSummary, isLoading, isActionLoading, error, successMessage,
        canAccept, canPickUp, canStartDelivering, canComplete, canFail
    ) { values ->
        WorkflowUiState(
            delivery = values[0] as Delivery?,
            orderSummary = values[1] as OrderSummary?,
            isLoading = values[2] as Boolean,
            isActionLoading = values[3] as Boolean,
            error = values[4] as String?,
            successMessage = values[5] as String?,
            canAccept = values[6] as Boolean,
            canPickUp = values[7] as Boolean,
            canStartDelivering = values[8] as Boolean,
            canComplete = values[9] as Boolean,
            canFail = values[10] as Boolean
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WorkflowUiState(null, null, false, false, null, null, false, false, false, false, false)) as StateFlow<WorkflowUiState>

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
                    _error.value = deliveryResult.exceptionOrNull()?.message ?: "Failed to load delivery"
                }
            }
            _isLoading.value = false
        }
    }

    private fun loadOrderSummary(orderId: java.util.UUID) {
        viewModelScope.launch {
            val result = deliveryRepository.getOrderSummaryForDelivery(orderId)
            when (result) {
                is Result.Success -> _orderSummary.value = mapOrderSummary(result.getOrThrow())
                is Result.Failure -> _error.value = result.exceptionOrNull()?.message ?: "Failed to load order details"
            }
        }
    }

    fun acceptDelivery(onResult: (Boolean) -> Unit) {
        _delivery.value?.let { delivery ->
            _isActionLoading.value = true
            viewModelScope.launch {
                val result = deliveryRepository.acceptDelivery(delivery.id)
                handleActionResult(result, "Delivery accepted", onResult)
            }
        }
    }

    fun pickUpDelivery(
        pickupLatitude: Double,
        pickupLongitude: Double,
        pickupPhotoUrl: String? = null,
        onResult: (Boolean) -> Unit
    ) {
        _delivery.value?.let { delivery ->
            _isActionLoading.value = true
            viewModelScope.launch {
                val result = deliveryRepository.pickUpDelivery(delivery.id, pickupLatitude, pickupLongitude, pickupPhotoUrl)
                handleActionResult(result, "Order picked up", onResult)
            }
        }
    }

    fun startDelivering(
        deliveryLatitude: Double,
        deliveryLongitude: Double,
        onResult: (Boolean) -> Unit
    ) {
        _delivery.value?.let { delivery ->
            _isActionLoading.value = true
            viewModelScope.launch {
                val result = deliveryRepository.startDelivering(delivery.id, deliveryLatitude, deliveryLongitude)
                handleActionResult(result, "Delivery started", onResult)
            }
        }
    }

    fun completeDelivery(
        deliveryLatitude: Double,
        deliveryLongitude: Double,
        deliveryPhotoUrl: String? = null,
        customerSignatureUrl: String? = null,
        onResult: (Boolean) -> Unit
    ) {
        _delivery.value?.let { delivery ->
            _isActionLoading.value = true
            viewModelScope.launch {
                val result = deliveryRepository.completeDelivery(
                    delivery.id, deliveryLatitude, deliveryLongitude,
                    deliveryPhotoUrl, customerSignatureUrl
                )
                handleActionResult(result, "Delivery completed successfully", onResult)
            }
        }
    }

    fun failDelivery(
        reason: String,
        pickupLatitude: Double? = null,
        pickupLongitude: Double? = null,
        deliveryLatitude: Double? = null,
        deliveryLongitude: Double? = null,
        onResult: (Boolean) -> Unit
    ) {
        _delivery.value?.let { delivery ->
            _isActionLoading.value = true
            viewModelScope.launch {
                val result = deliveryRepository.failDelivery(
                    delivery.id, reason,
                    pickupLatitude, pickupLongitude,
                    deliveryLatitude, deliveryLongitude
                )
                handleActionResult(result, "Delivery marked as failed", onResult)
            }
        }
    }

    private fun handleActionResult(result: Result<com.fooddelivery.deliverymobile.data.api.DeliveryResponse>, successMsg: String, onResult: (Boolean) -> Unit) {
        when (result) {
            is Result.Success -> {
                val updatedDelivery = mapDelivery(result.getOrThrow())
                _delivery.value = updatedDelivery
                _successMessage.value = successMsg
                _isActionLoading.value = false
                onResult(true)
            }
            is Result.Failure -> {
                _error.value = result.exceptionOrNull()?.message ?: "Action failed"
                _isActionLoading.value = false
                onResult(false)
            }
        }
    }

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
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

data class WorkflowUiState(
    val delivery: Delivery?,
    val orderSummary: OrderSummary?,
    val isLoading: Boolean,
    val isActionLoading: Boolean,
    val error: String?,
    val successMessage: String?,
    val canAccept: Boolean,
    val canPickUp: Boolean,
    val canStartDelivering: Boolean,
    val canComplete: Boolean,
    val canFail: Boolean
)