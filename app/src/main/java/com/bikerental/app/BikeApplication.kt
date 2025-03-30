package com.bikerental.app

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp


/**
 * Main application class for the Bike Rental application.
 *
 * Responsibilities:
 * - Initializes application-wide dependencies
 * - Sets up Firebase services
 * - Serves as the entry point for Hilt dependency injection
 *
 * This class is created when the application process starts and remains
 * throughout the app's lifecycle.
 */
@HiltAndroidApp
class BikeApplication : Application() {

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects have been created.
     *
     * Performs:
     * 1. Superclass initialization
     * 2. Firebase initialization
     */
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase services with the application context
        FirebaseApp.initializeApp(this)
    }
}