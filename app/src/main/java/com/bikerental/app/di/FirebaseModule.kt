package com.bikerental.app.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides Firebase service instances.
 *
 * This module makes core Firebase services available for dependency injection
 * throughout the application. All provided dependencies are singletons.
 *
 * Services Provided:
 * 1. FirebaseAuth - For authentication operations
 * 2. FirebaseFirestore - For database operations
 * 3. FirebaseStorage - For file storage operations
 *
 * Usage:
 * Simply annotate constructor parameters with @Inject in classes where
 * these services are needed.
 */
@Module // Marks this as a Dagger module
@InstallIn(SingletonComponent::class) // Installs these providers in the application component
object FirebaseModule {

    /**
     * Provides a singleton instance of Firebase Authentication.
     *
     * @return FirebaseAuth instance configured with the default FirebaseApp
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Provides a singleton instance of Cloud Firestore.
     *
     * @return FirebaseFirestore instance configured with the default FirebaseApp
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Provides a singleton instance of Firebase Storage.
     *
     * @return FirebaseStorage instance configured with the default FirebaseApp
     */
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
} 