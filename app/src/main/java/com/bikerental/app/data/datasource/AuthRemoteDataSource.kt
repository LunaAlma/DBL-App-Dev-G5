package com.bikerental.app.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Data source implementation for Firebase Authentication operations.
 *
 * Responsibilities:
 * - Provides direct access to Firebase Authentication services
 * - Handles all authentication-related API calls
 * - Converts Firebase callbacks to coroutine-friendly operations
 * - Manages current user state
 *
 * @property auth Injected instance of FirebaseAuth
 */
class AuthRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth
) {
    /**
     * Gets the currently authenticated Firebase user.
     *
     * @return Current FirebaseUser if logged in, null otherwise.
     *         This property reflects real-time authentication state.
     */
    val fetchCurrentUser: FirebaseUser? get() = auth.currentUser

    /**
     * Authenticates a user with email and password.
     *
     * @param email User's email address
     * @param password User's password
     * @throws Exception with descriptive message if authentication fails
     */
    suspend fun firebaseLogin(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            throw Exception("Login failed: ${e.message}")
        }
    }

    /**
     * Creates a new user account with email and password.
     *
     * @param email User's email address
     * @param password User's password (minimum 6 characters)
     * @throws Exception with descriptive message if account creation fails
     */
    suspend fun firebaseSignUp(email: String, password: String) {
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            throw Exception("Sign up failed: ${e.message}")
        }
    }

    /**
     * Sends a password reset email to the specified address.
     *
     * @param email The email address to send reset instructions to
     * @throws Exception if the email isn't registered or sending fails
     */
    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    /**
     * Signs out the current user with special handling for anonymous users.
     *
     * Behavior:
     * - For authenticated users: Simply signs out
     * - For anonymous users: Deletes the anonymous account before signing out
     */
    fun signOut() {
        auth.currentUser?.let { user ->
            if (user.isAnonymous) {
                user.delete()
            }
        }
        auth.signOut()
    }

    /**
     * Permanently deletes the currently authenticated user account.
     *
     * @throws Exception if no user is logged in or deletion fails
     * @throws IllegalStateException if called when no user is authenticated
     */
    suspend fun deleteAccount() {
        try {
            auth.currentUser?.delete()?.await()
        } catch (e: Exception) {
            throw Exception("Account deletion failed: ${e.message}")
        }
    }
}