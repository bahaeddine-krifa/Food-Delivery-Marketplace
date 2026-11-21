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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import com.fooddelivery.clientmobile.domain.model.Address
import com.fooddelivery.clientmobile.domain.model.Cart
import com.fooddelivery.clientmobile.presentation.cart.CartViewModel
import com.fooddelivery.clientmobile.presentation.checkout.CheckoutViewModel
import com.fooddelivery.clientmobile.ui.theme.FoodDeliveryTheme

@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    cartViewModel: CartViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onOrderPlaced: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val addresses by viewModel.addresses.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isPlacingOrder by viewModel.isPlacingOrder.collectAsState()
    val error by viewModel.error.collectAsState()
    val customerNotes by viewModel.customerNotes.collectAsState()
    val orderResult by viewModel.orderResult.collectAsState()
    val cart by cartViewModel.cart.collectAsState()

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadAddresses()
    }

    LaunchedEffect(orderResult) {
        orderResult?.let { result ->
            when (result) {
                is Result.Success -> {
                    val order = result.getOrNull()
                    order?.let { onOrderPlaced(it.id.toString()) }
                    viewModel.clearOrderResult()
                }
                is Result.Failure -> {
                    // Error already shown
                    viewModel.clearOrderResult()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Delivery Address
                AddressSection(
                    addresses = addresses,
                    selectedAddress = selectedAddress,
                    onAddressClick = { viewModel.selectAddress(it) },
                    onAddAddressClick = { /* TODO: Navigate to add address */ }
                )

                // Payment Method (Cash on Delivery for MVP)
                PaymentSection()

                // Order Items
                OrderItemsSection(cart = cart)

                // Order Summary
                OrderSummarySection(cart = cart)

                // Notes
                NotesSection(
                    notes = customerNotes,
                    onNotesChange = { viewModel.setCustomerNotes(it) }
                )

                // Place Order Button
                Button(
                    onClick = { viewModel.placeOrder(cart) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isPlacingOrder && selectedAddress != null && !cart.isEmpty,
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = FoodDeliveryTheme.colorScheme.primary
                    )
                ) {
                    if (isPlacingOrder) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = FoodDeliveryTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Place Order", fontWeight = FontWeight.Medium)
                    }
                }

                error?.let { errorMsg ->
                    Text(
                        text = errorMsg,
                        color = FoodDeliveryTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddressSection(
    addresses: List<Address>,
    selectedAddress: Address?,
    onAddressClick: (Address) -> Unit,
    onAddAddressClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Delivery Address", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onAddAddressClick) {
                Text("Add New")
            }
        }

        if (addresses.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = FoodDeliveryTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.AddLocation,
                        contentDescription = "Add address",
                        tint = FoodDeliveryTheme.colorScheme.onSurfaceVariant
                    )
                    Text(text = "No saved addresses", fontSize = 16.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant)
                    Button(onClick = onAddAddressClick) { Text("Add Address") }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(addresses) { address ->
                    AddressCard(
                        address = address,
                        isSelected = selectedAddress?.id == address.id,
                        onClick = { onAddressClick(address) }
                    )
                }
            }
        }
    }
}

@Composable
fun AddressCard(
    address: Address,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isSelected) FoodDeliveryTheme.colorScheme.primaryContainer else FoodDeliveryTheme.colorScheme.surface,
            contentColor = if (isSelected) FoodDeliveryTheme.colorScheme.onPrimaryContainer else FoodDeliveryTheme.colorScheme.onSurface
        ),
        border = if (isSelected) androidx.compose.ui.graphics.drawscope.Stroke(2.dp, FoodDeliveryTheme.colorScheme.primary) else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = address.label, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    if (address.isDefault) {
                        androidx.compose.material3.Chip(
                            onClick = { },
                            colors = androidx.compose.material3.ChipDefaults.chipColors(
                                containerColor = FoodDeliveryTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text("Default", fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                RadioButton(
                    selected = isSelected,
                    onClick = { }
                )
            }
            Text(text = address.fullAddress, fontSize = 14.sp)
        }
    }
}

@Composable
fun PaymentSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Payment Method", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Payments,
                        contentDescription = "Cash",
                        tint = FoodDeliveryTheme.colorScheme.primary
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(text = "Cash on Delivery", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text(text = "Pay when your order arrives", fontSize = 12.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemsSection(cart: Cart) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Order Items (${cart.itemCount})", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cart.items) { cartItem ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Image
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(FoodDeliveryTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Fastfood,
                                contentDescription = cartItem.product.name,
                                tint = FoodDeliveryTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Details
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(text = cartItem.product.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text(text = "${cartItem.quantity} × $${"%.2f".format(cartItem.product.price)}", fontSize = 12.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant)
                        }

                        // Price
                        Text(text = "$${"%.2f".format(cartItem.totalPrice)}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderSummarySection(cart: Cart) {
    val deliveryFee = 2.99
    val tax = cart.subtotal * 0.10
    val total = cart.subtotal + deliveryFee + tax

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Order Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryRow(label = "Subtotal", value = "$${"%.2f".format(cart.subtotal)}")
                SummaryRow(label = "Delivery", value = "$${"%.2f".format(deliveryFee)}")
                SummaryRow(label = "Tax (10%)", value = "$${"%.2f".format(tax)}")

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
fun SummaryRow(label: String, value: String, isTotal: Boolean = false) {
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
            color = if (isTotal) FoodDeliveryTheme.colorScheme.primary else FoodDeliveryTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun NotesSection(
    notes: String,
    onNotesChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Special Instructions (Optional)", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            label = { Text("e.g., Ring doorbell, leave at door...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )
    }
}

@Composable
fun PreviewCheckoutScreen() {
    FoodDeliveryTheme {
        CheckoutScreen(
            onOrderPlaced = { },
            onNavigateBack = { }
        )
    }
}