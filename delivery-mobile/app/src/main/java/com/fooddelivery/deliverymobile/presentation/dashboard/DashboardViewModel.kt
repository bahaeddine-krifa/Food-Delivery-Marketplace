package com.fooddelivery.deliverymobile.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fooddelivery.deliverymobile.data.api.DeliveryStatus
import com.fooddelivery.deliverymobile.data.api.DeliverySummaryResponse
import com.fooddelivery.deliverymobile.data.repository.DeliveryRepository
import com.fooddelivery.deliverymobile.data.repository.Result
import com.fooddelivery.deliverymobile.domain.model.DeliverySummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val deliveryRepository: DeliveryRepository
) : ViewModel() {

    private val _activeDeliveries = MutableStateFlow<List<DeliverySummary>>(emptyList())
    private val _availableDeliveries = MutableStateFlow<List<DeliverySummary>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val activeDeliveries = _activeDeliveries.asStateFlow()
    val availableDeliveries = _availableDeliveries.asStateFlow()
    val isLoading = _isLoading.asStateFlow()
    val error = _error.asStateFlow()

    val activeCount = _activeDeliveries.map { it.count { it.isActive } }
    val availableCount = _availableDeliveries.map { it.size }
    val completedTodayCount = _activeDeliveries.map { it.count { it.status == com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERED } }

    val dashboardUiState = combine(
        activeDeliveries, availableDeliveries, isLoading, error
    ) { active, available, loading, err ->
        DashboardUiState(active, available, loading, err)
    }

    fun loadDashboard() {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            loadActiveDeliveries()
            loadAvailableDeliveries()
            _isLoading.value = false
        }
    }

    private suspend fun loadActiveDeliveries() {
        val result = deliveryRepository.getMyDeliveries(0, 20)
        when (result) {
            is Result.Success -> {
                val deliveries = result.getOrThrow().content
                    .filter { it.status in setOf(DeliveryStatus.ASSIGNED, DeliveryStatus.ACCEPTED, DeliveryStatus.PICKED_UP, DeliveryStatus.DELIVERING) }
                    .map { mapToDomain(it) }
                _activeDeliveries.value = deliveries
            }
            is Result.Failure -> {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to load active deliveries"
            }
        }
    }

    private suspend fun loadAvailableDeliveries() {
        val result = deliveryRepository.getAvailableDeliveries(0, 10)
        when (result) {
            is Result.Success -> {
                _availableDeliveries.value = result.getOrThrow().content.map { mapToDomain(it) }
            }
            is Result.Failure -> {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to load available deliveries"
            }
        }
    }

    fun refresh() {
        loadDashboard()
    }

    fun clearError() {
        _error.value = null
    }

    private fun mapToDomain(apiSummary: DeliverySummaryResponse): DeliverySummary {
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

    private fun mapStatus(apiStatus: DeliveryStatus): com.fooddelivery.deliverymobile.domain.model.DeliveryStatus {
        return when (apiStatus) {
            DeliveryStatus.PENDING_ASSIGNMENT -> com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.PENDING_ASSIGNMENT
            DeliveryStatus.ASSIGNED -> com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ASSIGNED
            DeliveryStatus.ACCEPTED -> com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ACCEPTED
            DeliveryStatus.PICKED_UP -> com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.PICKED_UP
            DeliveryStatus.DELIVERING -> com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERING
            DeliveryStatus.DELIVERED -> com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERED
            DeliveryStatus.CANCELLED -> com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.CANCELLED
            DeliveryStatus.FAILED -> com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.FAILED
        }
    }
}

data class DashboardUiState(
    val activeDeliveries: List<DeliverySummary>,
    val availableDeliveries: List<DeliverySummary>,
    val isLoading: Boolean,
    val error: String?
)