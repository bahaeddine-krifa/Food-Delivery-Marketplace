package com.fooddelivery.deliverymobile.presentation.assignments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fooddelivery.deliverymobile.data.repository.DeliveryRepository
import com.fooddelivery.deliverymobile.data.repository.Result
import com.fooddelivery.deliverymobile.domain.model.DeliverySummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class AssignmentsViewModel @Inject constructor(
    private val deliveryRepository: DeliveryRepository
) : ViewModel() {

    private val _availableDeliveries = MutableStateFlow<List<DeliverySummary>>(emptyList())
    private val _myDeliveries = MutableStateFlow<List<DeliverySummary>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _isRefreshing = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _currentPage = MutableStateFlow(0)
    private val _hasMore = MutableStateFlow(true)

    val availableDeliveries: kotlinx.coroutines.flow.StateFlow<List<DeliverySummary>> = _availableDeliveries.asStateFlow()
    val myDeliveries: kotlinx.coroutines.flow.StateFlow<List<DeliverySummary>> = _myDeliveries.asStateFlow()
    val isLoading: kotlinx.coroutines.flow.StateFlow<Boolean> = _isLoading.asStateFlow()
    val isRefreshing: kotlinx.coroutines.flow.StateFlow<Boolean> = _isRefreshing.asStateFlow()
    val error: kotlinx.coroutines.flow.StateFlow<String?> = _error.asStateFlow()
    val hasMore: kotlinx.coroutines.flow.StateFlow<Boolean> = _hasMore.asStateFlow()

    val assignmentsUiState = combine(
        availableDeliveries, myDeliveries, isLoading, isRefreshing, error, hasMore
    ) { values ->
        AssignmentsUiState(
            values[0] as List<DeliverySummary>,
            values[1] as List<DeliverySummary>,
            values[2] as Boolean,
            values[3] as Boolean,
            values[4] as String?,
            values[5] as Boolean
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AssignmentsUiState(emptyList(), emptyList(), false, false, null, true))

    fun loadAssignments(refresh: Boolean = false) {
        if (refresh) {
            _isRefreshing.value = true
            _currentPage.value = 0
            _hasMore.value = true
        } else {
            _isLoading.value = true
        }
        _error.value = null

        viewModelScope.launch {
            loadAvailableDeliveries(refresh)
            loadMyDeliveries(refresh)
            if (refresh) _isRefreshing.value = false else _isLoading.value = false
        }
    }

    fun loadMore() {
        if (!_hasMore.value || _isLoading.value) return
        _isLoading.value = true
        viewModelScope.launch {
            loadAvailableDeliveries(false)
            _isLoading.value = false
        }
    }

    private suspend fun loadAvailableDeliveries(refresh: Boolean) {
        val page = if (refresh) 0 else _currentPage.value + 1
        val result = deliveryRepository.getAvailableDeliveries(page, 20)
        when (result) {
            is Result.Success -> {
                val response = result.getOrThrow()
                val mapped = response.content.map { mapToDomain(it) }
                if (refresh) {
                    _availableDeliveries.value = mapped
                } else {
                    _availableDeliveries.value = _availableDeliveries.value + mapped
                }
                _currentPage.value = response.number
                _hasMore.value = !response.last
            }
            is Result.Failure -> {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to load available deliveries"
            }
        }
    }

    private suspend fun loadMyDeliveries(refresh: Boolean) {
        val result = deliveryRepository.getMyDeliveries(0, 20)
        when (result) {
            is Result.Success -> {
                _myDeliveries.value = result.getOrThrow().content.map { mapToDomain(it) }
            }
            is Result.Failure -> {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to load my deliveries"
            }
        }
    }

    fun acceptDelivery(deliveryId: java.util.UUID, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = deliveryRepository.acceptDelivery(deliveryId)
            when (result) {
                is Result.Success -> {
                    loadAssignments(true)
                    onResult(true)
                }
                is Result.Failure -> {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to accept delivery"
                    onResult(false)
                }
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    private fun mapToDomain(apiSummary: com.fooddelivery.deliverymobile.data.api.DeliverySummaryResponse): DeliverySummary {
        return DeliverySummary(
            id = apiSummary.id,
            orderNumber = apiSummary.orderNumber,
            status = mapStatus(apiSummary.status),
            pickupAddress = apiSummary.pickupAddress,
            deliveryAddress = apiSummary.deliveryAddress,
            distanceKm = apiSummary.distanceKm,
            estimatedDurationMinutes = apiSummary.estimatedDurationMinutes,
            assignedAt = apiSummary.assignedAt,
            createdAt = apiSummary.createdAt
        )
    }

    private fun mapStatus(apiStatus: com.fooddelivery.deliverymobile.data.api.DeliveryStatus): com.fooddelivery.deliverymobile.domain.model.DeliveryStatus {
        return when (apiStatus) {
            com.fooddelivery.deliverymobile.data.api.DeliveryStatus.PENDING_ASSIGNMENT -> com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.PENDING_ASSIGNMENT
            com.fooddelivery.deliverymobile.data.api.DeliveryStatus.ASSIGNED -> com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ASSIGNED
            com.fooddelivery.deliverymobile.data.api.DeliveryStatus.ACCEPTED -> com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ACCEPTED
            com.fooddelivery.deliverymobile.data.api.DeliveryStatus.PICKED_UP -> com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.PICKED_UP
            com.fooddelivery.deliverymobile.data.api.DeliveryStatus.DELIVERING -> com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERING
            com.fooddelivery.deliverymobile.data.api.DeliveryStatus.DELIVERED -> com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERED
            com.fooddelivery.deliverymobile.data.api.DeliveryStatus.CANCELLED -> com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.CANCELLED
            com.fooddelivery.deliverymobile.data.api.DeliveryStatus.FAILED -> com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.FAILED
        }
    }
}

data class AssignmentsUiState(
    val availableDeliveries: List<DeliverySummary>,
    val myDeliveries: List<DeliverySummary>,
    val isLoading: Boolean,
    val isRefreshing: Boolean,
    val error: String?,
    val hasMore: Boolean
)