package com.fooddelivery.deliverymobile.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fooddelivery.deliverymobile.data.api.DeliveryStatus as ApiDeliveryStatus
import com.fooddelivery.deliverymobile.data.api.DeliverySummaryResponse
import com.fooddelivery.deliverymobile.data.repository.DeliveryRepository
import com.fooddelivery.deliverymobile.data.repository.Result
import com.fooddelivery.deliverymobile.domain.model.DeliveryStatus
import com.fooddelivery.deliverymobile.domain.model.DeliverySummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val deliveryRepository: DeliveryRepository
) : ViewModel() {

    private val _deliveries = MutableStateFlow<List<DeliverySummary>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _isRefreshing = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _currentPage = MutableStateFlow(0)
    private val _hasMore = MutableStateFlow(true)
    private val _filterStatus = MutableStateFlow<DeliveryStatus?>(null)

    val deliveries: StateFlow<List<DeliverySummary>> = _deliveries.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    val error: StateFlow<String?> = _error.asStateFlow()
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()
    val filterStatus: StateFlow<DeliveryStatus?> = _filterStatus.asStateFlow()

    val historyUiState = combine(
        deliveries, isLoading, isRefreshing, error, hasMore, filterStatus
    ) { values: Array<Any?> ->
        HistoryUiState(
            deliveries = values[0] as List<DeliverySummary>,
            isLoading = values[1] as Boolean,
            isRefreshing = values[2] as Boolean,
            error = values[3] as String?,
            hasMore = values[4] as Boolean,
            filterStatus = values[5] as DeliveryStatus?
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryUiState(emptyList(), false, false, null, true, null)) as StateFlow<HistoryUiState>

    fun loadHistory(refresh: Boolean = false) {
        if (refresh) {
            _isRefreshing.value = true
            _currentPage.value = 0
            _hasMore.value = true
        } else {
            _isLoading.value = true
        }
        _error.value = null

        viewModelScope.launch {
            loadDeliveries(refresh)
            if (refresh) _isRefreshing.value = false else _isLoading.value = false
        }
    }

    fun loadMore() {
        if (!_hasMore.value || _isLoading.value) return
        _isLoading.value = true
        viewModelScope.launch {
            loadDeliveries(false)
            _isLoading.value = false
        }
    }

    fun setFilterStatus(status: DeliveryStatus?) {
        _filterStatus.value = status
        loadHistory(true)
    }

    fun clearFilter() {
        setFilterStatus(null)
    }

    private suspend fun loadDeliveries(refresh: Boolean) {
        val page = if (refresh) 0 else _currentPage.value + 1
        val result = deliveryRepository.getMyDeliveries(page, 20)
        when (result) {
            is Result.Success -> {
                val response = result.getOrThrow()
                var filtered = response.content.map { mapToDomain(it) }
                _filterStatus.value?.let { status ->
                    filtered = filtered.filter { it.status == status }
                }
                if (refresh) {
                    _deliveries.value = filtered
                } else {
                    _deliveries.value = _deliveries.value + filtered
                }
                _currentPage.value = response.number
                _hasMore.value = !response.last
            }
            is Result.Failure -> {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to load history"
            }
        }
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

    private fun mapStatus(apiStatus: ApiDeliveryStatus): DeliveryStatus {
        return when (apiStatus) {
            ApiDeliveryStatus.PENDING_ASSIGNMENT -> DeliveryStatus.PENDING_ASSIGNMENT
            ApiDeliveryStatus.ASSIGNED -> DeliveryStatus.ASSIGNED
            ApiDeliveryStatus.ACCEPTED -> DeliveryStatus.ACCEPTED
            ApiDeliveryStatus.PICKED_UP -> DeliveryStatus.PICKED_UP
            ApiDeliveryStatus.DELIVERING -> DeliveryStatus.DELIVERING
            ApiDeliveryStatus.DELIVERED -> DeliveryStatus.DELIVERED
            ApiDeliveryStatus.CANCELLED -> DeliveryStatus.CANCELLED
            ApiDeliveryStatus.FAILED -> DeliveryStatus.FAILED
        }
    }

    fun clearError() {
        _error.value = null
    }
}

data class HistoryUiState(
    val deliveries: List<DeliverySummary>,
    val isLoading: Boolean,
    val isRefreshing: Boolean,
    val error: String?,
    val hasMore: Boolean,
    val filterStatus: DeliveryStatus?
)