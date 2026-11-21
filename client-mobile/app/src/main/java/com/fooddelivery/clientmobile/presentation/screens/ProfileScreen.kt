package com.fooddelivery.clientmobile.presentation.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.fooddelivery.clientmobile.domain.model.Address
import com.fooddelivery.clientmobile.domain.model.User
import com.fooddelivery.clientmobile.presentation.profile.ProfileViewModel
import com.fooddelivery.clientmobile.ui.theme.FoodDeliveryTheme
import coil3.compose.AsyncImage
import coil3.compose.data
import coil3.request.Crossfade

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onNavigateToAddresses: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val addresses by viewModel.addresses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isChangingPassword by viewModel.isChangingPassword.collectAsState()
    val error by viewModel.error.collectAsState()
    val passwordChangeResult by viewModel.passwordChangeResult.collectAsState()

    var showPasswordDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    LaunchedEffect(passwordChangeResult) {
        passwordChangeResult?.let { result ->
            when (result) {
                is Result.Success -> {
                    showPasswordDialog = false
                    currentPassword = ""
                    newPassword = ""
                    confirmNewPassword = ""
                }
                is Result.Failure -> {
                    // Error handled in viewModel
                }
            }
            viewModel.clearPasswordChangeResult()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FoodDeliveryTheme.colorScheme.surface
                )
            )

            user?.let { user ->
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Profile Header
                    ProfileHeader(user = user)

                    // Account Info
                    AccountInfoSection(user = user)

                    // Saved Addresses
                    AddressesSection(
                        addresses = addresses,
                        onManageClick = onNavigateToAddresses
                    )

                    // Security
                    SecuritySection(
                        onChangePasswordClick = onNavigateToChangePassword
                    )

                    // Settings
                    SettingsSection(
                        onSettingsClick = onNavigateToSettings
                    )

                    // Logout
                    Button(
                        onClick = { viewModel.logout(); onLogout() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = FoodDeliveryTheme.colorScheme.errorContainer,
                            contentColor = FoodDeliveryTheme.colorScheme.error
                        )
                    ) {
                        Text("Sign Out", fontWeight = FontWeight.Medium)
                    }
                }
            } ?: if (isLoading) {
                LoadingPlaceholder()
            }
        }
    }

    // Change Password Dialog
    if (showPasswordDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Change Password") },
            text = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current Password") },
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmNewPassword,
                        onValueChange = { confirmNewPassword = it },
                        label = { Text("Confirm New Password") },
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPassword == confirmNewPassword && newPassword.length >= 8) {
                            viewModel.changePassword(currentPassword, newPassword)
                        }
                    },
                    enabled = !isChangingPassword
                ) {
                    if (isChangingPassword) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = FoodDeliveryTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Change")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileHeader(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier.size(100.dp).clip(CircleShape)
            ) {
                user.avatarUrl?.let { url ->
                    AsyncImage(
                        model = coil3.compose.data(url),
                        contentDescription = user.fullName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        crossfade = Crossfade(true)
                    )
                } ?: Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(FoodDeliveryTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.fullName.initials(),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = FoodDeliveryTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Name & Email
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = user.fullName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = user.email, fontSize = 16.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant)
                user.phone?.let { Text(text = it, fontSize = 14.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant) }
            }

            // Status badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusChip(
                    text = user.role.name,
                    color = FoodDeliveryTheme.colorScheme.primary
                )
                StatusChip(
                    text = user.status.name.replace("_", " "),
                    color = if (user.status.name == "ACTIVE") FoodDeliveryTheme.colorScheme.secondary else FoodDeliveryTheme.colorScheme.tertiary
                )
                if (!user.emailVerified) {
                    StatusChip(
                        text = "Email not verified",
                        color = FoodDeliveryTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(text: String, color: Color) {
    Box(
        modifier = Modifier.padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = color,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .background(color.copy(alpha = 0.1f))
                .clip(RoundedCornerShape(20.dp))
        )
    }
}

@Composable
fun AccountInfoSection(user: User) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Account Information", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoRow(label = "Email", value = user.email)
                user.phone?.let { InfoRow(label = "Phone", value = it) }
                InfoRow(label = "Member since", value = formatDate(user.createdAt))
            }
        }
    }
}

@Composable
fun AddressesSection(
    addresses: List<Address>,
    onManageClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Saved Addresses", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onManageClick) { Text("Manage") }
        }

        if (addresses.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = FoodDeliveryTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.AddLocation,
                        contentDescription = "Add address",
                        tint = FoodDeliveryTheme.colorScheme.onSurfaceVariant
                    )
                    Text(text = "No saved addresses", fontSize = 16.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant)
                    Button(onClick = onManageClick) { Text("Add Address") }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(addresses) { address ->
                    AddressCard(address = address)
                }
            }
        }
    }
}

@Composable
fun AddressCard(address: Address) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = address.label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    if (address.isDefault) {
                        androidx.compose.material3.Chip(
                            onClick = { },
                            colors = androidx.compose.material3.ChipDefaults.chipColors(
                                containerColor = FoodDeliveryTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text("Default", fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
            Text(text = address.fullAddress, fontSize = 14.sp)
        }
    }
}

@Composable
fun SecuritySection(onChangePasswordClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Security", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Change Password", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text(text = "Update your password", fontSize = 12.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant)
                    }
                    Button(onClick = onChangePasswordClick) { Text("Change") }
                }
            }
        }
    }
}

@Composable
fun SettingsSection(onSettingsClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Settings", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "App Settings", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text(text = "Manage preferences, notifications, and more", fontSize = 12.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant)
                    }
                    Button(onClick = onSettingsClick) { Text("Open") }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun LoadingPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(5) {
            Box(
                modifier = Modifier.fillMaxWidth().height(100.dp)
                    .background(FoodDeliveryTheme.colorScheme.surfaceVariant)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}

private fun String.initials(): String {
    return this.split(" ").joinToString("") { it.firstOrNull()?.uppercase() ?: "" }.take(2)
}

private fun formatDate(date: java.time.OffsetDateTime): String {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM yyyy")
    return date.format(formatter)
}

@Composable
fun PreviewProfileScreen() {
    FoodDeliveryTheme {
        ProfileScreen(
            onNavigateToAddresses = { },
            onLogout = { }
        )
    }
}