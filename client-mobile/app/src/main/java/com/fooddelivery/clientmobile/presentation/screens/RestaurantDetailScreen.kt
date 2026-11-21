package com.fooddelivery.clientmobile.presentation.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgeDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.fooddelivery.clientmobile.domain.model.Category
import com.fooddelivery.clientmobile.domain.model.Product
import com.fooddelivery.clientmobile.domain.model.Restaurant
import com.fooddelivery.clientmobile.domain.model.RestaurantHours
import com.fooddelivery.clientmobile.presentation.cart.CartViewModel
import com.fooddelivery.clientmobile.presentation.restaurant.RestaurantDetailViewModel
import com.fooddelivery.clientmobile.ui.theme.FoodDeliveryTheme
import coil3.compose.AsyncImage
import coil3.compose.data
import coil3.request.Crossfade
import kotlinx.coroutines.launch

@Composable
fun RestaurantDetailScreen(
    viewModel: RestaurantDetailViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    cartViewModel: CartViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    restaurantId: String,
    onNavigateToCart: () -> Unit,
    onNavigateToProductDetail: ((Long, Long) -> Unit)? = null
) {
    val restaurant by viewModel.restaurant.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val products by viewModel.products.collectAsState()
    val hours by viewModel.hours.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingProducts by viewModel.isLoadingProducts.collectAsState()
    val error by viewModel.error.collectAsState()

    val scrollState = rememberScrollState()

    LaunchedEffect(restaurantId) {
        viewModel.loadRestaurant(restaurantId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Top App Bar with back button
            TopAppBar(
                title = {
                    Text(
                        text = restaurant?.name ?? "Restaurant",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Handle back navigation */ }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        Badge(
                            badgeContent = { Text(cartViewModel.cart.value.itemCount.toString()) },
                            colors = BadgeDefaults.badgeColors(
                                containerColor = FoodDeliveryTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.ShoppingCart,
                                contentDescription = "Cart"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FoodDeliveryTheme.colorScheme.surface
                )
            )

            restaurant?.let { restaurant ->
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Restaurant Header
                    RestaurantHeader(restaurant = restaurant)

                    // Hours
                    if (hours.isNotEmpty()) {
                        HoursSection(hours = hours)
                    }

                    // Categories
                    if (categories.isNotEmpty()) {
                        CategoryFilterChips(
                            categories = categories,
                            selectedCategoryId = selectedCategoryId,
                            onCategoryClick = { viewModel.setSelectedCategory(it) }
                        )
                    }

                    // Products
                    ProductsSection(
                        products = products,
                        isLoading = isLoadingProducts,
                        onAddToCart = { product, quantity, notes ->
                            cartViewModel.addItem(product, quantity, notes)
                        },
                        onLoadMore = { viewModel.loadProducts(restaurant.id.toString(), refresh = false) },
                        onNavigateToProductDetail = onNavigateToProductDetail
                    )
                }
            } ?: if (isLoading) {
                LoadingPlaceholder()
            } else if (error != null) {
                ErrorState(message = error, onRetry = { viewModel.loadRestaurant(restaurantId) })
            }
        }
    }
}

@Composable
fun RestaurantHeader(restaurant: Restaurant) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Cover Image
        restaurant.coverImageUrl?.let { url ->
            AsyncImage(
                model = coil3.compose.data(url),
                contentDescription = restaurant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                crossfade = Crossfade(true)
            )
        } ?: Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(FoodDeliveryTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Restaurant,
                contentDescription = restaurant.name,
                tint = FoodDeliveryTheme.colorScheme.onSurfaceVariant
            )
        }

        // Info
        Column(
            modifier = Modifier.padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = restaurant.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    restaurant.cuisineType?.let {
                        Text(text = it, fontSize = 16.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant)
                    }
                }

                if (restaurant.isOpenNow) {
                    Box(
                        modifier = Modifier.padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Open Now",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = FoodDeliveryTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (restaurant.averageRating > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = FoodDeliveryTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "%.1f".format(restaurant.averageRating),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "(${restaurant.totalReviews} reviews)",
                            fontSize = 14.sp,
                            color = FoodDeliveryTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoChip(
                    icon = androidx.compose.material.icons.Icons.Default.LocalShipping,
                    text = "Delivery: ${restaurant.deliveryFee.toInt() == 0 ? "Free" : "%.0f".format(restaurant.deliveryFee)}"
                )
                InfoChip(
                    icon = androidx.compose.material.icons.Icons.Default.AccessTime,
                    text = "${restaurant.preparationTimeMinutes} min"
                )
                InfoChip(
                    icon = androidx.compose.material.icons.Icons.Default.AttachMoney,
                    text = "Min: ${restaurant.minimumOrderAmount.toInt()}"
                )
            }

            restaurant.description?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = FoodDeliveryTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun InfoChip(icon: androidx.compose.material.icons.Icons.Default, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "",
            tint = FoodDeliveryTheme.colorScheme.onSurfaceVariant
        )
        Text(text = text, fontSize = 12.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun HoursSection(hours: List<RestaurantHours>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Hours", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val dayOrder = listOf(
                    com.fooddelivery.clientmobile.domain.model.DayOfWeek.MONDAY,
                    com.fooddelivery.clientmobile.domain.model.DayOfWeek.TUESDAY,
                    com.fooddelivery.clientmobile.domain.model.DayOfWeek.WEDNESDAY,
                    com.fooddelivery.clientmobile.domain.model.DayOfWeek.THURSDAY,
                    com.fooddelivery.clientmobile.domain.model.DayOfWeek.FRIDAY,
                    com.fooddelivery.clientmobile.domain.model.DayOfWeek.SATURDAY,
                    com.fooddelivery.clientmobile.domain.model.DayOfWeek.SUNDAY
                )

                dayOrder.forEach { day ->
                    hours.firstOrNull { it.dayOfWeek == day }?.let { hour ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = day.value.substring(0, 3),
                                fontSize = 14.sp,
                                fontWeight = if (day == com.fooddelivery.clientmobile.domain.model.DayOfWeek.fromJava(java.time.LocalDate.now().dayOfWeek)) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = if (hour.isClosed) "Closed" else "${hour.openTime} - ${hour.closeTime}",
                                fontSize = 14.sp,
                                color = if (hour.isClosed) FoodDeliveryTheme.colorScheme.error else FoodDeliveryTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (day == com.fooddelivery.clientmobile.domain.model.DayOfWeek.fromJava(java.time.LocalDate.now().dayOfWeek)) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        if (day != dayOrder.last()) Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryFilterChips(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategoryClick: (String?) -> Unit
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        modifier = Modifier.height(50.dp)
    ) {
        item {
            FilterChip(
                name = "All",
                isSelected = selectedCategoryId == null,
                onClick = { onCategoryClick(null) }
            )
        }
        items(categories.filter { it.isActive }) { category ->
            FilterChip(
                name = category.name,
                isSelected = selectedCategoryId == category.id.toString(),
                onClick = { onCategoryClick(category.id.toString()) }
            )
        }
    }
}

@Composable
fun FilterChip(name: String, isSelected: Boolean, onClick: () -> Unit) {
    androidx.compose.material3.Chip(
        onClick = onClick,
        selected = isSelected,
        colors = androidx.compose.material3.ChipDefaults.chipColors(
            containerColor = if (isSelected) FoodDeliveryTheme.colorScheme.primaryContainer else FoodDeliveryTheme.colorScheme.surfaceVariant,
            selectedContainerColor = FoodDeliveryTheme.colorScheme.primaryContainer
        )
    ) {
        Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ProductsSection(
    products: List<Product>,
    isLoading: Boolean,
    onAddToCart: (Product, Int, String?) -> Unit,
    onLoadMore: () -> Unit,
    onNavigateToProductDetail: ((Long, Long) -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (products.isEmpty() && !isLoading) {
            EmptyProducts()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products) { product ->
                    ProductCard(
                        product = product,
                        onAddToCart = { quantity, notes -> onAddToCart(product, quantity, notes) },
                        onProductClick = onNavigateToProductDetail?.let { navigate ->
                            { navigate(product.id, restaurant.id.toLong()) }
                        }
                    )
                }
                if (isLoading) {
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

@Composable
fun ProductCard(
    product: Product,
    onAddToCart: (Int, String?) -> Unit,
    onProductClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick?.invoke() }
            .combinedClickable(
                onClick = { onProductClick?.invoke() },
                onLongClick = { /* handle long click */ }
            ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Image
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                product.imageUrl?.let { url ->
                    AsyncImage(
                        model = coil3.compose.data(url),
                        contentDescription = product.name,
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
                        contentDescription = product.name,
                        tint = FoodDeliveryTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = product.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$${"%.2f".format(product.price)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = FoodDeliveryTheme.colorScheme.primary
                    )
                }

                product.description?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = FoodDeliveryTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                    )
                }

                // Tags
                if (product.tags.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        product.tags.take(3).forEach { tag ->
                            Chip(
                                label = { Text(tag, fontSize = 10.sp) },
                                colors = androidx.compose.material3.ChipDefaults.chipColors(
                                    containerColor = FoodDeliveryTheme.colorScheme.tertiaryContainer
                                )
                            )
                        }
                    }
                }

                if (product.isVegetarian || product.isVegan || product.isGlutenFree) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (product.isVegan) DietaryBadge("Vegan", FoodDeliveryTheme.colorScheme.secondary)
                        if (product.isVegetarian) DietaryBadge("Veg", FoodDeliveryTheme.colorScheme.secondary)
                        if (product.isGlutenFree) DietaryBadge("GF", FoodDeliveryTheme.colorScheme.tertiary)
                    }
                }

                // Add to cart button
                Button(
                    onClick = { onAddToCart(1, null) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = FoodDeliveryTheme.colorScheme.primary
                    )
                ) {
                    Text("Add to Cart", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun DietaryBadge(text: String, color: Color) {
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
fun EmptyProducts() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.Fastfood,
            contentDescription = "No products",
            tint = FoodDeliveryTheme.colorScheme.onSurfaceVariant
        )
        Text(text = "No products in this category", fontSize = 16.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun LoadingPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(3) {
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

@Composable
fun PreviewRestaurantDetailScreen() {
    FoodDeliveryTheme {
        RestaurantDetailScreen(
            restaurantId = "1",
            onNavigateToCart = { }
        )
    }
}