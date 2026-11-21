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
import com.fooddelivery.clientmobile.domain.model.Restaurant
import com.fooddelivery.clientmobile.presentation.home.HomeViewModel
import com.fooddelivery.clientmobile.ui.theme.FoodDeliveryTheme
import coil3.compose.AsyncImage
import coil3.compose.data
import coil3.request.Crossfade
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onNavigateToRestaurant: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val restaurants by viewModel.restaurants.collectAsState()
    val openRestaurants by viewModel.openRestaurants.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val error by viewModel.error.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadRestaurants(refresh = true)
        viewModel.loadOpenRestaurants()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Top App Bar
            TopAppBar(
                title = { Text("FoodDelivery", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FoodDeliveryTheme.colorScheme.surface,
                    titleContentColor = FoodDeliveryTheme.colorScheme.onSurface
                ),
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        Badge(
                            badgeContent = { /* Cart count */ },
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
                    IconButton(onClick = onNavigateToOrders) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ReceiptLong,
                            contentDescription = "Orders"
                        )
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    }
                }
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Search Bar
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) }
                )

                // Open Now Section
                if (openRestaurants.isNotEmpty()) {
                    SectionHeader(
                        title = "Open Now",
                        onSeeAllClick = { /* TODO */ }
                    )
                    RestaurantHorizontalList(
                        restaurants = openRestaurants,
                        onItemClick = onNavigateToRestaurant
                    )
                }

                // Categories
                if (categories.isNotEmpty()) {
                    SectionHeader(
                        title = "Categories",
                        onSeeAllClick = { /* TODO */ }
                    )
                    CategoryChips(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategoryClick = { viewModel.setSelectedCategory(it) }
                    )
                }

                // All Restaurants
                SectionHeader(
                    title = "All Restaurants",
                    onSeeAllClick = { /* TODO */ }
                )
                RestaurantList(
                    restaurants = restaurants,
                    onItemClick = onNavigateToRestaurant,
                    isLoading = isLoading,
                    isLoadingMore = isLoadingMore,
                    onLoadMore = { viewModel.loadRestaurants(refresh = false) },
                    error = error,
                    onRetry = { viewModel.loadRestaurants(refresh = true) }
                )
            }
        }

        // Bottom loading indicator
        if (isLoadingMore) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    androidx.compose.material3.TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search restaurants, cuisines...") },
        leadingIcon = {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
            containerColor = FoodDeliveryTheme.colorScheme.surfaceVariant
        )
    )
}

@Composable
fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        TextButton(onClick = onSeeAllClick) {
            Text("See all")
        }
    }
}

@Composable
fun CategoryChips(
    categories: List<com.fooddelivery.clientmobile.domain.model.Category>,
    selectedCategory: String?,
    onCategoryClick: (String?) -> Unit
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        modifier = Modifier.height(100.dp)
    ) {
        // All category
        item {
            CategoryChip(
                name = "All",
                imageUrl = null,
                isSelected = selectedCategory == null,
                onClick = { onCategoryClick(null) }
            )
        }
        items(categories.filter { it.isActive }) { category ->
            CategoryChip(
                name = category.name,
                imageUrl = category.imageUrl,
                isSelected = selectedCategory == category.id.toString(),
                onClick = { onCategoryClick(category.id.toString()) }
            )
        }
    }
}

@Composable
fun CategoryChip(
    name: String,
    imageUrl: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxHeight()
            .width(80.dp)
            .fillMaxWidth(),
        onClick = onClick,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isSelected) FoodDeliveryTheme.colorScheme.primaryContainer else FoodDeliveryTheme.colorScheme.surface,
            contentColor = if (isSelected) FoodDeliveryTheme.colorScheme.onPrimaryContainer else FoodDeliveryTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                imageUrl?.let { url ->
                    AsyncImage(
                        model = coil3.compose.data(url),
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        crossfade = Crossfade(true)
                    )
                } ?: Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Restaurant,
                    contentDescription = name,
                    tint = if (isSelected) FoodDeliveryTheme.colorScheme.primary else FoodDeliveryTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = androidx.compose.ui.text.TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.TextAlign.Center
            )
        }
    }
}

