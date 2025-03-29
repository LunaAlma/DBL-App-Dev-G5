package com.bikerental.app.data.repositories

import com.bikerental.app.data.datasource.AuthRemoteDataSource
import com.bikerental.app.data.datasource.FirebaseDataSource
//import com.bikerental.app.data.local.UserPreferences
import com.bikerental.app.data.model.Bike
import com.bikerental.app.data.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val firebaseDataSource: FirebaseDataSource
) {
    suspend fun createUserDocument(uid: String, name: String, email: String) =
        firebaseDataSource.createUserDocument(uid, name, email)

    fun getUsers(): Flow<List<User>> = firebaseDataSource.getUsers()

    fun getUserDetails(): Flow<User> = firebaseDataSource.getUserDetails()

    fun getUserById(uid: String): Flow<User> = firebaseDataSource.fetchUserById(uid)

    suspend fun updateUserDetails(user: User) = firebaseDataSource.updateUserDetails(user)

}