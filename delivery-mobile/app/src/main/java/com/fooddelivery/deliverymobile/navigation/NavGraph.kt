package com.fooddelivery.deliverymobile.navigation

import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fooddelivery.deliverymobile.presentation.auth.AuthScreen
import com.fooddelivery.deliverymobile.presentation.dashboard.DashboardScreen
import com.fooddelivery.deliverymobile.presentation.assignments.AssignmentsScreen
import com.fooddelivery.deliverymobile.presentation.workflow.WorkflowScreen
import com.fooddelivery.deliverymobile.presentation.detail.DetailScreen
import com.fooddelivery.deliverymobile.presentation.history.HistoryScreen
import com.fooddelivery.deliverymobile.presentation.splash.SplashScreen
import com.fooddelivery.deliverymobile.presentation.settings.SettingsScreen
import java.util.UUID

object NavRoute {
    const val SPLASH = "splash"
    const val AUTH = "auth"
    const val MAIN = "main"
    const val DASHBOARD = "dashboard"
    const val ASSIGNMENTS = "assignments"
    const val WORKFLOW = "workflow/{deliveryId}"
    const val DETAIL = "detail/{deliveryId}"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
}

fun NavGraphBuilder.addDeliveryMobileNavGraph(
    navController: NavController
) {
    composable(route = NavRoute.SPLASH) {
        SplashScreen(
            onNavigateToAuth = { navController.navigate(NavRoute.AUTH) },
            onNavigateToMain = { navController.navigate(NavRoute.MAIN) }
        )
    }

    composable(route = NavRoute.AUTH) {
        AuthScreen(onLoginSuccess = { navController.navigate(NavRoute.MAIN) })
    }

    navigation(
        startDestination = NavRoute.DASHBOARD,
        route = NavRoute.MAIN
    ) {
        composable(route = NavRoute.DASHBOARD) {
            DashboardScreen(
                onNavigateToAssignments = { navController.navigate(NavRoute.ASSIGNMENTS) },
                onNavigateToWorkflow = { deliveryId -> navController.navigate("${NavRoute.WORKFLOW.replace("{deliveryId}", deliveryId.toString())}") },
                onNavigateToHistory = { navController.navigate(NavRoute.HISTORY) },
                onNavigateToSettings = { navController.navigate(NavRoute.SETTINGS) },
                onLogout = {
                    // Handle logout navigation
                    navController.navigate(NavRoute.AUTH)
                }
            )
        }

        composable(route = NavRoute.ASSIGNMENTS) {
            AssignmentsScreen(
                onNavigateToWorkflow = { deliveryId -> navController.navigate("${NavRoute.WORKFLOW.replace("{deliveryId}", deliveryId.toString())}") },
                onNavigateToDetail = { deliveryId -> navController.navigate("${NavRoute.DETAIL.replace("{deliveryId}", deliveryId.toString())}") }
            )
        }

        composable(
            route = NavRoute.WORKFLOW
        ) { backStackEntry ->
            val deliveryId = UUID.fromString(backStackEntry.arguments?.getString("deliveryId")!!)
            WorkflowScreen(
                deliveryId = deliveryId,
                onNavigateToDetail = { deliveryId -> navController.navigate("${NavRoute.DETAIL.replace("{deliveryId}", deliveryId.toString())}") },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoute.DETAIL
        ) { backStackEntry ->
            val deliveryId = UUID.fromString(backStackEntry.arguments?.getString("deliveryId")!!)
            DetailScreen(
                deliveryId = deliveryId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = NavRoute.HISTORY) {
            HistoryScreen(onNavigateToDetail = { deliveryId -> navController.navigate("${NavRoute.DETAIL.replace("{deliveryId}", deliveryId.toString())}") })
        }

        composable(route = NavRoute.SETTINGS) {
            SettingsScreen(onLogout = {
                navController.navigate(NavRoute.AUTH)
            })
        }
    }
}