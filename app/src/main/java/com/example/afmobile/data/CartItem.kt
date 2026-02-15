package com.example.afmobile.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.io.Serializable

/**
 * Cart Item data class
 * Represents an item in the user's shopping cart
 */
data class CartItem(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val productName: String = "",
    val productPrice: Double = 0.0,
    val productImageUrl: String = "",
    val quantity: Int = 1,
    val addedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Serializable {

    /**
     * Calculate total price for this cart item
     */
    fun getTotalPrice(): Double {
        return productPrice * quantity
    }
}

/**
 * Firebase Cart Item data class for Firestore parsing
 * Uses compatible types for Firebase Firestore
 */
data class FirebaseCartItem(
    val userId: String = "",
    val productId: String = "",
    val productName: String = "",
    val productPrice: Double = 0.0,
    val productImageUrl: String = "",
    val quantity: Long = 1L,
    val addedAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    /**
     * Convert Firebase cart item to app cart item
     */
    fun toCartItem(id: String): CartItem {
        return CartItem(
            id = id,
            userId = userId,
            productId = productId,
            productName = productName,
            productPrice = productPrice,
            productImageUrl = productImageUrl,
            quantity = quantity.toInt(),
            addedAt = addedAt?.toDate()?.time ?: System.currentTimeMillis(),
            updatedAt = updatedAt?.toDate()?.time ?: System.currentTimeMillis()
        )
    }
}

/**
 * Cart item with full product details
 * Used for displaying cart items with complete product information
 */
data class CartItemWithProduct(
    val cartItem: CartItem,
    val product: Product?
) : Serializable {

    fun getTotalPrice(): Double {
        return cartItem.getTotalPrice()
    }

    fun isAvailable(): Boolean {
        return product != null && product.stockLevel >= cartItem.quantity
    }
}
