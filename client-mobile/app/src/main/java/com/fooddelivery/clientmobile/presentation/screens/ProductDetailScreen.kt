package com.fooddelivery.clientmobile.presentation.screens

import android.content.Context as LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.transform.RoundedCornersTransformation
import com.fooddelivery.clientmobile.domain.model.Product
import com.fooddelivery.clientmobile.presentation.cart.CartViewModel
import com.fooddelivery.clientmobile.presentation.restaurant.RestaurantDetailViewModel
import com.fooddelivery.clientmobile.ui.theme.FoodDeliveryTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Long,
    restaurantId: Long,
    restaurantDetailViewModel: RestaurantDetailViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val productState by restaurantDetailViewModel.productDetailState.collectAsStateWithLifecycle()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(productId) {
        restaurantDetailViewModel.loadProductDetail(productId)
    }

    val showAddedToCart = productState is ProductDetailState.Success && productState.product.id == productId && productState.product.isInCart

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Product Detail") },
                navigationIcon = {
                    IconButton(onClick = { /* navigate back */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FoodDeliveryTheme.colorScheme.surfaceContainerLow
                )
            )
        },
        floatingActionButton = {
            if (productState is ProductDetailState.Success) {
                val product = productState.product
                val isInCart = product.isInCart
                FloatingActionButton(
                    onClick = {
                        if (isInCart) {
                            cartViewModel.updateQuantity(product.id, product.cartQuantity + 1)
                        } else {
                            cartViewModel.addItem(product.toCartItem())
                        }
                        scaffoldState.snackbarHostState.showSnackbar("Added to cart")
                    },
                    containerColor = if (isInCart) FoodDeliveryTheme.colorScheme.secondaryContainer else FoodDeliveryTheme.colorScheme.primaryContainer,
                    contentColor = if (isInCart) FoodDeliveryTheme.colorScheme.onSecondaryContainer else FoodDeliveryTheme.colorScheme.onPrimaryContainer
                ) {
                    Text(if (isInCart) "Update Cart" else "Add to Cart")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            when (productState) {
                is ProductDetailState.Loading -> {
                    CenteredCircularProgress()
                }
                is ProductDetailState.Error -> {
                    CenteredError(message = productState.message) {
                        restaurantDetailViewModel.loadProductDetail(productId)
                    }
                }
                is ProductDetailState.Success -> {
                    ProductDetailContent(product = productState.product)
                }
            }
        }
    }

    if (showAddedToCart) {
        LaunchedEffect(Unit) {
            try {
                scaffoldState.snackbarHostState.showSnackbar("Added to cart!")
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}

@Composable
fun ProductDetailContent(product: Product) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Product Image
        product.imageUrl?.let { imageUrl ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .transformations(RoundedCornersTransformation(16.dp))
                    .build(),
                contentDescription = product.name,
                contentScale = androidx.compose.foundation.layout.ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(androidx.compose.ui.graphics.RoundedCornerShape(16.dp))
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Name and Rating
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            RatingBadge(rating = product.rating)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Category and Dietary Tags
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Category would need to be passed separately or fetched
            if (product.dietaryTags.isNotEmpty()) {
                DietaryTagsRow(tags = product.dietaryTags)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Description
        product.description?.let { desc ->
            Text(
                text = desc,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Price
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Price",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = String.format("%.2f", product.price),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Availability
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Availability",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Badge(
                label = {
                    Text(
                        if (product.isAvailable) "Available" else "Unavailable",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = if (product.isAvailable)
                    BadgeDefaults.badgeColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                else
                    BadgeDefaults.badgeColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Preparation Time
        product.preparationTimeMinutes?.let { prepTime ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Preparation Time",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${prepTime} min",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun RatingBadge(rating: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = String.format("%.1f", rating),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DietaryTagsRow(tags: List<String>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            Badge(
                label = {
                    Text(
                        text = tag.uppercase(),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = BadgeDefaults.badgeColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenteredCircularProgress() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenteredError(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}