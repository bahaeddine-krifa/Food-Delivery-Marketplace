package com.fooddelivery.clientmobile.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.fooddelivery.clientmobile.presentation.auth.AuthViewModel
import com.fooddelivery.clientmobile.ui.theme.FoodDeliveryTheme
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val isLogin by remember { mutableStateOf(true) }
    val email by remember { mutableStateOf("") }
    val password by remember { mutableStateOf("") }
    val firstName by remember { mutableStateOf("") }
    val lastName by remember { mutableStateOf("") }
    val phone by remember { mutableStateOf("") }
    val rememberMe by remember { mutableStateOf(false) }
    val confirmPassword by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(scrollState),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Logo
                Text(
                    text = "🍔",
                    fontSize = 60.sp
                )

                // Title
                Text(
                    text = if (isLogin) "Welcome Back" else "Create Account",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (isLogin) "Sign in to your account" else "Join FoodDelivery today",
                    fontSize = 16.sp,
                    color = FoodDeliveryTheme.colorScheme.onSurfaceVariant
                )

                // Form fields
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isLogin) {
                        RowFields(
                            label1 = "First Name",
                            value1 = firstName,
                            onValueChange1 = { firstName = it },
                            label2 = "Last Name",
                            value2 = lastName,
                            onValueChange2 = { lastName = it }
                        )
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = error != null && email.isNotBlank()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = error != null && password.isNotBlank()
                    )

                    if (!isLogin) {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = confirmPassword != password && confirmPassword.isNotBlank()
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone (optional)") },
                            keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (isLogin) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        androidx.compose.material3.Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.material3.Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it }
                            )
                            Text("Remember me")
                        }
                        TextButton(onClick = { /* TODO: Forgot password */ }) {
                            Text("Forgot password?")
                        }
                    }
                }

                // Error message
                error?.let { errorMsg ->
                    Text(
                        text = errorMsg,
                        color = FoodDeliveryTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    )
                }

                // Action button
                Button(
                    onClick = {
                        if (isLogin) {
                            viewModel.login(email, password, rememberMe)
                        } else {
                            if (password != confirmPassword) return@Button
                            viewModel.register(email, password, firstName, lastName, phone.ifBlank { null })
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = FoodDeliveryTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (isLogin) "Sign In" else "Create Account")
                    }
                }

                // Switch mode
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(if (isLogin) "Don't have an account? " else "Already have an account? ")
                    TextButton(onClick = {
                        isLogin = !isLogin
                        viewModel.clearError()
                    }) {
                        Text(if (isLogin) "Sign Up" else "Sign In")
                    }
                }
            }
        }
    }
}

@Composable
private fun RowFields(
    label1: String,
    value1: String,
    onValueChange1: (String) -> Unit,
    label2: String,
    value2: String,
    onValueChange2: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = value1,
            onValueChange = onValueChange1,
            label = { Text(label1) },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = value2,
            onValueChange = onValueChange2,
            label = { Text(label2) },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun PreviewAuthScreen() {
    FoodDeliveryTheme {
        AuthScreen(onLoginSuccess = { })
    }
}