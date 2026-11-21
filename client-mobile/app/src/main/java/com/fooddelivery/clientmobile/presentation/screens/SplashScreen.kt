package com.fooddelivery.clientmobile.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.clientmobile.ui.theme.FoodDeliveryTheme

@Composable
fun SplashScreen(onAuthCheckComplete: (Boolean) -> Unit) {
    LaunchedEffect(Unit) {
        // Simulate auth check - in real app, check token validity
        Thread.sleep(1500)
        onAuthCheckComplete(false) // Navigate to auth screen for now
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App logo placeholder
            Text(
                text = "🍔",
                fontSize = 80.sp
            )

            Text(
                text = "FoodDelivery",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            CircularProgressIndicator()
        }
    }
}

@Composable
fun PreviewSplashScreen() {
    FoodDeliveryTheme {
        SplashScreen(onAuthCheckComplete = { })
    }
}