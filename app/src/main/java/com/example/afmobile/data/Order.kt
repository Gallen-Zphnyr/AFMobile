package com.example.afmobile.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.io.Serializable

/**
 * Order data class
 * Represents a user's order with payment and shipping status
 */
data class Order(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val deliveryAddress: String = "",
    val phoneNumber: String = "",
    val items: List<OrderItem> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val totalAmount: Double = 0.0,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val orderStatus: OrderStatus = OrderStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val paidAt: Long? = null,
    val approvedAt: Long? = null,
    val shippedAt: Long? = null,
    val deliveredAt: Long? = null,
    val notes: String = ""
) : Serializable {

    /**
     * Get the current status description
     */
    fun getStatusDescription(): String {
        return when (orderStatus) {
            OrderStatus.PENDING -> "Waiting for payment"
            OrderStatus.PAID -> "Payment received - Waiting for admin approval"
            OrderStatus.APPROVED -> "Order approved - Preparing for shipment"
            OrderStatus.SHIPPED -> "Order shipped - On the way"
            OrderStatus.DELIVERED -> "Order delivered"
            OrderStatus.CANCELLED -> "Order cancelled"
        }
    }

    /**
     * Check if order can be paid
     */
    fun canBePaid(): Boolean {
        return paymentStatus == PaymentStatus.PENDING && orderStatus == OrderStatus.PENDING
    }

    /**
     * Check if order is awaiting admin approval
     */
    fun isAwaitingApproval(): Boolean {
        return paymentStatus == PaymentStatus.PAID && orderStatus == OrderStatus.PAID
    }
}

/**
 * Order Item data class
 * Represents a single product in an order
 */
data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val productImageUrl: String = "",
    val productPrice: Double = 0.0,
    val quantity: Int = 1
) : Serializable {

    fun getTotalPrice(): Double {
        return productPrice * quantity
    }
}

/**
 * Payment Status enum
 */
enum class PaymentStatus {
    PENDING,    // User hasn't paid yet
    PAID,       // User has clicked "Pay" button
    VERIFIED    // Admin verified payment (for future use)
}

/**
 * Order Status enum
 */
enum class OrderStatus {
    PENDING,    // Order created, waiting for payment
    PAID,       // User paid, waiting for admin approval
    APPROVED,   // Admin approved, ready to ship
    SHIPPED,    // Order has been shipped
    DELIVERED,  // Order delivered to customer
    CANCELLED   // Order cancelled
}

/**
 * Firebase Order data class for Firestore parsing
 */
data class FirebaseOrder(
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val deliveryAddress: String = "",
    val phoneNumber: String = "",
    val items: List<Map<String, Any>> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val totalAmount: Double = 0.0,
    val paymentStatus: String = "PENDING",
    val orderStatus: String = "PENDING",
    val createdAt: Timestamp? = null,
    val paidAt: Timestamp? = null,
    val approvedAt: Timestamp? = null,
    val shippedAt: Timestamp? = null,
    val deliveredAt: Timestamp? = null,
    val notes: String = ""
) {
    fun toOrder(id: String): Order {
        val orderItems = items.map { itemMap ->
            OrderItem(
                productId = itemMap["productId"] as? String ?: "",
                productName = itemMap["productName"] as? String ?: "",
                productImageUrl = itemMap["productImageUrl"] as? String ?: "",
                productPrice = (itemMap["productPrice"] as? Number)?.toDouble() ?: 0.0,
                quantity = (itemMap["quantity"] as? Number)?.toInt() ?: 1
            )
        }

        return Order(
            id = id,
            userId = userId,
            userName = userName,
            userEmail = userEmail,
            deliveryAddress = deliveryAddress,
            phoneNumber = phoneNumber,
            items = orderItems,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            totalAmount = totalAmount,
            paymentStatus = PaymentStatus.valueOf(paymentStatus),
            orderStatus = OrderStatus.valueOf(orderStatus),
            createdAt = createdAt?.toDate()?.time ?: System.currentTimeMillis(),
            paidAt = paidAt?.toDate()?.time,
            approvedAt = approvedAt?.toDate()?.time,
            shippedAt = shippedAt?.toDate()?.time,
            deliveredAt = deliveredAt?.toDate()?.time,
            notes = notes
        )
    }
}
