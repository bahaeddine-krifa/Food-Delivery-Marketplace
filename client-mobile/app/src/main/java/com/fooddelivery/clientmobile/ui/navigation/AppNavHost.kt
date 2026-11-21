package com.fooddelivery.clientmobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.fooddelivery.clientmobile.presentation.auth.AuthViewModel
import com.fooddelivery.clientmobile.presentation.cart.CartViewModel
import com.fooddelivery.clientmobile.presentation.checkout.CheckoutViewModel
import com.fooddelivery.clientmobile.presentation.home.HomeViewModel
import com.fooddelivery.clientmobile.presentation.orderdetail.OrderDetailViewModel
import com.fooddelivery.clientmobile.presentation.orders.OrdersViewModel
import com.fooddelivery.clientmobile.presentation.profile.ProfileViewModel
import com.fooddelivery.clientmobile.presentation.restaurant.RestaurantDetailViewModel
import com.fooddelivery.clientmobile.presentation.screens.AuthScreen
import com.fooddelivery.clientmobile.presentation.screens.CartScreen
import com.fooddelivery.clientmobile.presentation.screens.CheckoutScreen
import com.fooddelivery.clientmobile.presentation.screens.HomeScreen
import com.fooddelivery.clientmobile.presentation.screens.OrderDetailScreen
import com.fooddelivery.clientmobile.presentation.screens.OrdersScreen
import com.fooddelivery.clientmobile.presentation.screens.ProfileScreen
import com.fooddelivery.clientmobile.presentation.screens.ProductDetailScreen
import com.fooddelivery.clientmobile.presentation.screens.RestaurantDetailScreen
import com.fooddelivery.clientmobile.presentation.screens.SettingsScreen
import com.fooddelivery.clientmobile.presentation.screens.SplashScreen

@Composable
fun AppNavHost(
    navController: NavController = rememberNavController(),
    startDestination: String = AppScreen.Splash.route
) {
    var isAuthenticated by remember { mutableStateOf(false) }

    NavHost(navController, startDestination) {
        composable(AppScreen.Splash.route) {
            SplashScreen(onAuthCheckComplete = { authenticated ->
                isAuthenticated = authenticated
            })
        }

        composable(AppScreen.Auth.route) {
            AuthScreen(
                viewModel = hiltViewModel(),
                onLoginSuccess = { isAuthenticated = true }
            )
        }

        composable(AppScreen.Home.route) {
            HomeScreen(
                viewModel = hiltViewModel(),
                onNavigateToRestaurant = { restaurantId ->
                    navController.navigate(AppScreen.RestaurantDetail.createRoute(restaurantId))
                },
                onNavigateToCart = {
                    navController.navigate(AppScreen.Cart.route)
                },
                onNavigateToOrders = {
                    navController.navigate(AppScreen.Orders.route)
                },
                onNavigateToProfile = {
                    navController.navigate(AppScreen.Profile.route)
                }
            )
        }

        composable(
            route = AppScreen.RestaurantDetail.routeWithArgs,
            arguments = listOf(navArgument(AppScreen.RestaurantDetail.argRestaurantId) { type = NavType.StringType })
        ) { backStackEntry ->
            val restaurantId = backStackEntry.getString()!!
            RestaurantDetailScreen(
                viewModel = hiltViewModel(),
                restaurantId = restaurantId,
                onNavigateToCart = {
                    navController.navigate(AppScreen.Cart.route)
                },
                onNavigateToProductDetail = { productId, restaurantId ->
                    navController.navigate(AppScreen.ProductDetail.createRoute(productId, restaurantId))
                }
            )
        }

        composable(
            route = AppScreen.ProductDetail.routeWithArgs,
            arguments = listOf(
                navArgument(AppScreen.ProductDetail.argProductId) { type = NavType.StringType },
                navArgument(AppScreen.ProductDetail.argRestaurantId) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.getString()!!.toLong()
            val restaurantId = backStackEntry.getString()!!.toLong()
            ProductDetailScreen(
                productId = productId,
                restaurantId = restaurantId
            )
        }

        composable(AppScreen.Cart.route) {
            CartScreen(
                viewModel = hiltViewModel(),
                onNavigateToCheckout = {
                    navController.navigate(AppScreen.Checkout.route)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppScreen.Checkout.route) {
            CheckoutScreen(
                viewModel = hiltViewModel(),
                onOrderPlaced = { orderId ->
                    navController.navigate(AppScreen.OrderDetail.createRoute(orderId))
                    navController.popBackStack(AppScreen.Home.route, inclusive = false)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppScreen.Orders.route) {
            OrdersScreen(
                viewModel = hiltViewModel(),
                onNavigateToOrderDetail = { orderId ->
                    navController.navigate(AppScreen.OrderDetail.createRoute(orderId))
                }
            )
        }

        composable(
            route = AppScreen.OrderDetail.routeWithArgs,
            arguments = listOf(navArgument(AppScreen.OrderDetail.argOrderId) { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.getString()!!
            OrderDetailScreen(
                viewModel = hiltViewModel(),
                orderId = orderId
            )
        }

        composable(AppScreen.Profile.route) {
            ProfileScreen(
                viewModel = hiltViewModel(),
                onNavigateToAddresses = {
                    navController.navigate(AppScreen.Addresses.route)
                },
                onNavigateToChangePassword = {
                    navController.navigate(AppScreen.Settings.route)
                },
                onNavigateToSettings = {
                    navController.navigate(AppScreen.Settings.route)
                },
                onLogout = { isAuthenticated = false }
            )
        }

        composable(AppScreen.Settings.route) {
            SettingsScreen(
                profileViewModel = hiltViewModel(),
                onNavigateToAddresses = {
                    navController.navigate(AppScreen.Addresses.route)
                },
                onNavigateToChangePassword = {
                    // Navigate to change password screen (could be part of settings)
                },
                onSignOut = { isAuthenticated = false }
            )
        }

        composable(AppScreen.Addresses.route) {
            AddressScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

sealed class AppScreen(val route: String) {
    object Splash : AppScreen("splash")
    object Auth : AppScreen("auth")
    object Home : AppScreen("home")
    object RestaurantDetail : AppScreen("restaurant/{restaurantId}") {
        val argRestaurantId = "restaurantId"
        val routeWithArgs = "restaurant/{$argRestaurantId}"
        fun createRoute(restaurantId: String) = "restaurant/$restaurantId"
    }
    object ProductDetail : AppScreen("product/{productId}/{restaurantId}") {
        val argProductId = "productId"
        val argRestaurantId = "restaurantId"
        val routeWithArgs = "product/{$argProductId}/{$argRestaurantId}"
        fun createRoute(productId: Long, restaurantId: Long) = "product/$productId/$restaurantId"
    }
    object Cart : AppScreen("cart")
    object Checkout : AppScreen("checkout")
    object Orders : AppScreen("orders")
    object OrderDetail : AppScreen("order/{orderId}") {
        val argOrderId = "orderId"
        val routeWithArgs = "order/{$argOrderId}"
        fun createRoute(orderId: String) = "order/$orderId"
    }
    object Profile : AppScreen("profile")
    object Addresses : AppScreen("addresses")
    object Settings : AppScreen("settings")
}