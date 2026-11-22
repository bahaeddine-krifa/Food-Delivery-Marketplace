package com.fooddelivery.deliverymobile.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.deliverymobile.ui.theme.FoodDeliveryDeliveryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow, shape = RectangleShape)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.IconButton(onClick = { /* Navigate back handled by nav controller */ }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text("Settings", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Settings Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(12.dp))

                        SettingRow(
                            icon = Icons.Filled.Person,
                            title = "Account Information",
                            subtitle = "View and edit your profile",
                            onClick = { /* Navigate to profile */ }
                        )

                        Divider()

                        SettingRow(
                            icon = Icons.Filled.Settings,
                            title = "Delivery Preferences",
                            subtitle = "Manage delivery zones and vehicle type",
                            onClick = { /* Navigate to preferences */ }
                        )
                    }
                }

                // App Settings Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("App Settings", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(12.dp))

                        SettingSwitchRow(
                            icon = Icons.Filled.Notifications,
                            title = "Push Notifications",
                            subtitle = "Receive delivery alerts and updates",
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it }
                        )

                        Divider()

                        SettingSwitchRow(
                            icon = Icons.Filled.Settings,
                            title = "Dark Mode",
                            subtitle = "Use dark theme",
                            checked = darkModeEnabled,
                            onCheckedChange = { darkModeEnabled = it }
                        )

                        Divider()

                        SettingRow(
                            icon = Icons.Filled.Settings,
                            title = "Language",
                            subtitle = "English",
                            onClick = { /* Show language picker */ }
                        )
                    }
                }

                // Support Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Support", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(12.dp))

                        SettingRow(
                            icon = Icons.Filled.Settings,
                            title = "Help Center",
                            subtitle = "FAQs and guides",
                            onClick = { /* Open help */ }
                        )

                        Divider()

                        SettingRow(
                            icon = Icons.Filled.Settings,
                            title = "Contact Support",
                            subtitle = "Get in touch with our team",
                            onClick = { /* Open support chat */ }
                        )

                        Divider()

                        SettingRow(
                            icon = Icons.Filled.Settings,
                            title = "About",
                            subtitle = "Version 1.0.0",
                            onClick = { /* Show about dialog */ }
                        )
                    }
                }

                // Danger Zone
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Account", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(12.dp))

                        Button(
                            onClick = onLogout,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.ExitToApp, contentDescription = "Logout", tint = MaterialTheme.colorScheme.onError)
                                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))
                                Text("Sign Out", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onError)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    androidx.compose.material3.TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
                Column {
                    Text(title, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Icon(Icons.Filled.ArrowBack, contentDescription = "Next", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun SettingSwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
            Column {
                Text(title, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = androidx.compose.material3.SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}