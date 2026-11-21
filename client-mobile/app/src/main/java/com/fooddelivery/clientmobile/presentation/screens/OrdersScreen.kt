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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgeDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import com.fooddelivery.clientmobile.domain.model.OrderStatus
import com.fooddelivery.clientmobile.domain.model.OrderSummary
import com.fooddelivery.clientmobile.presentation.orders.OrdersViewModel
import com.fooddelivery.clientmobile.ui.theme.FoodDeliveryTheme

@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onNavigateToOrderDetail: (String) -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val error by viewModel.error.collectAsState()

    val scrollState = rememberScrollState()
    var expandedDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadOrders(refresh = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            TopAppBar(
                title = { Text("My Orders", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FoodDeliveryTheme.colorScheme.surface
                ),
                actions = {
                    // Filter dropdown
                    Box {
                        IconButton(onClick = { expandedDropdown = !expandedDropdown }) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.FilterList,
                                contentDescription = "Filter"
                            )
                        }
                        DropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Orders") },
                                onClick = {
                                    viewModel.setStatusFilter(null)
                                    expandedDropdown = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Pending") },
                                onClick = {
                                    viewModel.setStatusFilter(OrderStatus.PENDING)
                                    expandedDropdown = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Active") },
                                onClick = {
                                    // Active = ACCEPTED, PREPARING, READY, ASSIGNED, PICKED_UP, DELIVERING
                                    viewModel.setStatusFilter(OrderStatus.ACCEPTED) // Just one for now
                                    expandedDropdown = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delivered") },
                                onClick = {
                                    viewModel.setStatusFilter(OrderStatus.DELIVERED)
                                    expandedDropdown = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Cancelled") },
                                onClick = {
                                    viewModel.setStatusFilter(OrderStatus.CANCELLED)
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }
            )

            // Filter chip
            if (statusFilter != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    androidx.compose.material3.Chip(
                        onClick = { viewModel.setStatusFilter(null) },
                        label = { Text(statusFilter.value) },
                        trailingIcon = { Icon(imageVector = androidx.compose.material.icons.Icons.Default.Close, contentDescription = "Clear") }
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isLoading && orders.isEmpty()) {
                    LoadingOrdersList()
                } else if (error != null && orders.isEmpty()) {
                    ErrorState(message = error, onRetry = { viewModel.loadOrders(refresh = true) })
                } else if (orders.isEmpty()) {
                    EmptyOrdersState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(orders) { order ->
                            OrderCard(
                                order = order,
                                onClick = { onNavigateToOrderDetail(order.id.toString()) }
                            )
                        }
                        if (isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: OrderSummary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.orderNumber.take(8)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                OrderStatusBadge(status = order.status)
            }

            Text(
                text = order.restaurantName,
                fontSize = 14.sp,
                color = FoodDeliveryTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${order.itemCount} items",
                    fontSize = 12.sp,
                    color = FoodDeliveryTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$${"%.2f".format(order.totalAmount)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = FoodDeliveryTheme.colorScheme.primary
                )
            }

            Text(
                text = formatOrderDate(order.createdAt),
                fontSize = 12.sp,
                color = FoodDeliveryTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun OrderStatusBadge(status: OrderStatus) {
    val (color, text) = when (status) {
        OrderStatus.PENDING -> FoodDeliveryTheme.colorScheme.tertiary to "Pending"
        OrderStatus.ACCEPTED -> FoodDeliveryTheme.colorScheme.primary to "Accepted"
        OrderStatus.PREPARING -> FoodDeliveryTheme.colorScheme.primary to "Preparing"
        OrderStatus.READY -> FoodDeliveryTheme.colorScheme.secondary to "Ready"
        OrderStatus.ASSIGNED -> FoodDeliveryTheme.colorScheme.primary to "Assigned"
        OrderStatus.PICKED_UP -> FoodDeliveryTheme.colorScheme.primary to "Picked Up"
        OrderStatus.DELIVERING -> FoodDeliveryTheme.colorScheme.primary to "Delivering"
        OrderStatus.DELIVERED -> FoodDeliveryTheme.colorScheme.secondary to "Delivered"
        OrderStatus.CANCELLED -> FoodDeliveryTheme.colorScheme.error to "Cancelled"
        OrderStatus.REJECTED -> FoodDeliveryTheme.colorScheme.error to "Rejected"
    }

    Box(
        modifier = Modifier.padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .background(color.copy(alpha = 0.1f))
                .clip(RoundedCornerShape(20.dp))
        )
    }
}

@Composable
fun LoadingOrdersList() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(5) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(100.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(FoodDeliveryTheme.colorScheme.surfaceVariant))
            }
        }
    }
}

@Composable
fun EmptyOrdersState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.ReceiptLong,
            contentDescription = "No orders",
            tint = FoodDeliveryTheme.colorScheme.onSurfaceVariant
        )
        Text(text = "No orders yet", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = "Your order history will appear here", fontSize = 14.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant)
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

private fun formatOrderDate(date: java.time.OffsetDateTime): String {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a")
    return date.format(formatter)
}

@Composable
fun PreviewOrdersScreen() {
    FoodDeliveryTheme {
        OrdersScreen(onNavigateToOrderDetail = { })
    }
}