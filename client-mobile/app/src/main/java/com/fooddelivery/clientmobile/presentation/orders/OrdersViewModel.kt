package com.fooddelivery.clientmobile.presentation.orders

import androidx.hilt.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.fooddelivery.clientmobile.data.repository.OrderRepository
import com.fooddelivery.clientmobile.domain.model.OrderSummary
import com.fooddelivery.clientmobile.domain.model.OrderStatus
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@ActivityRetainedScoped
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) {

    private val _orders = MutableStateFlow<List<OrderSummary>>(emptyList())
    val orders = _orders.asStateFlow()

    private val _statusFilter = MutableStateFlow<OrderStatus?>(null)
    val statusFilter = _statusFilter.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore = _isLoadingMore.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var currentPage = 0
    private var hasMorePages = true

    fun loadOrders(refresh: Boolean = false) {
        if (refresh) {
            currentPage = 0
            hasMorePages = true
            _orders.value = emptyList()
            _isLoading.value = true
        } else if (!hasMorePages || _isLoadingMore.value) {
            return
        } else {
            _isLoadingMore.value = true
        }

        _error.value = null
        viewModelScope.launch {
            val result = orderRepository.getOrders(
                status = _statusFilter.value,
                page = currentPage,
                size = 20
            )

            when (result) {
                is Result.Success -> {
                    val pageResponse = result.getOrNull()
                    pageResponse?.let { response ->
                        val newOrders = response.content.map { mapToDomain(it) }
                        if (refresh || currentPage == 0) {
                            _orders.value = newOrders
                        } else {
                            _orders.value = _orders.value + newOrders
                        }
                        currentPage = response.pageable.pageNumber + 1
                        hasMorePages = !response.last
                    }
                }
                is Result.Failure -> {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to load orders"
                }
            }
            _isLoading.value = false
            _isLoadingMore.value = false
        }
    }

    fun setStatusFilter(status: OrderStatus?) {
        _statusFilter.value = status
        loadOrders(refresh = true)
    }

    fun clearError() {
        _error.value = null
    }

    private fun mapToDomain(apiOrder: com.fooddelivery.clientmobile.data.api.ApiModels.OrderSummaryResponse): OrderSummary {
        return OrderSummary(
            id = java.util.UUID.fromString(apiOrder.id),
            orderNumber = apiOrder.orderNumber,
            restaurantName = apiOrder.restaurantName,
            status = com.fooddelivery.clientmobile.domain.model.OrderStatus.valueOf(apiOrder.status.value),
            totalAmount = apiOrder.totalAmount,
            itemCount = apiOrder.itemCount,
            createdAt = java.time.OffsetDateTime.parse(apiOrder.createdAt)
        )
    }
}