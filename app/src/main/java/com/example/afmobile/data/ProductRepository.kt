package com.example.afmobile.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository for Product data
 * Handles data operations from both local database and Firebase
 */
class ProductRepository(context: Context) {

    private val dbHelper = ProductDatabaseHelper(context)
    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "ProductRepository"

    // LiveData for products
    private val _allProducts = MutableLiveData<List<Product>>()
    val allProducts: LiveData<List<Product>> = _allProducts

    private val _allCategories = MutableLiveData<List<String>>()
    val allCategories: LiveData<List<String>> = _allCategories

    init {
        // Load initial data
        refreshAllProducts()
        refreshCategories()
    }

    /**
     * Refresh all products from database
     */
    fun refreshAllProducts() {
        _allProducts.postValue(dbHelper.getAllProducts())
        refreshCategories()
    }

    /**
     * Refresh categories
     */
    private fun refreshCategories() {
        _allCategories.postValue(dbHelper.getAllCategories())
    }

    fun getProductsByCategory(category: String): LiveData<List<Product>> {
        val liveData = MutableLiveData<List<Product>>()
        liveData.value = dbHelper.getProductsByCategory(category)
        return liveData
    }

    fun searchProducts(query: String): LiveData<List<Product>> {
        val liveData = MutableLiveData<List<Product>>()
        liveData.value = dbHelper.searchProducts(query)
        return liveData
    }

    suspend fun getProductById(productId: String): Product? {
        return dbHelper.getProductById(productId)
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
                dbHelper.insertProducts(products)
                refreshAllProducts()
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
    fun insertProduct(product: Product) {
        dbHelper.insertOrUpdateProduct(product)
        refreshAllProducts()
    }

    /**
     * Get product count in local database
     */
    fun getProductCount(): Int {
        return dbHelper.getProductCount()
    }

    /**
     * Clear all products from local database
     */
    fun clearAllProducts() {
        dbHelper.deleteAllProducts()
        refreshAllProducts()
    }
}
