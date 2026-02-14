package com.example.afmobile.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

/**
 * Product Entity for Room Database
 * Mirrors the Firebase Firestore product structure
 */
@Entity(tableName = "products")
data class Product(
    @PrimaryKey
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
)

/**
 * Firebase Product data class for Firestore parsing
 */
data class FirebaseProduct(
    val name: String = "",
    val description: String = "",
    val price: Number = 0,
    val category: String = "",
    val imageUrl: String = "",
    val sku: String = "",
    val stockLevel: Number = 0,
    val salesCount: Number = 0,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toProduct(id: String): Product {
        return Product(
            id = id,
            name = name,
            description = description,
            price = price.toDouble(),
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
