package com.example.afmobile.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Repository for Cart data
 * Handles cart operations with Firebase Firestore
 */
class CartRepository(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val productRepository = ProductRepository(context)
    private val TAG = "CartRepository"

    companion object {
        private const val COLLECTION_CART = "cart"
    }

    // LiveData for cart items
    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _cartItemsWithProducts = MutableLiveData<List<CartItemWithProduct>>()
    val cartItemsWithProducts: LiveData<List<CartItemWithProduct>> = _cartItemsWithProducts

    private val _cartItemCount = MutableLiveData<Int>()
    val cartItemCount: LiveData<Int> = _cartItemCount

    private val _cartTotalPrice = MutableLiveData<Double>()
    val cartTotalPrice: LiveData<Double> = _cartTotalPrice

    /**
     * Get current user ID
     */
    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Load cart items from Firestore
     */
    suspend fun loadCartItems(): Boolean {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                Log.w(TAG, "User not authenticated")
                _cartItems.postValue(emptyList())
                _cartItemsWithProducts.postValue(emptyList())
                updateCartSummary(emptyList())
                return false
            }

            Log.d(TAG, "Loading cart items for user: $userId")

            val snapshot = firestore.collection(COLLECTION_CART)
                .whereEqualTo("userId", userId)
                .orderBy("addedAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val items = snapshot.documents.mapNotNull { doc ->
                try {
                    val firebaseCartItem = doc.toObject(FirebaseCartItem::class.java)
                    firebaseCartItem?.toCartItem(doc.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing cart item ${doc.id}: ${e.message}")
                    null
                }
            }

            _cartItems.postValue(items)

            // Load full product details for each cart item
            loadCartItemsWithProducts(items)

            updateCartSummary(items)

            Log.d(TAG, "Successfully loaded ${items.size} cart items")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error loading cart items: ${e.message}", e)
            _cartItems.postValue(emptyList())
            _cartItemsWithProducts.postValue(emptyList())
            updateCartSummary(emptyList())
            false
        }
    }

    /**
     * Load cart items with full product details
     */
    private suspend fun loadCartItemsWithProducts(items: List<CartItem>) {
        val itemsWithProducts = items.map { cartItem ->
            val product = productRepository.getProductById(cartItem.productId)
            CartItemWithProduct(cartItem, product)
        }
        _cartItemsWithProducts.postValue(itemsWithProducts)
    }

    /**
     * Add item to cart
     */
    suspend fun addToCart(product: Product, quantity: Int): Boolean {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                Log.w(TAG, "User not authenticated")
                return false
            }

            Log.d(TAG, "Adding to cart: ${product.name} (quantity: $quantity)")

            // Check if item already exists in cart
            val existingItem = findCartItemByProductId(userId, product.id)

            if (existingItem != null) {
                // Update quantity if item exists
                val newQuantity = existingItem.quantity + quantity
                updateCartItemQuantity(existingItem.id, newQuantity)
            } else {
                // Create new cart item
                val cartItem = hashMapOf(
                    "userId" to userId,
                    "productId" to product.id,
                    "productName" to product.name,
                    "productPrice" to product.price,
                    "productImageUrl" to product.imageUrl,
                    "quantity" to quantity,
                    "addedAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )

                firestore.collection(COLLECTION_CART)
                    .add(cartItem)
                    .await()

                Log.d(TAG, "Successfully added ${product.name} to cart")
            }

            // Reload cart items
            loadCartItems()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding to cart: ${e.message}", e)
            false
        }
    }

    /**
     * Find cart item by product ID
     */
    private suspend fun findCartItemByProductId(userId: String, productId: String): CartItem? {
        return try {
            val snapshot = firestore.collection(COLLECTION_CART)
                .whereEqualTo("userId", userId)
                .whereEqualTo("productId", productId)
                .limit(1)
                .get()
                .await()

            if (snapshot.documents.isNotEmpty()) {
                val doc = snapshot.documents[0]
                val firebaseCartItem = doc.toObject(FirebaseCartItem::class.java)
                firebaseCartItem?.toCartItem(doc.id)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error finding cart item: ${e.message}", e)
            null
        }
    }

    /**
     * Update cart item quantity
     */
    suspend fun updateCartItemQuantity(cartItemId: String, quantity: Int): Boolean {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                Log.w(TAG, "User not authenticated")
                return false
            }

            Log.d(TAG, "Updating cart item quantity: $cartItemId to $quantity")

            if (quantity <= 0) {
                // Remove item if quantity is 0 or less
                return removeFromCart(cartItemId)
            }

            firestore.collection(COLLECTION_CART)
                .document(cartItemId)
                .update(
                    mapOf(
                        "quantity" to quantity,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()

            // Reload cart items
            loadCartItems()
            Log.d(TAG, "Successfully updated cart item quantity")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating cart item quantity: ${e.message}", e)
            false
        }
    }

    /**
     * Remove item from cart
     */
    suspend fun removeFromCart(cartItemId: String): Boolean {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                Log.w(TAG, "User not authenticated")
                return false
            }

            Log.d(TAG, "Removing cart item: $cartItemId")

            firestore.collection(COLLECTION_CART)
                .document(cartItemId)
                .delete()
                .await()

            // Reload cart items
            loadCartItems()
            Log.d(TAG, "Successfully removed cart item")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error removing cart item: ${e.message}", e)
            false
        }
    }

    /**
     * Clear all cart items for current user
     */
    suspend fun clearCart(): Boolean {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                Log.w(TAG, "User not authenticated")
                return false
            }

            Log.d(TAG, "Clearing cart for user: $userId")

            val snapshot = firestore.collection(COLLECTION_CART)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            // Delete all cart items in batch
            val batch = firestore.batch()
            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()

            // Clear local data
            _cartItems.postValue(emptyList())
            _cartItemsWithProducts.postValue(emptyList())
            updateCartSummary(emptyList())

            Log.d(TAG, "Successfully cleared cart")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing cart: ${e.message}", e)
            false
        }
    }

    /**
     * Get cart item count
     */
    fun getCartItemCount(): Int {
        return _cartItems.value?.sumOf { it.quantity } ?: 0
    }

    /**
     * Get cart total price
     */
    fun getCartTotalPrice(): Double {
        return _cartItems.value?.sumOf { it.getTotalPrice() } ?: 0.0
    }

    /**
     * Update cart summary (count and total)
     */
    private fun updateCartSummary(items: List<CartItem>) {
        val count = items.sumOf { it.quantity }
        val total = items.sumOf { it.getTotalPrice() }

        _cartItemCount.postValue(count)
        _cartTotalPrice.postValue(total)
    }

    /**
     * Setup real-time listener for cart changes
     */
    fun setupCartListener() {
        val userId = getCurrentUserId()
        if (userId == null) {
            Log.w(TAG, "User not authenticated, cannot setup listener")
            return
        }

        firestore.collection(COLLECTION_CART)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to cart changes: ${error.message}", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { doc ->
                        try {
                            val firebaseCartItem = doc.toObject(FirebaseCartItem::class.java)
                            firebaseCartItem?.toCartItem(doc.id)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing cart item ${doc.id}: ${e.message}")
                            null
                        }
                    }

                    _cartItems.postValue(items)
                    updateCartSummary(items)

                    Log.d(TAG, "Cart updated via listener: ${items.size} items")
                }
            }
    }
}
