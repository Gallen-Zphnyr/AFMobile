package com.example.afmobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.afmobile.data.Product
import com.example.afmobile.data.ProductRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for Product operations
 */
class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductRepository
    val allProducts: LiveData<List<Product>>
    val allCategories: LiveData<List<String>>

    init {
        repository = ProductRepository(application.applicationContext)
        allProducts = repository.allProducts
        allCategories = repository.allCategories
    }

    /**
     * Sync products from Firebase
     */
    fun syncProducts() {
        viewModelScope.launch {
            repository.syncProductsFromFirebase()
        }
    }

    /**
     * Get products by category
     */
    fun getProductsByCategory(category: String): LiveData<List<Product>> {
        return repository.getProductsByCategory(category)
    }

    /**
     * Search products
     */
    fun searchProducts(query: String): LiveData<List<Product>> {
        return repository.searchProducts(query)
    }

    /**
     * Get product count
     */
    fun getProductCount(): Int {
        return repository.getProductCount()
    }
}
