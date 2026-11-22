package com.fooddelivery.deliverymobile.presentation.history

import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fooddelivery.deliverymobile.domain.model.DeliveryStatus
import com.fooddelivery.deliverymobile.ui.theme.FoodDeliveryDeliveryTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(26)
@Composable
fun HistoryScreen(
    onNavigateToDetail: (java.util.UUID) -> Unit
) {
    val viewModel: HistoryViewModel = hiltViewModel()
    val uiState by viewModel.historyUiState.collectAsStateWithLifecycle()

    val deliveries = uiState.deliveries

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow, RectangleShape)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Delivery History", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("${deliveries.size} total deliveries", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        androidx.compose.material3.IconButton(onClick = { viewModel.loadHistory(true) }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                        }
                        androidx.compose.material3.IconButton(onClick = { /* Show filter dialog */ }) {
                            Icon(Icons.Filled.FilterList, contentDescription = "Filter")
                        }
                    }
                }
            }

            // Filter Chips
            if (uiState.filterStatus != null) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val filterStatus = uiState.filterStatus!!
                        val filterDisplayName = when (filterStatus) {
                            DeliveryStatus.PENDING_ASSIGNMENT -> "Pending Assignment"
                            DeliveryStatus.ASSIGNED -> "Assigned"
                            DeliveryStatus.ACCEPTED -> "Accepted"
                            DeliveryStatus.PICKED_UP -> "Picked Up"
                            DeliveryStatus.DELIVERING -> "Delivering"
                            DeliveryStatus.DELIVERED -> "Delivered"
                            DeliveryStatus.CANCELLED -> "Cancelled"
                            DeliveryStatus.FAILED -> "Failed"
                        }
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer, shape = RectangleShape)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(end = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Filter: $filterDisplayName", fontSize = 12.sp)
                                androidx.compose.material3.IconButton(onClick = { viewModel.clearFilter() }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear filter")
                                }
                            }
                        }
                    }
                }
            }

            // Stats Summary
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HistoryStatCard(title = "Completed", value = "${uiState.deliveries.count { it.status.name == "DELIVERED" }}", color = MaterialTheme.colorScheme.primary)
                HistoryStatCard(title = "Failed", value = "${uiState.deliveries.count { it.status.name == "FAILED" }}", color = MaterialTheme.colorScheme.error)
                HistoryStatCard(title = "Cancelled", value = "${uiState.deliveries.count { it.status.name == "CANCELLED" }}", color = MaterialTheme.colorScheme.outline)
            }

            // Deliveries List
            if (deliveries.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.LocalShipping, contentDescription = "No history", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                        Text("No Delivery History", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Text("Completed deliveries will appear here", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(deliveries) { delivery ->
                        HistoryDeliveryCard(
                            delivery = delivery,
                            onClick = { onNavigateToDetail(delivery.id) }
                        )
                    }
                    // Load more
                    if (uiState.hasMore && !uiState.isLoading) {
                        item {
                            Button(onClick = { viewModel.loadMore() }, modifier = Modifier.fillMaxWidth()) {
                                Text("Load More")
                            }
                        }
                    }
                    if (uiState.isLoading && deliveries.isNotEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }

            // Error
            uiState.error?.let { error ->
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Card(colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Text(error, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryStatCard(title: String, value: String, color: Color) {
    Card(
        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@RequiresApi(26)
@Composable
fun HistoryDeliveryCard(
    delivery: com.fooddelivery.deliverymobile.domain.model.DeliverySummary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Order #${delivery.orderNumber}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                DeliveryStatusChip(status = delivery.status)
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Pickup", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(delivery.pickupAddress, fontSize = 14.sp, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Delivery", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(delivery.deliveryAddress, fontSize = 14.sp, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
            }

            delivery.distanceKm?.let { distance ->
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("📍 ${distance} km", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    delivery.estimatedDurationMinutes?.let { duration ->
                        Text("⏱ ${duration} min", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            delivery.assignedAt?.let { assignedAt ->
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                Text("Assigned: ${formatDate(assignedAt)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
            androidx.compose.material3.TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
                Text("View Details", fontWeight = FontWeight.Medium)
            }
        }
    }
}

@RequiresApi(26)
fun formatDate(dateTime: java.time.OffsetDateTime): String {
    return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
}

@Composable
fun DeliveryStatusChip(status: com.fooddelivery.deliverymobile.domain.model.DeliveryStatus) {
    val color = when (status) {
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.PENDING_ASSIGNMENT -> 0xFF9E9E9E.toInt()
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ASSIGNED -> 0xFF2196F3.toInt()
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ACCEPTED -> 0xFF4CAF50.toInt()
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.PICKED_UP -> 0xFFFF9800.toInt()
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERING -> 0xFF3F51B5.toInt()
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERED -> 0xFF4CAF50.toInt()
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.CANCELLED -> 0xFF9E9E9E.toInt()
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.FAILED -> 0xFFF44336.toInt()
    }
    val displayName = when (status) {
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.PENDING_ASSIGNMENT -> "Pending Assignment"
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ASSIGNED -> "Assigned"
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ACCEPTED -> "Accepted"
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.PICKED_UP -> "Picked Up"
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERING -> "Delivering"
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERED -> "Delivered"
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.CANCELLED -> "Cancelled"
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.FAILED -> "Failed"
    }
    Box(
        modifier = Modifier
            .padding(4.dp)
            .background(androidx.compose.ui.graphics.Color(color).copy(alpha = 0.15f), shape = RectangleShape)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = displayName,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = androidx.compose.ui.graphics.Color(color)
        )
    }
}