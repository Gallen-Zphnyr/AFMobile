package com.example.afmobile.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Data Access Object for Product operations
 */
@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY updatedAt DESC")
    fun getAllProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM products WHERE category = :category ORDER BY updatedAt DESC")
    fun getProductsByCategory(category: String): LiveData<List<Product>>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): Product?

    @Query("SELECT * FROM products WHERE name LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%'")
    fun searchProducts(searchQuery: String): LiveData<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int

    @Query("SELECT DISTINCT category FROM products ORDER BY category")
    fun getAllCategories(): LiveData<List<String>>
}
