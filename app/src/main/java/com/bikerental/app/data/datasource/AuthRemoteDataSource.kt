package com.bikerental.app.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth
) {
    val fetchCurrentUser: FirebaseUser? get() = auth.currentUser

    suspend fun firebaseLogin(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            throw Exception("Login failed: ${e.message}")
        }
    }

    suspend fun signUp(email: String, password: String) {
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            throw Exception("Sign up failed: ${e.message}")
        }
    }

    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    suspend fun deleteUserAccount() {

    }

    fun signOut() {
        auth.currentUser?.let { user ->
            if (user.isAnonymous) {
                user.delete()
            }
        }
        auth.signOut()
    }

    suspend fun deleteAccount() {
        try {
            auth.currentUser?.delete()?.await()
        } catch (e: Exception) {
            throw Exception("Account deletion failed: ${e.message}")
        }
    }
}