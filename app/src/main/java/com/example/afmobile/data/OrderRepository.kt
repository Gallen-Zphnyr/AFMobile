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
 * Repository for Order data
 * Handles order operations with Firebase Firestore
 */
class OrderRepository(context: Context) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val TAG = "OrderRepository"

    // LiveData for orders
    private val _userOrders = MutableLiveData<List<Order>>()
    val userOrders: LiveData<List<Order>> = _userOrders

    /**
     * Create a new order from cart items
     */
    suspend fun createOrder(
        cartItems: List<CartItemWithProduct>,
        deliveryAddress: String,
        phoneNumber: String,
        userName: String,
        userEmail: String
    ): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

            // Calculate totals
            val subtotal = cartItems.sumOf { it.getTotalPrice() }
            val deliveryFee = 50.0 // Fixed delivery fee
            val totalAmount = subtotal + deliveryFee

            // Create order items
            val orderItems = cartItems.map { item ->
                mapOf(
                    "productId" to item.cartItem.productId,
                    "productName" to item.cartItem.productName,
                    "productImageUrl" to item.cartItem.productImageUrl,
                    "productPrice" to item.cartItem.productPrice,
                    "quantity" to item.cartItem.quantity
                )
            }

            // Create order document
            val orderData = hashMapOf(
                "userId" to userId,
                "userName" to userName,
                "userEmail" to userEmail,
                "deliveryAddress" to deliveryAddress,
                "phoneNumber" to phoneNumber,
                "items" to orderItems,
                "subtotal" to subtotal,
                "deliveryFee" to deliveryFee,
                "totalAmount" to totalAmount,
                "paymentStatus" to PaymentStatus.PENDING.name,
                "orderStatus" to OrderStatus.PENDING.name,
                "createdAt" to FieldValue.serverTimestamp(),
                "paidAt" to null,
                "approvedAt" to null,
                "shippedAt" to null,
                "deliveredAt" to null,
                "notes" to ""
            )

            val docRef = firestore.collection("orders")
                .add(orderData)
                .await()

            Log.d(TAG, "Order created successfully: ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating order: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Mark order as paid by user
     */
    suspend fun markOrderAsPaid(orderId: String): Result<Boolean> {
        return try {
            firestore.collection("orders")
                .document(orderId)
                .update(
                    mapOf(
                        "paymentStatus" to PaymentStatus.PAID.name,
                        "orderStatus" to OrderStatus.PAID.name,
                        "paidAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()

            Log.d(TAG, "Order marked as paid: $orderId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error marking order as paid: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Load user's orders
     */
    suspend fun loadUserOrders(): Result<List<Order>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

            val snapshot = firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val orders = snapshot.documents.mapNotNull { doc ->
                try {
                    val firebaseOrder = doc.toObject(FirebaseOrder::class.java)
                    firebaseOrder?.toOrder(doc.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing order ${doc.id}: ${e.message}")
                    null
                }
            }

            _userOrders.postValue(orders)
            Log.d(TAG, "Loaded ${orders.size} orders for user")
            Result.success(orders)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading orders: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Get a specific order by ID
     */
    suspend fun getOrder(orderId: String): Result<Order> {
        return try {
            val doc = firestore.collection("orders")
                .document(orderId)
                .get()
                .await()

            val firebaseOrder = doc.toObject(FirebaseOrder::class.java)
            val order = firebaseOrder?.toOrder(doc.id)

            if (order != null) {
                Result.success(order)
            } else {
                Result.failure(Exception("Order not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting order: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Cancel an order (only if not yet paid)
     */
    suspend fun cancelOrder(orderId: String): Result<Boolean> {
        return try {
            val order = getOrder(orderId).getOrNull()
            if (order?.paymentStatus == PaymentStatus.PENDING) {
                firestore.collection("orders")
                    .document(orderId)
                    .update(
                        mapOf(
                            "orderStatus" to OrderStatus.CANCELLED.name
                        )
                    )
                    .await()
                Result.success(true)
            } else {
                Result.failure(Exception("Cannot cancel paid orders"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling order: ${e.message}", e)
            Result.failure(e)
        }
    }
}
