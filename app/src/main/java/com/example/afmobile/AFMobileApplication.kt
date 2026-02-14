package com.example.afmobile

import android.app.Application
import com.example.afmobile.data.AppDatabase

/**
 * Application class for AFMobile
 */
class AFMobileApplication : Application() {

    // Lazy initialization of database
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}
