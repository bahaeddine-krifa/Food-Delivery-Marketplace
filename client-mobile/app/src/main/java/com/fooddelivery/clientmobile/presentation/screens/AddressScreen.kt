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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.clientmobile.domain.model.Address
import com.fooddelivery.clientmobile.domain.model.AddressType
import com.fooddelivery.clientmobile.domain.model.CreateAddressRequest
import com.fooddelivery.clientmobile.domain.model.UpdateAddressRequest
import com.fooddelivery.clientmobile.presentation.address.AddressViewModel
import com.fooddelivery.clientmobile.ui.theme.FoodDeliveryTheme

@Composable
fun AddressScreen(
    viewModel: AddressViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val addresses by viewModel.addresses.collectAsState()
    val defaultAddress by viewModel.defaultAddress.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val error by viewModel.error.collectAsState()
    val saveResult by viewModel.saveResult.collectAsState()
    val deleteResult by viewModel.deleteResult.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf<Address?>(null) }
    var label by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var building by remember { mutableStateOf("") }
    var apartment by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }
    var addressType by remember { mutableStateOf(AddressType.HOME) }

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadAddresses()
    }

    LaunchedEffect(saveResult) {
        saveResult?.let { result ->
            when (result) {
                is Result.Success -> {
                    showAddDialog = false
                    editingAddress = null
                    clearForm()
                }
                is Result.Failure -> {
                    // Error handled in viewModel
                }
            }
            viewModel.clearSaveResult()
        }
    }

    LaunchedEffect(deleteResult) {
        deleteResult?.let { result ->
            when (result) {
                is Result.Success -> {
                    // Deleted successfully
                }
                is Result.Failure -> {
                    // Error handled in viewModel
                }
            }
            viewModel.clearDeleteResult()
        }
    }

    fun clearForm() {
        label = ""
        street = ""
        building = ""
        apartment = ""
        city = ""
        state = ""
        postalCode = ""
        instructions = ""
        isDefault = false
        addressType = AddressType.HOME
    }

    fun populateForm(address: Address) {
        label = address.label
        street = address.street
        building = address.building ?: ""
        apartment = address.apartment ?: ""
        city = address.city
        state = address.state
        postalCode = address.postalCode
        instructions = address.instructions ?: ""
        isDefault = address.isDefault
        addressType = address.addressType
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            TopAppBar(
                title = { Text("Saved Addresses", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        editingAddress = null
                        clearForm()
                        showAddDialog = true
                    }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Add,
                            contentDescription = "Add Address"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FoodDeliveryTheme.colorScheme.surface
                )
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isLoading && addresses.isEmpty()) {
                    LoadingPlaceholder()
                } else if (addresses.isEmpty()) {
                    EmptyAddresses()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(addresses) { address ->
                            AddressCard(
                                address = address,
                                isDefault = defaultAddress?.id == address.id,
                                onEditClick = {
                                    editingAddress = address
                                    populateForm(address)
                                    showAddDialog = true
                                },
                                onDeleteClick = {
                                    viewModel.deleteAddress(address.id)
                                },
                                onSetDefaultClick = {
                                    viewModel.setDefaultAddress(address.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add/Edit Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                editingAddress = null
                clearForm()
            },
            title = { Text(if (editingAddress != null) "Edit Address" else "Add Address") },
            text = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it },
                        label = { Text("Label *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = street,
                        onValueChange = { street = it },
                        label = { Text("Street Address *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = building,
                            onValueChange = { building = it },
                            label = { Text("Building") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = apartment,
                            onValueChange = { apartment = it },
                            label = { Text("Apartment") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City *") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = state,
                            onValueChange = { state = it },
                            label = { Text("State *") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = postalCode,
                        onValueChange = { postalCode = it },
                        label = { Text("Postal Code *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = instructions,
                        onValueChange = { instructions = it },
                        label = { Text("Delivery Instructions") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Address Type
                    Text(text = "Address Type", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AddressType.values().forEach { type ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                RadioButton(
                                    selected = addressType == type,
                                    onClick = { addressType = type }
                                )
                                Text(text = type.name, fontSize = 14.sp)
                            }
                        }
                    }
                    // Default checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        androidx.compose.material3.Checkbox(
                            checked = isDefault,
                            onCheckedChange = { isDefault = it }
                        )
                        Text("Set as default address")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (validateForm()) {
                            val request = CreateAddressRequest(
                                label = label,
                                street = street,
                                building = building.ifBlank { null },
                                floor = null,
                                apartment = apartment.ifBlank { null },
                                city = city,
                                state = state,
                                postalCode = postalCode,
                                country = "TN",
                                latitude = null,
                                longitude = null,
                                instructions = instructions.ifBlank { null },
                                isDefault = isDefault,
                                addressType = addressType
                            )
                            if (editingAddress != null) {
                                val updateRequest = UpdateAddressRequest(
                                    label = label.ifBlank { null },
                                    street = street.ifBlank { null },
                                    building = building.ifBlank { null },
                                    floor = null,
                                    apartment = apartment.ifBlank { null },
                                    city = city.ifBlank { null },
                                    state = state.ifBlank { null },
                                    postalCode = postalCode.ifBlank { null },
                                    country = null,
                                    latitude = null,
                                    longitude = null,
                                    instructions = instructions.ifBlank { null },
                                    isDefault = isDefault,
                                    addressType = addressType
                                )
                                viewModel.updateAddress(editingAddress!!.id, updateRequest)
                            } else {
                                viewModel.createAddress(request)
                            }
                        }
                    },
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = FoodDeliveryTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (editingAddress != null) "Save" else "Add")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    editingAddress = null
                    clearForm()
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AddressCard(
    address: Address,
    isDefault: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSetDefaultClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = if (isDefault) androidx.compose.ui.graphics.drawscope.Stroke(2.dp, FoodDeliveryTheme.colorScheme.primary) else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                    if (isDefault) {
                        androidx.compose.material3.Chip(
                            onClick = { },
                            colors = androidx.compose.material3.ChipDefaults.chipColors(
                                containerColor = FoodDeliveryTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text("Default", fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    androidx.compose.material3.Chip(
                        onClick = { },
                        colors = androidx.compose.material3.ChipDefaults.chipColors(
                            containerColor = FoodDeliveryTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(address.addressType.name, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Text(text = address.fullAddress, fontSize = 14.sp)

            if (!isDefault) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onSetDefaultClick) {
                        Text("Set as Default")
                    }
                    TextButton(onClick = onEditClick) {
                        Text("Edit")
                    }
                }
            } else {
                TextButton(onClick = onEditClick) {
                    Text("Edit")
                }
            }

            // Delete button (with confirmation in real app)
            TextButton(onClick = onDeleteClick) {
                Text("Delete", color = FoodDeliveryTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun EmptyAddresses() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.AddLocation,
            contentDescription = "No addresses",
            tint = FoodDeliveryTheme.colorScheme.onSurfaceVariant
        )
        Text(text = "No saved addresses", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = "Add your first delivery address", fontSize = 14.sp, color = FoodDeliveryTheme.colorScheme.onSurfaceVariant)
        Button(onClick = { /* Handled by FAB */ }) {
            Text("Add Address")
        }
    }
}

@Composable
fun LoadingPlaceholder() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(3) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(140.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(FoodDeliveryTheme.colorScheme.surfaceVariant))
            }
        }
    }
}

private fun validateForm(): Boolean {
    return label.isNotBlank() && street.isNotBlank() && city.isNotBlank() && state.isNotBlank() && postalCode.isNotBlank()
}

@Composable
fun PreviewAddressScreen() {
    FoodDeliveryTheme {
        AddressScreen(onNavigateBack = { })
    }
}