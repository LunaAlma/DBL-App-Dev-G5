package com.bikerental.app.data.repositories

import com.google.firebase.auth.FirebaseUser
import com.bikerental.app.data.datasource.AuthRemoteDataSource
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource
) {
    val getCurrentUser: FirebaseUser? = authRemoteDataSource.fetchCurrentUser

    suspend fun firebaseLogin(email: String, password: String) {
        authRemoteDataSource.firebaseLogin(email, password)
    }

    suspend fun firebaseSignUp(email: String, password: String) {
        authRemoteDataSource.firebaseSignUp(email, password)
    }

    suspend fun sendPasswordResetEmail(email: String) {
        authRemoteDataSource.sendPasswordResetEmail(email)
    }

    fun logout() {
        authRemoteDataSource.signOut()
    }

    suspend fun deleteAccount() {
        authRemoteDataSource.deleteAccount()
    }
}