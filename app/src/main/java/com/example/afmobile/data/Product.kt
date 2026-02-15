package com.example.afmobile.data

import com.google.firebase.Timestamp
import java.io.Serializable

/**
 * Product data class
 * Matches the Firebase Firestore product structure
 */
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrl: String,
    val sku: String,
    val stockLevel: Int,
    val salesCount: Int,
    val createdAt: Long,
    val updatedAt: Long
) : Serializable

/**
 * Firebase Product data class for Firestore parsing
 * Uses Long for integer fields and Double for decimal fields
 * to match Firebase Firestore's native types
 */
data class FirebaseProduct(
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val imageUrl: String = "",
    val sku: String = "",
    val stockLevel: Long = 0L,
    val salesCount: Long = 0L,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toProduct(id: String): Product {
        return Product(
            id = id,
            name = name,
            description = description,
            price = price,
            category = category,
            imageUrl = imageUrl,
            sku = sku,
            stockLevel = stockLevel.toInt(),
            salesCount = salesCount.toInt(),
            createdAt = createdAt?.seconds ?: 0L,
            updatedAt = updatedAt?.seconds ?: 0L
        )
    }
}
