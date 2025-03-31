package com.bikerental.app.data.repositories

import com.google.firebase.auth.FirebaseUser
import com.bikerental.app.data.datasource.AuthRemoteDataSource
import javax.inject.Inject

/**
 * Repository layer for authentication operations.
 *
 * Responsibilities:
 * - Acts as abstraction layer between domain/business logic and data sources
 * - Centralizes authentication-related operations for the application
 * - Provides clean API for ViewModels/UseCases to interact with authentication services
 * - Manages data source coordination (though currently only uses remote source)
 *
 * @property authRemoteDataSource Injected data source for Firebase authentication operations
 */
class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource
) {
    /**
     * Provides the currently authenticated Firebase user.
     *
     * This is a direct pass-through to the remote data source's current user state.
     *
     * @return Current authenticated [FirebaseUser] if available, null otherwise.
     *         Value changes automatically based on authentication state.
     */
    val getCurrentUser: FirebaseUser? = authRemoteDataSource.fetchCurrentUser

    /**
     * Authenticates a user with email/password credentials.
     *
     * @param email User's registered email address
     * @param password User's password
     * @throws Exception Wrapped authentication error from Firebase with original message
     * @return No return value - success indicates user is now logged in
     */
    suspend fun firebaseLogin(email: String, password: String) {
        authRemoteDataSource.firebaseLogin(email, password)
    }

    /**
     * Creates a new user account with email/password credentials.
     *
     * @param email Email address to register
     * @param password Password (must meet Firebase complexity requirements)
     * @throws Exception Wrapped account creation error from Firebase
     * @return No return value - success indicates user is created and logged in
     */
    suspend fun firebaseSignUp(email: String, password: String) {
        authRemoteDataSource.firebaseSignUp(email, password)
    }

    /**
     * Initiates password reset process for a registered email.
     *
     * @param email Registered email address to send reset instructions
     * @throws Exception If email isn't registered or sending fails
     */
    suspend fun sendPasswordResetEmail(email: String) {
        authRemoteDataSource.sendPasswordResetEmail(email)
    }

    /**
     * Terminates the current user session with cleanup logic.
     *
     * Handles special case for anonymous users by deleting their temporary account.
     * After execution, [getCurrentUser] will return null.
     */
    fun logout() {
        authRemoteDataSource.signOut()
    }

    /**
     * Permanently deletes the currently authenticated user account.
     *
     * @throws IllegalStateException If no user is currently authenticated
     * @throws Exception If account deletion fails due to permissions or network issues
     */
    suspend fun deleteAccount() {
        authRemoteDataSource.deleteAccount()
    }
}