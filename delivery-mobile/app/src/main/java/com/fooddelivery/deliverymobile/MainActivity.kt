package com.fooddelivery.deliverymobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavOptions
import androidx.navigation.NavOptions.Builder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.fooddelivery.deliverymobile.navigation.NavRoute
import com.fooddelivery.deliverymobile.navigation.addDeliveryMobileNavGraph
import com.fooddelivery.deliverymobile.presentation.auth.AuthViewModel
import com.fooddelivery.deliverymobile.presentation.dashboard.DashboardScreen
import com.fooddelivery.deliverymobile.presentation.assignments.AssignmentsScreen
import com.fooddelivery.deliverymobile.presentation.workflow.WorkflowScreen
import com.fooddelivery.deliverymobile.presentation.detail.DetailScreen
import com.fooddelivery.deliverymobile.presentation.history.HistoryScreen
import com.fooddelivery.deliverymobile.presentation.settings.SettingsScreen
import com.fooddelivery.deliverymobile.presentation.splash.SplashScreen
import com.fooddelivery.deliverymobile.ui.theme.FoodDeliveryDeliveryTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            FoodDeliveryDeliveryTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authState = authViewModel.uiState.collectAsStateWithLifecycle()

                    NavHost(navController, startDestination = NavRoute.SPLASH) {
                        addDeliveryMobileNavGraph(navController)
                    }
                }
            }
        }

        // Check auth status on startup
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.checkAuthStatus()
            }
        }
    }
}

// Extension function for navigate with NavOptions
fun androidx.navigation.NavController.navigate(route: String, builder: Builder.() -> Unit): Unit {
    val options = Builder().apply(builder).build()
    this.navigate(route, options)
}