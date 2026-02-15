package com.example.afmobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.afmobile.data.*
import kotlinx.coroutines.launch

/**
 * ViewModel for Order operations
 */
class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val orderRepository: OrderRepository = OrderRepository(application.applicationContext)

    val userOrders: LiveData<List<Order>> = orderRepository.userOrders

    /**
     * Create a new order
     */
    fun createOrder(
        cartItems: List<CartItemWithProduct>,
        deliveryAddress: String,
        phoneNumber: String,
        userName: String,
        userEmail: String,
        onComplete: (Result<String>) -> Unit
    ) {
        viewModelScope.launch {
            val result = orderRepository.createOrder(
                cartItems,
                deliveryAddress,
                phoneNumber,
                userName,
                userEmail
            )
            onComplete(result)
        }
    }

    /**
     * Mark order as paid
     */
    fun markOrderAsPaid(orderId: String, onComplete: (Result<Boolean>) -> Unit) {
        viewModelScope.launch {
            val result = orderRepository.markOrderAsPaid(orderId)
            onComplete(result)
        }
    }

    /**
     * Load user's orders
     */
    fun loadOrders(onComplete: (Result<List<Order>>) -> Unit = {}) {
        viewModelScope.launch {
            val result = orderRepository.loadUserOrders()
            onComplete(result)
        }
    }

    /**
     * Get a specific order
     */
    fun getOrder(orderId: String, onComplete: (Result<Order>) -> Unit) {
        viewModelScope.launch {
            val result = orderRepository.getOrder(orderId)
            onComplete(result)
        }
    }

    /**
     * Cancel an order
     */
    fun cancelOrder(orderId: String, onComplete: (Result<Boolean>) -> Unit) {
        viewModelScope.launch {
            val result = orderRepository.cancelOrder(orderId)
            onComplete(result)
        }
    }
}
