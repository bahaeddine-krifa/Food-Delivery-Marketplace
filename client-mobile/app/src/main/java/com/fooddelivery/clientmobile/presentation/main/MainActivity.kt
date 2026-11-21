package com.fooddelivery.clientmobile.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.fooddelivery.clientmobile.domain.model.Cart
import com.fooddelivery.clientmobile.presentation.auth.AuthViewModel
import com.fooddelivery.clientmobile.presentation.cart.CartViewModel
import com.fooddelivery.clientmobile.ui.navigation.AppNavHost
import com.fooddelivery.clientmobile.ui.theme.FoodDeliveryTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by hiltViewModel()
    private val cartViewModel: CartViewModel by hiltViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodDeliveryTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }

        // Observe auth state changes
        lifecycleScope.launch {
            authViewModel.isAuthenticated.collect { isAuthenticated ->
                // Navigation handled by AppNavHost
            }
        }
    }
}