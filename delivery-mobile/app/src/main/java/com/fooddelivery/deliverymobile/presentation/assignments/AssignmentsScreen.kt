package com.fooddelivery.deliverymobile.presentation.assignments

import androidx.compose.foundation.background
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
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.DirectionsBike
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
import com.fooddelivery.deliverymobile.domain.model.DeliverySummary
import com.fooddelivery.deliverymobile.ui.theme.FoodDeliveryDeliveryTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentsScreen(
    onNavigateToWorkflow: (java.util.UUID) -> Unit,
    onNavigateToDetail: (java.util.UUID) -> Unit
) {
    val viewModel: AssignmentsViewModel = hiltViewModel()
    val uiState by viewModel.assignmentsUiState.collectAsStateWithLifecycle()

    val availableDeliveries = uiState.availableDeliveries
    val myDeliveries = uiState.myDeliveries

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
                        Text("Assignments", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("Available: ${availableDeliveries.size} | My Deliveries: ${myDeliveries.size}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    androidx.compose.material3.IconButton(onClick = { viewModel.loadAssignments(true) }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            }

            // Tabs
            var selectedTab by remember { mutableStateOf(0) }
            val tabTitles = listOf("Available (${availableDeliveries.size})", "My Deliveries (${myDeliveries.size})")

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabTitles.forEachIndexed { index, title ->
                    androidx.compose.material3.FilterChip(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        label = { Text(title, fontSize = 14.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            // Content
            if (selectedTab == 0) {
                // Available Deliveries
                if (availableDeliveries.isEmpty() && !uiState.isLoading) {
                    EmptyState(
                        icon = Icons.Filled.DirectionsBike,
                        title = "No Available Deliveries",
                        subtitle = "New delivery requests will appear here",
                        actionText = "Refresh",
                        onActionClick = { viewModel.loadAssignments(true) }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(availableDeliveries) { delivery ->
                            AvailableDeliveryCard(
                                delivery = delivery,
                                onAccept = { success ->
                                    if (success) {
                                        // Navigate to workflow after accepting
                                        onNavigateToWorkflow(delivery.id)
                                    }
                                }
                            )
                        }
                        // Load more
                        if (uiState.hasMore && !uiState.isLoading) {
                            item {
                                Button(
                                    onClick = { viewModel.loadMore() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Load More")
                                }
                            }
                        }
                        if (uiState.isLoading && availableDeliveries.isNotEmpty()) {
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
            } else {
                // My Deliveries
                if (myDeliveries.isEmpty() && !uiState.isLoading) {
                    EmptyState(
                        icon = Icons.Filled.LocalShipping,
                        title = "No Active Deliveries",
                        subtitle = "Accept deliveries from the Available tab",
                        actionText = "Browse Available",
                        onActionClick = { selectedTab = 0 }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(myDeliveries) { delivery ->
                            MyDeliveryCard(
                                delivery = delivery,
                                onClick = { onNavigateToWorkflow(delivery.id) }
                            )
                        }
                    }
                }
            }

            // Error
            uiState.error?.let { error ->
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(error, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    actionText: String,
    onActionClick: () -> Unit
) {
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
            Icon(icon, contentDescription = title, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Button(onClick = onActionClick) {
                Text(actionText)
            }
        }
    }
}

@Composable
fun AvailableDeliveryCard(
    delivery: com.fooddelivery.deliverymobile.domain.model.DeliverySummary,
    onAccept: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Order #${delivery.orderNumber}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                StatusChip(status = delivery.status)
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

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
            Button(
                onClick = { onAccept(true) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Accept Delivery", fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun MyDeliveryCard(
    delivery: com.fooddelivery.deliverymobile.domain.model.DeliverySummary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Order #${delivery.orderNumber}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                StatusChip(status = delivery.status)
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

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
            androidx.compose.material3.TextButton(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue Delivery", fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun StatusChip(status: DeliveryStatus) {
    val color = status.statusColor
    val displayName = status.displayName
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