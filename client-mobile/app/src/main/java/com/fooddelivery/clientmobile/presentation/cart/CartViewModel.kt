package com.fooddelivery.clientmobile.presentation.cart

import androidx.hilt.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.fooddelivery.clientmobile.domain.model.Cart
import com.fooddelivery.clientmobile.domain.model.CartItem
import com.fooddelivery.clientmobile.domain.model.Product
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
@ActivityRetainedScoped
class CartViewModel @Inject constructor() {

    private val _cart = MutableStateFlow<Cart>(Cart())
    val cart = _cart.asStateFlow()

    fun addItem(product: Product, quantity: Int = 1, notes: String? = null) {
        _cart.value = _cart.value.addItem(product, quantity, notes)
    }

    fun removeItem(productId: java.util.UUID, notes: String? = null) {
        _cart.value = _cart.value.removeItem(productId, notes)
    }

    fun updateQuantity(productId: java.util.UUID, quantity: Int, notes: String? = null) {
        _cart.value = _cart.value.updateQuantity(productId, quantity, notes)
    }

    fun clearCart() {
        _cart.value = _cart.value.clear()
    }

    fun getItemQuantity(productId: java.util.UUID, notes: String? = null): Int {
        return _cart.value.items.firstOrNull { it.product.id == productId && it.notes == notes }?.quantity ?: 0
    }
}