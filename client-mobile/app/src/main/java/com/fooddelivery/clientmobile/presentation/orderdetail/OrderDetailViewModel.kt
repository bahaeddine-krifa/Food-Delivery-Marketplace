package com.fooddelivery.clientmobile.presentation.orderdetail

import androidx.hilt.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.fooddelivery.clientmobile.data.repository.OrderRepository
import com.fooddelivery.clientmobile.domain.model.Order
import com.fooddelivery.clientmobile.domain.model.OrderItem
import com.fooddelivery.clientmobile.domain.model.OrderStatus
import com.fooddelivery.clientmobile.domain.model.OrderStatusHistory
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@ActivityRetainedScoped
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) {

    private val _order = MutableStateFlow<Order?>(null)
    val order = _order.asStateFlow()

    private val _history = MutableStateFlow<List<OrderStatusHistory>>(emptyList())
    val history = _history.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isCancelling = MutableStateFlow(false)
    val isCancelling = _isCancelling.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _cancelResult = MutableStateFlow<Result<Order>?>(null)
    val cancelResult = _cancelResult.asStateFlow()

    fun loadOrder(orderId: String) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            val result = orderRepository.getOrder(orderId)
            when (result) {
                is Result.Success -> {
                    val apiOrder = result.getOrNull()
                    apiOrder?.let { _order.value = mapToDomain(it) }
                    loadHistory(orderId)
                }
                is Result.Failure -> {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to load order"
                }
            }
            _isLoading.value = false
        }
    }

    private fun loadHistory(orderId: String) {
        viewModelScope.launch {
            val result = orderRepository.getOrderHistory(orderId, page = 0, size = 50)
            when (result) {
                is Result.Success -> {
                    val pageResponse = result.getOrNull()
                    pageResponse?.let { response ->
                        _history.value = response.content.map { mapToDomain(it) }
                    }
                }
                is Result.Failure -> {
                    // History is optional, don't show error
                }
            }
        }
    }

    fun cancelOrder(orderId: String, reason: String? = null) {
        val currentOrder = _order.value
        if (currentOrder == null) return

        // Check if order can be cancelled by customer
        if (!currentOrder.status.canBeCancelledByCustomer()) {
            _error.value = "This order cannot be cancelled at its current status"
            return
        }

        _isCancelling.value = true
        _error.value = null
        _cancelResult.value = null

        viewModelScope.launch {
            val result = orderRepository.cancelOrder(orderId, reason)
            _isCancelling.value = false
            _cancelResult.value = result

            when (result) {
                is Result.Success -> {
                    val updatedOrder = result.getOrNull()
                    updatedOrder?.let { _order.value = mapToDomain(it) }
                    loadHistory(orderId)
                }
                is Result.Failure -> {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to cancel order"
                }
            }
        }
    }

    fun clearCancelResult() {
        _cancelResult.value = null
    }

    fun clearError() {
        _error.value = null
    }

    private fun mapToDomain(apiOrder: com.fooddelivery.clientmobile.data.api.ApiModels.OrderResponse): Order {
        return Order(
            id = java.util.UUID.fromString(apiOrder.id),
            orderNumber = apiOrder.orderNumber,
            restaurantId = java.util.UUID.fromString(apiOrder.restaurantId),
            restaurantName = apiOrder.restaurantName,
            customerId = java.util.UUID.fromString(apiOrder.customerId),
            status = com.fooddelivery.clientmobile.domain.model.OrderStatus.valueOf(apiOrder.status.value),
            subtotal = apiOrder.subtotal,
            taxAmount = apiOrder.taxAmount,
            deliveryFee = apiOrder.deliveryFee,
            discountAmount = apiOrder.discountAmount,
            totalAmount = apiOrder.totalAmount,
            customerNotes = apiOrder.customerNotes,
            restaurantNotes = apiOrder.restaurantNotes,
            estimatedPreparationMinutes = apiOrder.estimatedPreparationMinutes,
            estimatedDeliveryMinutes = apiOrder.estimatedDeliveryMinutes,
            confirmedAt = apiOrder.confirmedAt?.let { java.time.OffsetDateTime.parse(it) },
            preparingAt = apiOrder.preparingAt?.let { java.time.OffsetDateTime.parse(it) },
            readyAt = apiOrder.readyAt?.let { java.time.OffsetDateTime.parse(it) },
            pickedUpAt = apiOrder.pickedUpAt?.let { java.time.OffsetDateTime.parse(it) },
            deliveredAt = apiOrder.deliveredAt?.let { java.time.OffsetDateTime.parse(it) },
            cancelledAt = apiOrder.cancelledAt?.let { java.time.OffsetDateTime.parse(it) },
            cancellationReason = apiOrder.cancellationReason,
            items = apiOrder.items.map { mapToDomain(it) },
            createdAt = java.time.OffsetDateTime.parse(apiOrder.createdAt),
            updatedAt = java.time.OffsetDateTime.parse(apiOrder.updatedAt)
        )
    }

    private fun mapToDomain(apiItem: com.fooddelivery.clientmobile.data.api.ApiModels.OrderItemResponse): OrderItem {
        return OrderItem(
            id = java.util.UUID.fromString(apiItem.id),
            productId = java.util.UUID.fromString(apiItem.productId),
            productName = apiItem.productName,
            unitPrice = apiItem.unitPrice,
            quantity = apiItem.quantity,
            notes = apiItem.notes,
            lineTotal = apiItem.lineTotal
        )
    }

    private fun mapToDomain(apiHistory: com.fooddelivery.clientmobile.data.api.ApiModels.OrderStatusHistoryResponse): OrderStatusHistory {
        return OrderStatusHistory(
            id = java.util.UUID.fromString(apiHistory.id),
            fromStatus = apiHistory.fromStatus?.let { com.fooddelivery.clientmobile.domain.model.OrderStatus.valueOf(it.value) },
            toStatus = com.fooddelivery.clientmobile.domain.model.OrderStatus.valueOf(apiHistory.toStatus.value),
            changedBy = java.util.UUID.fromString(apiHistory.changedBy),
            changeReason = apiHistory.changeReason,
            metadata = apiHistory.metadata,
            createdAt = java.time.OffsetDateTime.parse(apiHistory.createdAt)
        )
    }
}