@Composable
fun RestaurantHorizontalList(
    restaurants: List<Restaurant>,
    onItemClick: (String) -> Unit
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        modifier = Modifier.height(280.dp)
    ) {
        items(restaurants) { restaurant ->
            RestaurantCard(
                restaurant = restaurant,
                onClick = { onItemClick(restaurant.id.toString()) },
                isHorizontal = true
            )
        }
    }
}

@Composable
fun RestaurantList(
    restaurants: List<Restaurant>,
    onItemClick: (String) -> Unit,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    error: String?,
    onRetry: () -> Unit
) {
    if (isLoading && restaurants.isEmpty()) {
        LoadingList()
    } else if (error != null && restaurants.isEmpty()) {
        ErrorState(message = error, onRetry = onRetry)
    } else if (restaurants.isEmpty()) {
        EmptyState()
    } else {
        LazyColumn(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(restaurants) { restaurant ->
                RestaurantCard(
                    restaurant = restaurant,
                    onClick = { onItemClick(restaurant.id.toString()) },
                    isHorizontal = false
                )
            }
            if (isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit,
    isHorizontal: Boolean = false
) {
    val modifier = if (isHorizontal) {
        Modifier.width(260.dp).height(260.dp)
    } else {
        Modifier.fillMaxWidth().height(140.dp)
    }

    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = FoodDeliveryTheme.colorScheme.surface
        )
    ) {
        if (isHorizontal) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Image
                restaurant.logoUrl?.let { url ->
                    AsyncImage(
                        model = coil3.compose.data(url),
                        contentDescription = restaurant.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        crossfade = Crossfade(true)
                    )
                } ?: Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(FoodDeliveryTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Restaurant,
                        contentDescription = restaurant.name,
                        tint = FoodDeliveryTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Content
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = restaurant.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (restaurant.cuisineType != null) {
                            Text(
                                text = restaurant.cuisineType!!,
                                fontSize = 12.sp,
                                color = FoodDeliveryTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (restaurant.averageRating > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = FoodDeliveryTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = "%.1f".format(restaurant.averageRating),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "(${restaurant.totalReviews})",
                                    fontSize = 12.sp,
                                    color = FoodDeliveryTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (restaurant.isOpenNow) {
                            Text(
                                text = "Open Now",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = FoodDeliveryTheme.colorScheme.secondary
                            )
                        }
                    }

                    Text(
                        text = "Delivery: ${restaurant.deliveryFee.toInt() == 0 ? "Free" : "%.0f".format(restaurant.deliveryFee)} • ${restaurant.preparationTimeMinutes} min",
                        fontSize = 12.sp,
                        color = FoodDeliveryTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                // Image
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    restaurant.logoUrl?.let { url ->
                        AsyncImage(
                            model = coil3.compose.data(url),
                            contentDescription = restaurant.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            crossfade = Crossfade(true)
                        )
                    } ?: Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(FoodDeliveryTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Restaurant,
                            contentDescription = restaurant.name,
                            tint = FoodDeliveryTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Content
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = restaurant.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                    )

                    restaurant.cuisineType?.let {
                        Text(text = it, fontSize = 12.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (restaurant.averageRating > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = FoodDeliveryTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = "%.1f".format(restaurant.averageRating),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "(${restaurant.totalReviews})",
                                    fontSize = 12.sp,
                                    color = FoodDeliveryTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        if (restaurant.isOpenNow) {
                            Text(
                                text = "Open Now",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = FoodDeliveryTheme.colorScheme.secondary
                            )
                        }
                    }

                    Text(
                        text = "Delivery: ${restaurant.deliveryFee.toInt() == 0 ? "Free" : "%.0f".format(restaurant.deliveryFee)} • ${restaurant.preparationTimeMinutes} min",
                        fontSize = 12.sp,
                        color = FoodDeliveryTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingList() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(5) {
            Card(
                modifier = Modifier.fillMaxWidth().height(140.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(FoodDeliveryTheme.colorScheme.surfaceVariant))
            }
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
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.RestaurantMenu,
            contentDescription = "No restaurants",
            tint = FoodDeliveryTheme.colorScheme.onSurfaceVariant
        )
        Text(text = "No restaurants found", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = "Try adjusting your search or filters", fontSize = 14.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun PreviewHomeScreen() {
    FoodDeliveryTheme {
        HomeScreen(
            onNavigateToRestaurant = { },
            onNavigateToCart = { },
            onNavigateToOrders = { },
            onNavigateToProfile = { }
        )
    }
}