package com.fooddelivery.clientmobile.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fooddelivery.clientmobile.presentation.profile.ProfileViewModel
import com.fooddelivery.clientmobile.ui.theme.FoodDeliveryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    profileViewModel: ProfileViewModel = viewModel(),
    onNavigateToAddresses: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onSignOut: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { /* navigate back */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FoodDeliveryTheme.colorScheme.surfaceContainerLow
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            // Account Section
            SettingsSection(
                title = "Account",
                items = listOf(
                    SettingItem(
                        title = "My Addresses",
                        subtitle = "Manage delivery addresses",
                        icon = Icons.Default.LocationOn,
                        onClick = onNavigateToAddresses
                    ),
                    SettingItem(
                        title = "Change Password",
                        subtitle = "Update your password",
                        icon = Icons.Default.Lock,
                        onClick = onNavigateToChangePassword
                    )
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Notifications Section
            SettingsSection(
                title = "Notifications",
                items = listOf(
                    SettingItem(
                        title = "Push Notifications",
                        subtitle = "Order updates, promotions, and more",
                        icon = Icons.Default.Notifications,
                        trailing = {
                            Switch(
                                checked = true,
                                onCheckedChange = { /* handle change */ },
                                colors = SwitchDefaults.colors(
                                    thumbColor = FoodDeliveryTheme.colorScheme.primary,
                                    trackColor = FoodDeliveryTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    ),
                    SettingItem(
                        title = "Email Notifications",
                        subtitle = "Receive emails for orders and promotions",
                        icon = Icons.Default.Email,
                        trailing = {
                            Switch(
                                checked = true,
                                onCheckedChange = { /* handle change */ },
                                colors = SwitchDefaults.colors(
                                    thumbColor = FoodDeliveryTheme.colorScheme.primary,
                                    trackColor = FoodDeliveryTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    ),
                    SettingItem(
                        title = "SMS Notifications",
                        subtitle = "Receive SMS for order updates",
                        icon = Icons.Default.Message,
                        trailing = {
                            Switch(
                                checked = false,
                                onCheckedChange = { /* handle change */ },
                                colors = SwitchDefaults.colors(
                                    thumbColor = FoodDeliveryTheme.colorScheme.primary,
                                    trackColor = FoodDeliveryTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    )
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Preferences Section
            SettingsSection(
                title = "Preferences",
                items = listOf(
                    SettingItem(
                        title = "Language",
                        subtitle = "English",
                        icon = Icons.Default.Language,
                        trailing = {
                            Text(
                                text = "English",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    ),
                    SettingItem(
                        title = "Currency",
                        subtitle = "USD",
                        icon = Icons.Default.AttachMoney,
                        trailing = {
                            Text(
                                text = "USD",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Support Section
            SettingsSection(
                title = "Support",
                items = listOf(
                    SettingItem(
                        title = "Help Center",
                        subtitle = "FAQs and guides",
                        icon = Icons.Default.Help,
                        onClick = { /* navigate to help */ }
                    ),
                    SettingItem(
                        title = "Contact Us",
                        subtitle = "Get in touch with support",
                        icon = Icons.Default.ContactSupport,
                        onClick = { /* navigate to contact */ }
                    ),
                    SettingItem(
                        title = "Terms of Service",
                        subtitle = "Read our terms and conditions",
                        icon = Icons.Default.Description,
                        onClick = { /* navigate to terms */ }
                    ),
                    SettingItem(
                        title = "Privacy Policy",
                        subtitle = "How we handle your data",
                        icon = Icons.Default.PrivacyTip,
                        onClick = { /* navigate to privacy */ }
                    )
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // About Section
            SettingsSection(
                title = "About",
                items = listOf(
                    SettingItem(
                        title = "App Version",
                        subtitle = "1.0.0",
                        icon = Icons.Default.Info,
                        trailing = {
                            Text(
                                text = "1.0.0",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    ),
                    SettingItem(
                        title = "Open Source Licenses",
                        subtitle = "View third-party licenses",
                        icon = Icons.Default.Code,
                        onClick = { /* show licenses */ }
                    )
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Out Button
            Button(
                onClick = {
                    val confirmed = remember { mutableStateOf(false) }
                    // In a real app, show confirmation dialog
                    onSignOut()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FoodDeliveryTheme.colorScheme.errorContainer,
                    contentColor = FoodDeliveryTheme.colorScheme.onErrorContainer
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Sign out",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Sign Out")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Delete Account
            TextButton(
                onClick = { /* show delete account confirmation */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Delete Account",
                    color = FoodDeliveryTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    items: List<SettingItem>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    SettingItemComposable(
                        item = item,
                        isLast = index == items.lastIndex
                    )
                    if (!item.isLast) {
                        Divider(
                            modifier = Modifier.padding(start = 72.dp, end = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingItemComposable(
    item: SettingItem,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                item.subtitle?.let { subtitle ->
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        item.trailing?.invoke()
        item.onClick?.let { click ->
            androidx.compose.material3.Text(
                text = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
                    .clickable { click() }
                    .padding(end = 16.dp)
            )
        }
    }
}

data class SettingItem(
    val title: String,
    val subtitle: String? = null,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val trailing: (@Composable () -> Unit)? = null,
    val onClick: (() -> Unit)? = null,
    val isLast: Boolean = false
)