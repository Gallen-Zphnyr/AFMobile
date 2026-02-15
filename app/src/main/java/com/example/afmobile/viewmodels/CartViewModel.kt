package com.example.afmobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.afmobile.data.CartItem
import com.example.afmobile.data.CartItemWithProduct
import com.example.afmobile.data.CartRepository
import com.example.afmobile.data.Product
import kotlinx.coroutines.launch

/**
 * ViewModel for Cart operations
 */
class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CartRepository

    val cartItems: LiveData<List<CartItem>>
    val cartItemsWithProducts: LiveData<List<CartItemWithProduct>>
    val cartItemCount: LiveData<Int>
    val cartTotalPrice: LiveData<Double>

    init {
        repository = CartRepository(application.applicationContext)
        cartItems = repository.cartItems
        cartItemsWithProducts = repository.cartItemsWithProducts
        cartItemCount = repository.cartItemCount
        cartTotalPrice = repository.cartTotalPrice

        // Setup real-time listener
        repository.setupCartListener()
    }

    /**
     * Load cart items from Firestore
     */
    fun loadCartItems() {
        viewModelScope.launch {
            repository.loadCartItems()
        }
    }

    /**
     * Add item to cart
     */
    fun addToCart(product: Product, quantity: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.addToCart(product, quantity)
            onComplete(success)
        }
    }

    /**
     * Update cart item quantity
     */
    fun updateQuantity(cartItemId: String, quantity: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.updateCartItemQuantity(cartItemId, quantity)
            onComplete(success)
        }
    }

    /**
     * Remove item from cart
     */
    fun removeFromCart(cartItemId: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.removeFromCart(cartItemId)
            onComplete(success)
        }
    }

    /**
     * Clear all cart items
     */
    fun clearCart(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.clearCart()
            onComplete(success)
        }
    }

    /**
     * Get cart item count
     */
    fun getCartItemCount(): Int {
        return repository.getCartItemCount()
    }

    /**
     * Get cart total price
     */
    fun getCartTotalPrice(): Double {
        return repository.getCartTotalPrice()
    }
}
