package com.fooddelivery.orders.dto

import com.fooddelivery.common.enums.OrderStatus
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.util.UUID

data class CreateOrderRequest(
    @NotNull
    val restaurantId: UUID,

    @NotNull
    val addressId: UUID,

    @NotBlank
    @Size(min = 1, max = 64)
    val idempotencyKey: String,

    @Size(max = 1000)
    val customerNotes: String? = null,

    @NotNull
    @Size(min = 1)
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    @NotNull
    val productId: UUID,

    @NotNull
    @Min(1)
    val quantity: Int,

    @Size(max = 500)
    val notes: String? = null
)

data class UpdateOrderStatusRequest(
    @NotNull
    val status: OrderStatus,

    @Size(max = 500)
    val reason: String? = null
)

data class OrderItemResponse(
    val id: UUID,
    val productId: UUID,
    val productName: String,
    val unitPrice: BigDecimal,
    val quantity: Int,
    val notes: String?,
    val lineTotal: BigDecimal
)

data class OrderResponse(
    val id: UUID,
    val orderNumber: String,
    val restaurantId: UUID,
    val restaurantName: String,
    val customerId: UUID,
    val status: OrderStatus,
    val subtotal: BigDecimal,
    val taxAmount: BigDecimal,
    val deliveryFee: BigDecimal,
    val discountAmount: BigDecimal,
    val totalAmount: BigDecimal,
    val customerNotes: String?,
    val restaurantNotes: String?,
    val estimatedPreparationMinutes: Int?,
    val estimatedDeliveryMinutes: Int?,
    val confirmedAt: java.time.OffsetDateTime?,
    val preparingAt: java.time.OffsetDateTime?,
    val readyAt: java.time.OffsetDateTime?,
    val pickedUpAt: java.time.OffsetDateTime?,
    val deliveredAt: java.time.OffsetDateTime?,
    val cancelledAt: java.time.OffsetDateTime?,
    val cancellationReason: String?,
    val items: List<OrderItemResponse>,
    val createdAt: java.time.OffsetDateTime,
    val updatedAt: java.time.OffsetDateTime
) {
    companion object {
        fun fromEntity(order: com.fooddelivery.orders.domain.Order): OrderResponse {
            val items = order.items.map { item ->
                OrderItemResponse(
                    id = item.id,
                    productId = item.product?.id ?: UUID.randomUUID(),
                    productName = item.productNameSnapshot,
                    unitPrice = item.unitPriceSnapshot,
                    quantity = item.quantity,
                    notes = item.notes,
                    lineTotal = item.lineTotal
                )
            }

            return OrderResponse(
                id = order.id,
                orderNumber = order.orderNumber,
                restaurantId = order.restaurant?.id ?: UUID.randomUUID(),
                restaurantName = order.restaurant?.name ?: "Unknown Restaurant",
                customerId = order.customer?.id ?: UUID.randomUUID(),
                status = order.status,
                subtotal = order.subtotal,
                taxAmount = order.taxAmount,
                deliveryFee = order.deliveryFee,
                discountAmount = order.discountAmount,
                totalAmount = order.totalAmount,
                customerNotes = order.customerNotes,
                restaurantNotes = order.restaurantNotes,
                estimatedPreparationMinutes = order.estimatedPreparationMinutes,
                estimatedDeliveryMinutes = order.estimatedDeliveryMinutes,
                confirmedAt = order.confirmedAt,
                preparingAt = order.preparingAt,
                readyAt = order.readyAt,
                pickedUpAt = order.pickedUpAt,
                deliveredAt = order.deliveredAt,
                cancelledAt = order.cancelledAt,
                cancellationReason = order.cancellationReason,
                items = items,
                createdAt = order.createdAt,
                updatedAt = order.updatedAt
            )
        }
    }
}

data class OrderStatusHistoryResponse(
    val id: UUID,
    val fromStatus: OrderStatus?,
    val toStatus: OrderStatus,
    val changedBy: UUID,
    val changeReason: String?,
    val metadata: String?,
    val createdAt: java.time.OffsetDateTime
) {
    companion object {
        fun fromEntity(history: com.fooddelivery.orders.domain.OrderStatusHistory): OrderStatusHistoryResponse {
            return OrderStatusHistoryResponse(
                id = history.id,
                fromStatus = history.fromStatus,
                toStatus = history.toStatus,
                changedBy = history.changedBy,
                changeReason = history.changeReason,
                metadata = history.metadata,
                createdAt = history.createdAt
            )
        }
    }
}

data class OrderSummaryResponse(
    val id: UUID,
    val orderNumber: String,
    val restaurantName: String,
    val status: OrderStatus,
    val totalAmount: BigDecimal,
    val itemCount: Int,
    val createdAt: java.time.OffsetDateTime
) {
    companion object {
        fun fromEntity(order: com.fooddelivery.orders.domain.Order): OrderSummaryResponse {
            return OrderSummaryResponse(
                id = order.id,
                orderNumber = order.orderNumber,
                restaurantName = order.restaurant?.name ?: "Unknown Restaurant",
                status = order.status,
                totalAmount = order.totalAmount,
                itemCount = order.items.sumOf { it.quantity },
                createdAt = order.createdAt
            )
        }
    }
}