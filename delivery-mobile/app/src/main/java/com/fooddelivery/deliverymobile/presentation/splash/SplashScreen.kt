package com.fooddelivery.deliverymobile.presentation.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.deliverymobile.navigation.NavRoute
import com.fooddelivery.deliverymobile.ui.theme.FoodDeliveryDeliveryTheme
import com.fooddelivery.deliverymobile.ui.theme.LightColorScheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        delay(2000)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.STARTED)) {
            onNavigateToAuth()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.DirectionsBike,
                contentDescription = "Food Delivery",
                modifier = Modifier.size(80.dp),
                tint = LightColorScheme.primary
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))
            Text(
                text = "Food Delivery",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = LightColorScheme.primary
            )
            Text(
                text = "Delivery Partner",
                fontSize = 16.sp,
                color = LightColorScheme.onSurfaceVariant
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(32.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = LightColorScheme.primary
            )
        }
    }
}