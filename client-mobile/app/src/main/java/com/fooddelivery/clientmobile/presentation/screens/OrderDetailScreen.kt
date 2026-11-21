package com.fooddelivery.clientmobile.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.clientmobile.domain.model.Order
import com.fooddelivery.clientmobile.domain.model.OrderItem
import com.fooddelivery.clientmobile.domain.model.OrderStatus
import com.fooddelivery.clientmobile.domain.model.OrderStatusHistory
import com.fooddelivery.clientmobile.presentation.orderdetail.OrderDetailViewModel
import com.fooddelivery.clientmobile.ui.theme.FoodDeliveryTheme

@Composable
fun OrderDetailScreen(
    viewModel: OrderDetailViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    orderId: String
) {
    val order by viewModel.order.collectAsState()
    val history by viewModel.history.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isCancelling by viewModel.isCancelling.collectAsState()
    val error by viewModel.error.collectAsState()
    val cancelResult by viewModel.cancelResult.collectAsState()

    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    LaunchedEffect(orderId) {
        viewModel.loadOrder(orderId)
    }

    LaunchedEffect(cancelResult) {
        cancelResult?.let { result ->
            when (result) {
                is Result.Success -> {
                    showCancelDialog = false
                    cancelReason = ""
                }
                is Result.Failure -> {
                    // Error handled in viewModel
                }
            }
            viewModel.clearCancelResult()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            TopAppBar(
                title = { Text("Order Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Handle back */ }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FoodDeliveryTheme.colorScheme.surface
                )
            )

            order?.let { order ->
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Status Timeline
                    OrderStatusTimeline(
                        currentStatus = order.status,
                        history = history
                    )

                    // Order Info
                    OrderInfoSection(order = order)

                    // Items
                    OrderItemsSection(items = order.items)

                    // Pricing
                    OrderPricingSection(order = order)

                    // Delivery Info
                    order.customerNotes?.let {
                        DeliveryInfoSection(notes = it)
                    }

                    // Cancel button (if applicable)
                    if (order.status.canBeCancelledByCustomer()) {
                        CancelOrderButton(
                            onClick = { showCancelDialog = true },
                            isLoading = isCancelling
                        )
                    }
                }
            } ?: if (isLoading) {
                LoadingPlaceholder()
            } else if (error != null) {
                ErrorState(message = error, onRetry = { viewModel.loadOrder(orderId) })
            }
        }
    }

    // Cancel Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Order") },
            text = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Are you sure you want to cancel this order?")
                    OutlinedTextField(
                        value = cancelReason,
                        onValueChange = { cancelReason = it },
                        label = { Text("Reason (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.cancelOrder(orderId, cancelReason.ifBlank { null }) },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = FoodDeliveryTheme.colorScheme.error
                    )
                ) {
                    if (isCancelling) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = FoodDeliveryTheme.colorScheme.onError
                        )
                    } else {
                        Text("Cancel Order")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Keep Order")
                }
            }
        )
    }
}

