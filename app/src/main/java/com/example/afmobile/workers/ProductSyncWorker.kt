package com.example.afmobile.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.afmobile.data.ProductRepository

/**
 * Background worker to sync products from Firebase periodically
 */
class ProductSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val TAG = "ProductSyncWorker"

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting product sync...")

            val repository = ProductRepository(applicationContext)
            val success = repository.syncProductsFromFirebase()

            if (success) {
                Log.d(TAG, "Product sync completed successfully")
                Result.success()
            } else {
                Log.w(TAG, "Product sync completed with warnings")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Product sync failed: ${e.message}", e)
            Result.retry()
        }
    }
}
