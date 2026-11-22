package com.fooddelivery.deliverymobile.presentation.dashboard

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fooddelivery.deliverymobile.navigation.NavRoute
import com.fooddelivery.deliverymobile.ui.theme.FoodDeliveryDeliveryTheme
import com.fooddelivery.deliverymobile.domain.model.DeliveryStatus
import com.fooddelivery.deliverymobile.domain.model.DeliverySummary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAssignments: () -> Unit,
    onNavigateToWorkflow: (java.util.UUID) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.dashboardUiState.collectAsStateWithLifecycle(
        initialValue = DashboardUiState(emptyList(), emptyList(), false, null)
    )

    val activeDeliveries = uiState.activeDeliveries
    val availableDeliveries = uiState.availableDeliveries

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
                        Text("Dashboard", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("Welcome back, ready to deliver?", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    androidx.compose.material3.IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            }

            // Stats Cards
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Active",
                    value = "${activeDeliveries.size}",
                    icon = Icons.Filled.LocalShipping,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
                StatCard(
                    title = "Available",
                    value = "${availableDeliveries.size}",
                    icon = Icons.Filled.DirectionsBike,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
                StatCard(
                    title = "Completed",
                    value = "${uiState.activeDeliveries.count { it.status == DeliveryStatus.DELIVERED }}",
                    icon = Icons.Filled.Assignment,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
            }

            // Active Deliveries Section
            if (activeDeliveries.isNotEmpty()) {
                SectionHeader(
                    title = "Active Deliveries",
                    actionText = "View All",
                    onActionClick = onNavigateToAssignments
                )
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(activeDeliveries.take(3)) { delivery ->
                        ActiveDeliveryCard(
                            delivery = delivery,
                            onClick = { onNavigateToWorkflow(delivery.id) }
                        )
                    }
                }
            }

            // Available Deliveries Section
            if (availableDeliveries.isNotEmpty()) {
                SectionHeader(
                    title = "Available Deliveries",
                    actionText = "Browse All",
                    onActionClick = onNavigateToAssignments
                )
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableDeliveries.take(3)) { delivery ->
                        AvailableDeliveryCard(
                            delivery = delivery,
                            onClick = { onNavigateToWorkflow(delivery.id) }
                        )
                    }
                }
            }

            // Empty State
            if (activeDeliveries.isEmpty() && availableDeliveries.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.DirectionsBike, contentDescription = "No deliveries", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                        Text("No deliveries yet", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Text("Available deliveries will appear here", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Button(onClick = onNavigateToAssignments) {
                            Text("Find Deliveries")
                        }
                    }
                }
            }

            // Loading indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Error
            uiState.error?.let { error ->
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Card(
                        colors = androidx.compose.material3.CardDefaults.cardColors(
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
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.1f), RectangleShape)
                    .clip(RectangleShape)
            ) {
                Icon(icon, contentDescription = title, modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp), tint = color)
            }
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        androidx.compose.material3.TextButton(onClick = onActionClick) {
            Text(actionText)
        }
    }
}

@Composable
fun ActiveDeliveryCard(
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
                Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Text("Pickup", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(delivery.pickupAddress, fontSize = 14.sp, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
                Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
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
        }
    }
}

@Composable
fun AvailableDeliveryCard(
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
                StatusChip(status = delivery.status)
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Text("Pickup", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(delivery.pickupAddress, fontSize = 14.sp, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
                Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
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

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
            androidx.compose.material3.Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text("Accept Delivery", fontWeight = FontWeight.Medium)
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