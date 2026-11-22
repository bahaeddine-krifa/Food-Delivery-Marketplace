package com.fooddelivery.deliverymobile.presentation.detail

import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fooddelivery.deliverymobile.navigation.NavRoute
import com.fooddelivery.deliverymobile.ui.theme.FoodDeliveryDeliveryTheme
import com.fooddelivery.deliverymobile.domain.model.DeliveryStatus
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(26)
@Composable
fun DetailScreen(
    deliveryId: java.util.UUID,
    onNavigateBack: () -> Unit
) {
    val viewModel: DetailViewModel = hiltViewModel()
    val uiState by viewModel.detailUiState.collectAsStateWithLifecycle()
    val delivery = uiState.delivery
    val orderSummary = uiState.orderSummary

    LaunchedEffect(deliveryId) {
        viewModel.loadDelivery(deliveryId)
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    ) {
                        Text("Delivery Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        delivery?.let {
                            Text("Order #${it.orderNumber}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            delivery?.let { delivery ->
                // Status Progress Indicator
                StatusProgressIndicator(currentStatus = delivery.status)

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Order Info Card
                    OrderInfoCard(orderSummary = orderSummary, delivery = delivery)

                    // Addresses
                    AddressSection(delivery = delivery)

                    // Timeline
                    TimelineSection(delivery = delivery)

                    // Verification Codes
                    VerificationCodesCard(delivery = delivery)

                    // Photos
                    PhotosSection(delivery = delivery)
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
                val color = if (isActive) getStatusColor(status) else MaterialTheme.colorScheme.outlineVariant.value.toInt()

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier.size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (index < statuses.size - 1) {
                            Box(
                                modifier = Modifier
                                    .size(2.dp, 16.dp)
                                    .align(Alignment.TopCenter)
                                    .padding(top = 16.dp)
                                    .background(if (index < currentIndex) androidx.compose.ui.graphics.Color(getStatusColor(statuses[index + 1])) else MaterialTheme.colorScheme.outlineVariant, RectangleShape)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(androidx.compose.ui.graphics.Color(color), RectangleShape)
                                .clip(RectangleShape)
                        ) {
                            if (isActive && index <= currentIndex) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = "Completed", modifier = Modifier.size(16.dp).padding(4.dp), tint = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Text(text = "${index + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    Text(text = getStatusDisplayName(status), fontSize = 10.sp, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal, color = if (isActive) androidx.compose.ui.graphics.Color(color) else MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

fun getStatusColor(status: com.fooddelivery.deliverymobile.domain.model.DeliveryStatus): Int {
    return when (status) {
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.PENDING_ASSIGNMENT -> 0xFF9E9E9E.toInt()
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ASSIGNED -> 0xFF2196F3.toInt()
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ACCEPTED -> 0xFF4CAF50.toInt()
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.PICKED_UP -> 0xFFFF9800.toInt()
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERING -> 0xFF3F51B5.toInt()
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERED -> 0xFF4CAF50.toInt()
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.CANCELLED -> 0xFF9E9E9E.toInt()
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.FAILED -> 0xFFF44336.toInt()
    }
}

fun getStatusDisplayName(status: com.fooddelivery.deliverymobile.domain.model.DeliveryStatus): String {
    return when (status) {
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.PENDING_ASSIGNMENT -> "Pending Assignment"
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ASSIGNED -> "Assigned"
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ACCEPTED -> "Accepted"
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.PICKED_UP -> "Picked Up"
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERING -> "Delivering"
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERED -> "Delivered"
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.CANCELLED -> "Cancelled"
        com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.FAILED -> "Failed"
    }
}

@Composable
fun OrderInfoCard(
    orderSummary: com.fooddelivery.deliverymobile.domain.model.OrderSummary?,
    delivery: com.fooddelivery.deliverymobile.domain.model.Delivery
) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Order Details", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                StatusChip(status = delivery.status)
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(12.dp))
            orderSummary?.let { order ->
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoRow(label = "Order", value = "#${order.orderNumber}")
                    InfoRow(label = "Customer", value = order.customerName)
                    InfoRow(label = "Phone", value = order.customerPhone, icon = Icons.Filled.Call, onIconClick = {})
                    InfoRow(label = "Restaurant", value = order.restaurantName)
                    InfoRow(label = "Phone", value = order.restaurantPhone, icon = Icons.Filled.Call, onIconClick = {})
                    InfoRow(label = "Total", value = "₹${order.totalAmount}", isBold = true)
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector? = null, onIconClick: (() -> Unit)? = null, isBold: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(value, fontSize = 14.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
            icon?.let { i -> androidx.compose.material3.IconButton(onClick = onIconClick!!) { Icon(i, contentDescription = label) } }
        }
    }
}

@Composable
fun AddressSection(delivery: com.fooddelivery.deliverymobile.domain.model.Delivery) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Filled.LocalShipping, contentDescription = "Pickup", tint = MaterialTheme.colorScheme.primary)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))
                    Text("PICKUP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Text(delivery.pickupAddress, fontSize = 14.sp, textAlign = TextAlign.Center, maxLines = 3, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Navigate", fontSize = 12.sp) }
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Filled.LocationOn, contentDescription = "Delivery", tint = MaterialTheme.colorScheme.secondary)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))
                    Text("DELIVERY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                }
                Text(delivery.deliveryAddress, fontSize = 14.sp, textAlign = TextAlign.Center, maxLines = 3, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Navigate", fontSize = 12.sp) }
            }
        }
    }
}

