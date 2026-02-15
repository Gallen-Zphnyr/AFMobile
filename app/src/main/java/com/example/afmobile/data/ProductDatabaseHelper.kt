package com.example.afmobile.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * SQLite Database Helper for Products
 */
class ProductDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "afmobile_database"
        private const val DATABASE_VERSION = 1
        private const val TAG = "ProductDatabaseHelper"

        // Table name
        private const val TABLE_PRODUCTS = "products"

        // Column names
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_IMAGE_URL = "imageUrl"
        private const val COLUMN_SKU = "sku"
        private const val COLUMN_STOCK_LEVEL = "stockLevel"
        private const val COLUMN_SALES_COUNT = "salesCount"
        private const val COLUMN_CREATED_AT = "createdAt"
        private const val COLUMN_UPDATED_AT = "updatedAt"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_PRODUCTS (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_PRICE REAL NOT NULL,
                $COLUMN_CATEGORY TEXT,
                $COLUMN_IMAGE_URL TEXT,
                $COLUMN_SKU TEXT,
                $COLUMN_STOCK_LEVEL INTEGER DEFAULT 0,
                $COLUMN_SALES_COUNT INTEGER DEFAULT 0,
                $COLUMN_CREATED_AT INTEGER,
                $COLUMN_UPDATED_AT INTEGER
            )
        """.trimIndent()

        db.execSQL(createTable)
        Log.d(TAG, "Database table created successfully")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        onCreate(db)
    }

    /**
     * Insert or update a product
     */
    fun insertOrUpdateProduct(product: Product): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, product.id)
            put(COLUMN_NAME, product.name)
            put(COLUMN_DESCRIPTION, product.description)
            put(COLUMN_PRICE, product.price)
            put(COLUMN_CATEGORY, product.category)
            put(COLUMN_IMAGE_URL, product.imageUrl)
            put(COLUMN_SKU, product.sku)
            put(COLUMN_STOCK_LEVEL, product.stockLevel)
            put(COLUMN_SALES_COUNT, product.salesCount)
            put(COLUMN_CREATED_AT, product.createdAt)
            put(COLUMN_UPDATED_AT, product.updatedAt)
        }

        return db.insertWithOnConflict(TABLE_PRODUCTS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    /**
     * Insert multiple products
     */
    fun insertProducts(products: List<Product>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            products.forEach { product ->
                insertOrUpdateProduct(product)
            }
            db.setTransactionSuccessful()
            Log.d(TAG, "Inserted ${products.size} products")
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting products: ${e.message}", e)
        } finally {
            db.endTransaction()
        }
    }

    /**
     * Get all products ordered by updated date
     */
    fun getAllProducts(): List<Product> {
        val products = mutableListOf<Product>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_PRODUCTS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_UPDATED_AT DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                products.add(cursorToProduct(it))
            }
        }

        return products
    }

    /**
     * Get products by category
     */
    fun getProductsByCategory(category: String): List<Product> {
        val products = mutableListOf<Product>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_PRODUCTS,
            null,
            "$COLUMN_CATEGORY = ?",
            arrayOf(category),
            null,
            null,
            "$COLUMN_UPDATED_AT DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                products.add(cursorToProduct(it))
            }
        }

        return products
    }

    /**
     * Search products by name or description
     */
    fun searchProducts(query: String): List<Product> {
        val products = mutableListOf<Product>()
        val db = readableDatabase
        val searchQuery = "%$query%"
        val cursor = db.query(
            TABLE_PRODUCTS,
            null,
            "$COLUMN_NAME LIKE ? OR $COLUMN_DESCRIPTION LIKE ?",
            arrayOf(searchQuery, searchQuery),
            null,
            null,
            "$COLUMN_UPDATED_AT DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                products.add(cursorToProduct(it))
            }
        }

        return products
    }

    /**
     * Get product by ID
     */
    fun getProductById(productId: String): Product? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_PRODUCTS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(productId),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                return cursorToProduct(it)
            }
        }

        return null
    }

    /**
     * Get all distinct categories
     */
    fun getAllCategories(): List<String> {
        val categories = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT DISTINCT $COLUMN_CATEGORY FROM $TABLE_PRODUCTS ORDER BY $COLUMN_CATEGORY",
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                val category = it.getString(it.getColumnIndexOrThrow(COLUMN_CATEGORY))
                if (!category.isNullOrEmpty()) {
                    categories.add(category)
                }
            }
        }

        return categories
    }

    /**
     * Get product count
     */
    fun getProductCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_PRODUCTS", null)
        cursor.use {
            if (it.moveToFirst()) {
                return it.getInt(0)
            }
        }
        return 0
    }

    /**
     * Delete all products
     */
    fun deleteAllProducts(): Int {
        val db = writableDatabase
        return db.delete(TABLE_PRODUCTS, null, null)
    }

    /**
     * Delete a product
     */
    fun deleteProduct(productId: String): Int {
        val db = writableDatabase
        return db.delete(TABLE_PRODUCTS, "$COLUMN_ID = ?", arrayOf(productId))
    }

    /**
     * Convert cursor to Product object
     */
    private fun cursorToProduct(cursor: Cursor): Product {
        return Product(
            id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
            description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)) ?: "",
            price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
            category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)) ?: "",
            imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)) ?: "",
            sku = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SKU)) ?: "",
            stockLevel = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOCK_LEVEL)),
            salesCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SALES_COUNT)),
            createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)),
            updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT))
        )
    }
}
