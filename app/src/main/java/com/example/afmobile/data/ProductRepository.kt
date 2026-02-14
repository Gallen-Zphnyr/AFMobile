package com.example.afmobile.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository for Product data
 * Handles data operations from both local database and Firebase
 */
class ProductRepository(private val productDao: ProductDao) {

    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "ProductRepository"

    // LiveData from local database
    val allProducts: LiveData<List<Product>> = productDao.getAllProducts()

    fun getProductsByCategory(category: String): LiveData<List<Product>> {
        return productDao.getProductsByCategory(category)
    }

    fun searchProducts(query: String): LiveData<List<Product>> {
        return productDao.searchProducts(query)
    }

    val allCategories: LiveData<List<String>> = productDao.getAllCategories()

    suspend fun getProductById(productId: String): Product? {
        return productDao.getProductById(productId)
    }

    /**
     * Sync products from Firebase to local database
     */
    suspend fun syncProductsFromFirebase(): Boolean {
        return try {
            Log.d(TAG, "Starting product sync from Firebase...")

            val snapshot = firestore.collection("products")
                .get()
                .await()

            val products = snapshot.documents.mapNotNull { doc ->
                try {
                    val firebaseProduct = doc.toObject(FirebaseProduct::class.java)
                    firebaseProduct?.toProduct(doc.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing product ${doc.id}: ${e.message}")
                    null
                }
            }

            if (products.isNotEmpty()) {
                productDao.insertProducts(products)
                Log.d(TAG, "Successfully synced ${products.size} products")
            } else {
                Log.w(TAG, "No products found in Firebase")
            }

            true
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing products from Firebase: ${e.message}", e)
            false
        }
    }

    /**
     * Insert a single product (mainly for testing)
     */
    suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product)
    }

    /**
     * Get product count in local database
     */
    suspend fun getProductCount(): Int {
        return productDao.getProductCount()
    }

    /**
     * Clear all products from local database
     */
    suspend fun clearAllProducts() {
        productDao.deleteAllProducts()
    }
}
