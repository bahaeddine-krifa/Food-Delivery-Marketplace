package com.fooddelivery.deliverymobile.presentation.workflow

import androidx.compose.foundation.background
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fooddelivery.deliverymobile.navigation.NavRoute
import com.fooddelivery.deliverymobile.ui.theme.FoodDeliveryDeliveryTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkflowScreen(
    deliveryId: java.util.UUID,
    onNavigateToDetail: (java.util.UUID) -> Unit,
    onNavigateBack: () -> Unit
) {
    val viewModel: WorkflowViewModel = hiltViewModel()
    val uiState by viewModel.workflowUiState.collectAsStateWithLifecycle(initialValue = WorkflowUiState(null, null, false, false, null, null, false, false, false, false, false))
    val delivery = uiState.delivery
    val orderSummary = uiState.orderSummary

    var showFailDialog by remember { mutableStateOf(false) }
    var failReason by remember { mutableStateOf("") }

    LaunchedEffect(deliveryId) {
        viewModel.loadDelivery(deliveryId)
    }

    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            // Show success toast/snackbar
            // For now just clear after delay
            delay(3000)
            viewModel.clearMessages()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
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
                    androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                        Text("Delivery Workflow", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        delivery?.let {
                            Text("Order #${it.orderNumber}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    androidx.compose.material3.IconButton(onClick = { onNavigateToDetail(deliveryId) }) {
                        Icon(Icons.Filled.Map, contentDescription = "Details")
                    }
                }
            }

            delivery?.let { delivery ->
                // Status Progress Indicator
                StatusProgressIndicator(currentStatus = delivery.status)

                // Main Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Order Info Card
                    OrderInfoCard(orderSummary = orderSummary, delivery = delivery)

                    // Pickup / Delivery Addresses
                    AddressSection(delivery = delivery)

                    // Action Buttons based on status
                    ActionButtonsSection(
                        delivery = delivery,
                        uiState = uiState,
                        onAccept = { viewModel.acceptDelivery { success -> if (success) viewModel.loadDelivery(deliveryId) } },
                        onPickUp = { lat, lng, photo ->
                            viewModel.pickUpDelivery(lat, lng, photo) { success -> if (success) viewModel.loadDelivery(deliveryId) }
                        },
                        onStartDelivering = { lat, lng ->
                            viewModel.startDelivering(lat, lng) { success -> if (success) viewModel.loadDelivery(deliveryId) }
                        },
                        onComplete = { lat, lng, photo, signature ->
                            viewModel.completeDelivery(lat, lng, photo, signature) { success -> if (success) viewModel.loadDelivery(deliveryId) }
                        },
                        onFail = { reason ->
                            viewModel.failDelivery(reason) { success -> if (success) viewModel.loadDelivery(deliveryId) }
                        },
                        onShowFailDialog = { showFailDialog = true }
                    )

                    // Verification Codes
                    VerificationCodesCard(delivery = delivery)
                }
            }

            // Loading overlay
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

    // Fail Dialog
    if (showFailDialog) {
        FailReasonDialog(
            onConfirm = { reason ->
                viewModel.failDelivery(reason) { success ->
                    showFailDialog = false
                    failReason = ""
                    if (success) viewModel.loadDelivery(deliveryId)
                }
            },
            onDismiss = { showFailDialog = false },
            reason = failReason,
            onReasonChange = { failReason = it }
        )
    }
}

// Helper functions for status display
fun getStatusColor(status: com.fooddelivery.deliverymobile.domain.model.DeliveryStatus): Int = when (status) {
    com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.PENDING_ASSIGNMENT -> 0xFF9E9E9E.toInt()
    com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ASSIGNED -> 0xFF2196F3.toInt()
    com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ACCEPTED -> 0xFF4CAF50.toInt()
    com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.PICKED_UP -> 0xFFFF9800.toInt()
    com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERING -> 0xFF3F51B5.toInt()
    com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERED -> 0xFF4CAF50.toInt()
    com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.CANCELLED -> 0xFF9E9E9E.toInt()
    com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.FAILED -> 0xFFF44336.toInt()
}

fun getStatusDisplayName(status: com.fooddelivery.deliverymobile.domain.model.DeliveryStatus): String = when (status) {
    com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.PENDING_ASSIGNMENT -> "Pending Assignment"
    com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ASSIGNED -> "Assigned"
    com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ACCEPTED -> "Accepted"
    com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.PICKED_UP -> "Picked Up"
    com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERING -> "Delivering"
    com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERED -> "Delivered"
    com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.CANCELLED -> "Cancelled"
    com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.FAILED -> "Failed"
}

