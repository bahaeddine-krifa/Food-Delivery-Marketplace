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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.fooddelivery.clientmobile.domain.model.CartItem
import com.fooddelivery.clientmobile.presentation.cart.CartViewModel
import com.fooddelivery.clientmobile.ui.theme.FoodDeliveryTheme
import coil3.compose.AsyncImage
import coil3.compose.data
import coil3.request.Crossfade
import kotlinx.coroutines.launch

@Composable
fun CartScreen(
    viewModel: CartViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onNavigateToCheckout: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val cart by viewModel.cart.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("Cart", fontWeight = FontWeight.Bold) },
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

            if (cart.isEmpty) {
                EmptyCart()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    LazyColumn(
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(cart.items) { cartItem ->
                            CartItemCard(
                                cartItem = cartItem,
                                onQuantityChange = { quantity ->
                                    if (quantity <= 0) {
                                        viewModel.removeItem(cartItem.product.id, cartItem.notes)
                                    } else {
                                        viewModel.updateQuantity(cartItem.product.id, quantity, cartItem.notes)
                                    }
                                },
                                onRemove = { viewModel.removeItem(cartItem.product.id, cartItem.notes) }
                            )
                        }
                    }

                    // Cart Summary
                    CartSummary(
                        cart = cart,
                        onCheckoutClick = onNavigateToCheckout
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyCart() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.ShoppingCart,
                contentDescription = "Empty cart",
                tint = FoodDeliveryTheme.colorScheme.onSurfaceVariant
            )
            Text(text = "Your cart is empty", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Add some delicious food to get started", fontSize = 14.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant)
            Button(onClick = { /* Navigate to home */ }) {
                Text("Browse Restaurants")
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Image
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(70.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                cartItem.product.imageUrl?.let { url ->
                    AsyncImage(
                        model = coil3.compose.data(url),
                        contentDescription = cartItem.product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        crossfade = Crossfade(true)
                    )
                } ?: Box(
                    modifier = Modifier.fillMaxSize().background(FoodDeliveryTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Fastfood,
                        contentDescription = cartItem.product.name,
                        tint = FoodDeliveryTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = cartItem.product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                )

                cartItem.notes?.let {
                    Text(text = it, fontSize = 12.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant)
                }

                // Dietary tags
                if (cartItem.product.isVegetarian || cartItem.product.isVegan || cartItem.product.isGlutenFree) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (cartItem.product.isVegan) DietaryBadge("Vegan", FoodDeliveryTheme.colorScheme.secondary)
                        if (cartItem.product.isVegetarian) DietaryBadge("Veg", FoodDeliveryTheme.colorScheme.secondary)
                        if (cartItem.product.isGlutenFree) DietaryBadge("GF", FoodDeliveryTheme.colorScheme.tertiary)
                    }
                }

                // Quantity selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuantitySelector(
                        quantity = cartItem.quantity,
                        onDecrease = { onQuantityChange(cartItem.quantity - 1) },
                        onIncrease = { onQuantityChange(cartItem.quantity + 1) }
                    )
                    Text(
                        text = "$${"%.2f".format(cartItem.totalPrice)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = FoodDeliveryTheme.colorScheme.primary
                    )
                }
            }

            // Remove button
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = FoodDeliveryTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = onDecrease) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Remove,
                contentDescription = "Decrease"
            )
        }
        Text(text = quantity.toString(), fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.width(24.dp))
        IconButton(onClick = onIncrease) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Add,
                contentDescription = "Increase"
            )
        }
    }
}

@Composable
fun CartSummary(
    cart: com.fooddelivery.clientmobile.domain.model.Cart,
    onCheckoutClick: () -> Unit
) {
    val deliveryFee = 2.99 // Would come from restaurant
    val tax = cart.subtotal * 0.10
    val total = cart.subtotal + deliveryFee + tax

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Order Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            SummaryRow(label = "Subtotal", value = "$${"%.2f".format(cart.subtotal)}")
            SummaryRow(label = "Delivery", value = "$${"%.2f".format(deliveryFee)}")
            SummaryRow(label = "Tax (10%)", value = "$${"%.2f".format(tax)}")

            Divider()

            SummaryRow(
                label = "Total",
                value = "$${"%.2f".format(total)}",
                isTotal = true
            )

            Button(
                onClick = onCheckoutClick,
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = FoodDeliveryTheme.colorScheme.primary
                )
            ) {
                Text("Proceed to Checkout", fontWeight = FontWeight.Medium)
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
fun DietaryBadge(text: String, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier.padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier
                .padding(horizontal = 6.dp, vertical = 2.dp)
                .background(color.copy(alpha = 0.1f))
                .clip(RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun PreviewCartScreen() {
    FoodDeliveryTheme {
        CartScreen(
            onNavigateToCheckout = { },
            onNavigateBack = { }
        )
    }
}