@Composable
fun OrderStatusTimeline(
    currentStatus: OrderStatus,
    history: List<OrderStatusHistory>
) {
    val statusOrder = listOf(
        OrderStatus.PENDING,
        OrderStatus.ACCEPTED,
        OrderStatus.PREPARING,
        OrderStatus.READY,
        OrderStatus.ASSIGNED,
        OrderStatus.PICKED_UP,
        OrderStatus.DELIVERING,
        OrderStatus.DELIVERED
    )

    val currentIndex = statusOrder.indexOf(currentStatus)
    val isCompleted = currentStatus == OrderStatus.DELIVERED || currentStatus == OrderStatus.CANCELLED || currentStatus == OrderStatus.REJECTED

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Order Status", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        // Current status badge
        OrderStatusBadgeLarge(status = currentStatus)

        // Timeline
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            statusOrder.forEachIndexed { index, status ->
                val isActive = index <= currentIndex || (isCompleted && status == OrderStatus.DELIVERED)
                val isCurrent = index == currentIndex
                val isLast = index == statusOrder.lastIndex

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Circle
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(
                                if (isActive) FoodDeliveryTheme.colorScheme.primary
                                else FoodDeliveryTheme.colorScheme.surfaceVariant
                            )
                    ) {
                        if (isCurrent) {
                            Icon(
                                imageVector = getStatusIcon(status),
                                contentDescription = "",
                                tint = FoodDeliveryTheme.colorScheme.onPrimary,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    // Line (except last)
                    if (!isLast) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .fillMaxHeight()
                                .background(
                                    if (index < currentIndex) FoodDeliveryTheme.colorScheme.primary
                                    else FoodDeliveryTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {}
                    }

                    // Label
                    Text(
                        text = getStatusShortName(status),
                        fontSize = 10.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isActive) FoodDeliveryTheme.colorScheme.onSurface else FoodDeliveryTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.TextAlign.Center,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun OrderStatusBadgeLarge(status: OrderStatus) {
    val (color, text) = when (status) {
        OrderStatus.PENDING -> FoodDeliveryTheme.colorScheme.tertiary to "Pending"
        OrderStatus.ACCEPTED -> FoodDeliveryTheme.colorScheme.primary to "Accepted"
        OrderStatus.PREPARING -> FoodDeliveryTheme.colorScheme.primary to "Preparing"
        OrderStatus.READY -> FoodDeliveryTheme.colorScheme.secondary to "Ready for Pickup"
        OrderStatus.ASSIGNED -> FoodDeliveryTheme.colorScheme.primary to "Driver Assigned"
        OrderStatus.PICKED_UP -> FoodDeliveryTheme.colorScheme.primary to "Picked Up"
        OrderStatus.DELIVERING -> FoodDeliveryTheme.colorScheme.primary to "On the Way"
        OrderStatus.DELIVERED -> FoodDeliveryTheme.colorScheme.secondary to "Delivered"
        OrderStatus.CANCELLED -> FoodDeliveryTheme.colorScheme.error to "Cancelled"
        OrderStatus.REJECTED -> FoodDeliveryTheme.colorScheme.error to "Rejected"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(color.copy(alpha = 0.1f))
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getStatusIcon(status),
                contentDescription = "",
                tint = color
            )
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun OrderInfoSection(order: Order) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Order Information", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoRow(label = "Order Number", value = order.orderNumber)
                InfoRow(label = "Restaurant", value = order.restaurantName)
                InfoRow(label = "Order Date", value = formatDateTime(order.createdAt))
                order.confirmedAt?.let { InfoRow(label = "Confirmed", value = formatDateTime(it)) }
                order.preparingAt?.let { InfoRow(label = "Preparing", value = formatDateTime(it)) }
                order.readyAt?.let { InfoRow(label = "Ready", value = formatDateTime(it)) }
                order.pickedUpAt?.let { InfoRow(label = "Picked Up", value = formatDateTime(it)) }
                order.deliveredAt?.let { InfoRow(label = "Delivered", value = formatDateTime(it)) }
                order.cancelledAt?.let { InfoRow(label = "Cancelled", value = formatDateTime(it)) }
                order.cancellationReason?.let { InfoRow(label = "Cancellation Reason", value = it) }
            }
        }
    }
}

@Composable
fun OrderItemsSection(items: List<OrderItem>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Items (${items.size})", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = item.productName, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Text(text = "${item.quantity} × $${"%.2f".format(item.unitPrice)}", fontSize = 12.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant)
                            item.notes?.let { Text(text = it, fontSize = 12.sp, color = FoodDeliveryTheme.colorScheme.tertiary) }
                        }
                        Text(text = "$${"%.2f".format(item.lineTotal)}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderPricingSection(order: Order) {
    val deliveryFee = order.deliveryFee
    val tax = order.taxAmount
    val discount = order.discountAmount
    val total = order.totalAmount

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Pricing", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryRow(label = "Subtotal", value = "$${"%.2f".format(order.subtotal)}")
                if (discount > 0) SummaryRow(label = "Discount", value = "-$${"%.2f".format(discount)}", isDiscount = true)
                SummaryRow(label = "Delivery", value = "$${"%.2f".format(deliveryFee)}")
                SummaryRow(label = "Tax", value = "$${"%.2f".format(tax)}")

                Divider()

                SummaryRow(
                    label = "Total",
                    value = "$${"%.2f".format(total)}",
                    isTotal = true
                )
            }
        }
    }
}