@RequiresApi(26)
@Composable
fun TimelineSection(delivery: com.fooddelivery.deliverymobile.domain.model.Delivery) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Timeline", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(12.dp))

            val events = buildTimelineEvents(delivery)
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                events.forEachIndexed { index, event ->
                    TimelineItem(
                        title = event.title,
                        time = event.time,
                        description = event.description,
                        isCompleted = event.isCompleted,
                        isCurrent = event.isCurrent,
                        isLast = index == events.lastIndex
                    )
                }
            }
        }
    }
}

data class TimelineEvent(
    val title: String,
    val time: java.time.OffsetDateTime?,
    val description: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

fun buildTimelineEvents(delivery: com.fooddelivery.deliverymobile.domain.model.Delivery): List<TimelineEvent> {
    val events = mutableListOf<TimelineEvent>()

    events.add(TimelineEvent(
        title = "Created",
        time = delivery.createdAt,
        description = "Delivery request created",
        isCompleted = true,
        isCurrent = false
    ))

    delivery.assignedAt?.let {
        events.add(TimelineEvent(
            title = "Assigned",
            time = it,
            description = "Assigned to delivery partner",
            isCompleted = true,
            isCurrent = delivery.status == com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ASSIGNED
        ))
    }

    delivery.acceptedAt?.let {
        events.add(TimelineEvent(
            title = "Accepted",
            time = it,
            description = "Delivery accepted",
            isCompleted = true,
            isCurrent = delivery.status == com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.ACCEPTED
        ))
    }

    delivery.pickedUpAt?.let {
        events.add(TimelineEvent(
            title = "Picked Up",
            time = it,
            description = "Order picked up from restaurant",
            isCompleted = true,
            isCurrent = delivery.status == com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.PICKED_UP
        ))
    }

    // Delivering (no specific timestamp in model, infer from pickedUpAt)
    if (delivery.status == com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERING) {
        events.add(TimelineEvent(
            title = "Delivering",
            time = delivery.pickedUpAt,
            description = "On the way to customer",
            isCompleted = false,
            isCurrent = true
        ))
    }

    delivery.deliveredAt?.let {
        events.add(TimelineEvent(
            title = "Delivered",
            time = it,
            description = "Order delivered to customer",
            isCompleted = true,
            isCurrent = delivery.status == com.fooddelivery.deliverymobile.domain.model.DeliveryStatus.DELIVERED
        ))
    }

    return events
}

@RequiresApi(26)
@Composable
fun TimelineItem(
    title: String,
    time: java.time.OffsetDateTime?,
    description: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Timeline line and dot
        Column(
            modifier = Modifier.padding(top = 4.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Box(
                modifier = Modifier.size(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!isLast) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .fillMaxSize()
                            .align(Alignment.TopCenter)
                            .background(if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, RectangleShape)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            if (isCompleted) MaterialTheme.colorScheme.primary
                            else if (isCurrent) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.surface,
                            RectangleShape
                        )
                        .clip(RectangleShape)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, fontSize = 14.sp, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal, color = if (isCompleted || isCurrent) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
            time?.let { t ->
                Text(formatTime(t), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@RequiresApi(26)
fun formatTime(dateTime: java.time.OffsetDateTime): String {
    return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, HH:mm"))
}

@Composable
fun VerificationCodesCard(delivery: com.fooddelivery.deliverymobile.domain.model.Delivery) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Verification Codes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                    Text("Pickup Code", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(delivery.pickupCode, fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    Text("Share with restaurant", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.outlineVariant, RectangleShape)
                )
                Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                    Text("Delivery Code", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(delivery.deliveryCode, fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    Text("Collect from customer", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Composable
fun PhotosSection(delivery: com.fooddelivery.deliverymobile.domain.model.Delivery) {
    val hasPhotos = delivery.pickupPhotoUrl != null || delivery.deliveryPhotoUrl != null || delivery.customerSignatureUrl != null
    if (!hasPhotos) return

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Photos & Signature", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                delivery.pickupPhotoUrl?.let { url ->
                    PhotoCard(title = "Pickup Photo", imageUrl = url, modifier = Modifier.fillMaxWidth().weight(1f))
                }
                delivery.deliveryPhotoUrl?.let { url ->
                    PhotoCard(title = "Delivery Photo", imageUrl = url, modifier = Modifier.fillMaxWidth().weight(1f))
                }
                delivery.customerSignatureUrl?.let { url ->
                    PhotoCard(title = "Customer Signature", imageUrl = url, modifier = Modifier.fillMaxWidth().weight(1f))
                }
            }
        }
    }
}

@Composable
fun PhotoCard(title: String, imageUrl: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest, RectangleShape)
                .clip(RectangleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = com.fooddelivery.deliverymobile.R.drawable.ic_launcher_foreground),
            error = painterResource(id = com.fooddelivery.deliverymobile.R.drawable.ic_launcher_foreground)
        )
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