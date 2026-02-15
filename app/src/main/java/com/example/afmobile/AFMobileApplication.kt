package com.example.afmobile

import android.app.Application

/**
 * Application class for AFMobile
 * SQLite database is accessed directly through ProductDatabaseHelper in repositories
 */
class AFMobileApplication : Application() {


    override fun onCreate() {
        super.onCreate()
    }
}