@Composable
fun DeliveryInfoSection(notes: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Delivery Instructions", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = notes, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun CancelOrderButton(onClick: () -> Unit, isLoading: Boolean) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = FoodDeliveryTheme.colorScheme.errorContainer,
            contentColor = FoodDeliveryTheme.colorScheme.error
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = FoodDeliveryTheme.colorScheme.error
            )
        } else {
            Text("Cancel Order", fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SummaryRow(label: String, value: String, isTotal: Boolean = false, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = if (isTotal) 16.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) FoodDeliveryTheme.colorScheme.onSurface else FoodDeliveryTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = if (isTotal) 18.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium,
            color = if (isDiscount) FoodDeliveryTheme.colorScheme.secondary else if (isTotal) FoodDeliveryTheme.colorScheme.primary else FoodDeliveryTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun LoadingPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(5) {
            Box(
                modifier = Modifier.fillMaxWidth().height(100.dp)
                    .background(FoodDeliveryTheme.colorScheme.surfaceVariant)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.ErrorOutline,
            contentDescription = "Error",
            tint = FoodDeliveryTheme.colorScheme.error
        )
        Text(text = "Something went wrong", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = message, fontSize = 14.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.TextAlign.Center)
        Button(onClick = onRetry) { Text("Try Again") }
    }
}

private fun formatDateTime(date: java.time.OffsetDateTime): String {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a")
    return date.format(formatter)
}

private fun getStatusIcon(status: OrderStatus): androidx.compose.ui.graphics.vector.ImageVector {
    return when (status) {
        OrderStatus.PENDING -> androidx.compose.material.icons.Icons.Default.ClockLoader
        OrderStatus.ACCEPTED -> androidx.compose.material.icons.Icons.Default.CheckCircle
        OrderStatus.PREPARING -> androidx.compose.material.icons.Icons.Default.Restaurant
        OrderStatus.READY -> androidx.compose.material.icons.Icons.Default.CheckCircle
        OrderStatus.ASSIGNED -> androidx.compose.material.icons.Icons.Default.DirectionsBike
        OrderStatus.PICKED_UP -> androidx.compose.material.icons.Icons.Default.DirectionsBike
        OrderStatus.DELIVERING -> androidx.compose.material.icons.Icons.Default.LocalShipping
        OrderStatus.DELIVERED -> androidx.compose.material.icons.Icons.Default.CheckCircle
        OrderStatus.CANCELLED -> androidx.compose.material.icons.Icons.Default.Cancel
        OrderStatus.REJECTED -> androidx.compose.material.icons.Icons.Default.Close
    }
}

private fun getStatusShortName(status: OrderStatus): String {
    return when (status) {
        OrderStatus.PENDING -> "Pending"
        OrderStatus.ACCEPTED -> "Accepted"
        OrderStatus.PREPARING -> "Preparing"
        OrderStatus.READY -> "Ready"
        OrderStatus.ASSIGNED -> "Assigned"
        OrderStatus.PICKED_UP -> "Picked Up"
        OrderStatus.DELIVERING -> "Delivering"
        OrderStatus.DELIVERED -> "Delivered"
        OrderStatus.CANCELLED -> "Cancelled"
        OrderStatus.REJECTED -> "Rejected"
    }
}

@Composable
fun PreviewOrderDetailScreen() {
    FoodDeliveryTheme {
        OrderDetailScreen(orderId = "1")
    }
}