@Composable
fun StatusProgressIndicator(currentStatus: com.fooddelivery.deliverymobile.domain.model.DeliveryStatus) {
    val statuses = listOf(
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ASSIGNED,
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ACCEPTED,
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.PICKED_UP,
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERING,
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERED
    )

    val currentIndex = statuses.indexOf(currentStatus)
    val isCompleted = currentStatus == com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERED ||
                       currentStatus == com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.CANCELLED ||
                       currentStatus == com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.FAILED

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            statuses.forEachIndexed { index, status ->
                val isActive = index <= currentIndex || isCompleted
                val isCurrent = index == currentIndex && !isCompleted
                val colorInt = if (isActive) getStatusColor(status) else MaterialTheme.colorScheme.outlineVariant.copy().toArgb()

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Circle with line
                    Box(
                        modifier = Modifier.size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Line behind (except last)
                        if (index < statuses.size - 1) {
                            Box(
                                modifier = Modifier
                                    .size(2.dp, 16.dp)
                                    .align(Alignment.TopCenter)
                                    .padding(top = 16.dp)
                                    .background(if (index < currentIndex) Color(getStatusColor(statuses[index + 1])) else MaterialTheme.colorScheme.outlineVariant, RectangleShape)
                            )
                        }
                        // Circle
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(colorInt), RectangleShape)
                        ) {
                            if (isActive && index <= currentIndex) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Completed",
                                    modifier = Modifier.size(16.dp).padding(4.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Text(
                        text = getStatusDisplayName(status),
                        fontSize = 10.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isActive) Color(colorInt) else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun OrderInfoCard(
    orderSummary: com.fooddelivery.deliverymobile.domain.model.OrderSummary?,
    delivery: com.fooddelivery.deliverymobile.domain.model.Delivery
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
                Text("Order Details", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                StatusChip(status = delivery.status, color = getStatusColor(delivery.status))
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(12.dp))

            orderSummary?.let { order ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoRow(label = "Order", value = "#${order.orderNumber}")
                    InfoRow(label = "Customer", value = order.customerName)
                    InfoRow(label = "Phone", value = order.customerPhone, icon = Icons.Filled.Call, onIconClick = { /* Call */ })
                    InfoRow(label = "Restaurant", value = order.restaurantName)
                    InfoRow(label = "Phone", value = order.restaurantPhone, icon = Icons.Filled.Call, onIconClick = { /* Call */ })
                    InfoRow(label = "Total", value = "₹${order.totalAmount}", isBold = true)
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onIconClick: (() -> Unit)? = null,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(value, fontSize = 14.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
            icon?.let { i ->
                androidx.compose.material3.IconButton(onClick = onIconClick!!) {
                    Icon(i, contentDescription = label)
                }
            }
        }
    }
}

@Composable
fun AddressSection(delivery: com.fooddelivery.deliverymobile.domain.model.Delivery) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Pickup Address
        Card(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.DirectionsBike, contentDescription = "Pickup", tint = MaterialTheme.colorScheme.primary)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))
                    Text("PICKUP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Text(delivery.pickupAddress, fontSize = 14.sp, textAlign = TextAlign.Center, maxLines = 3, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                androidx.compose.material3.Button(
                    onClick = { /* Open maps */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Navigate", fontSize = 12.sp)
                }
            }
        }

        // Delivery Address
        Card(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = "Delivery", tint = MaterialTheme.colorScheme.secondary)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))
                    Text("DELIVERY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                }
                Text(delivery.deliveryAddress, fontSize = 14.sp, textAlign = TextAlign.Center, maxLines = 3, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                androidx.compose.material3.Button(
                    onClick = { /* Open maps */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Navigate", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ActionButtonsSection(
    delivery: com.fooddelivery.deliverymobile.domain.model.Delivery,
    uiState: com.fooddelivery.deliverymobile.presentation.workflow.WorkflowUiState,
    onAccept: () -> Unit,
    onPickUp: (Double, Double, String?) -> Unit,
    onStartDelivering: (Double, Double) -> Unit,
    onComplete: (Double, Double, String?, String?) -> Unit,
    onFail: (String) -> Unit,
    onShowFailDialog: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (delivery.status) {
            com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ASSIGNED -> {
                Button(
                    onClick = onAccept,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isActionLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (uiState.isActionLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                    else Text("Accept Delivery", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ACCEPTED -> {
                Button(
                    onClick = { onPickUp(0.0, 0.0, null) }, // In real app, get GPS coordinates
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isActionLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    if (uiState.isActionLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.size(20.dp))
                    else Text("Mark as Picked Up", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.PICKED_UP -> {
                Button(
                    onClick = { onStartDelivering(0.0, 0.0) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isActionLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    if (uiState.isActionLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onTertiary, modifier = Modifier.size(20.dp))
                    else Text("Start Delivering", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERING -> {
                Button(
                    onClick = { onComplete(0.0, 0.0, null, null) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isActionLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (uiState.isActionLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                    else Text("Complete Delivery", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            else -> Unit
        }

        // Fail button (available for all active statuses)
        if (delivery.canFail) {
            OutlinedButton(
                onClick = onShowFailDialog,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isActionLoading,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Report Issue / Fail Delivery", fontSize = 14.sp)
            }
        }

        // Completed states
        when (delivery.status) {
            com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERED -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = "Delivered", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                        Text("Delivery Completed!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Thank you for delivering this order", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
            com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.FAILED -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Failed", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.error)
                        Text("Delivery Failed", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Text("This delivery was marked as failed", fontSize = 14.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }
            com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.CANCELLED -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Delivery Cancelled", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("This delivery was cancelled", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            else -> Unit
        }
    }
}

@Composable
fun VerificationCodesCard(delivery: com.fooddelivery.deliverymobile.domain.model.Delivery) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Verification Codes", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Pickup Code", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(delivery.pickupCode, fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    Text("Share with restaurant", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(80.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant, shape = RectangleShape)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text("Delivery Code", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(delivery.deliveryCode, fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    Text("Collect from customer", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Composable
fun FailReasonDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    reason: String,
    onReasonChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { onConfirm(reason) },
                enabled = reason.isNotBlank()
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Report Delivery Issue") },
        text = {
            Column(modifier = Modifier.padding(8.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Please provide a reason for failing this delivery:")
                TextField(
                    value = reason,
                    onValueChange = onReasonChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Reason") },
                    maxLines = 3,
                    singleLine = false
                )
            }
        }
    )
}

@Composable
fun StatusChip(status: com.fooddelivery.deliverymobile.domain.model.DeliveryStatus, color: Int) {
    // Simple text-based status indicator instead of Chip
    Box(
        modifier = Modifier
            .padding(4.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(Color(color).copy(alpha = 0.15f), shape = RectangleShape)
    ) {
        Text(
            text = getStatusDisplayName(status),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(color)
        )
    }
}