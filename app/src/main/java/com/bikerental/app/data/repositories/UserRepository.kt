package com.bikerental.app.data.repositories

import com.bikerental.app.data.datasource.AuthRemoteDataSource
import com.bikerental.app.data.datasource.FirebaseDataSource
import com.bikerental.app.data.model.Bike
import com.bikerental.app.data.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource
) {

//    suspend fun getUserDetails(userId: String): User {
//        return firebaseDataSource.getUserDetails(userId)
//    }

    fun getUsers(): Flow<List<User>> = firebaseDataSource.getUsers()

    suspend fun updateUserDetails(user: User) = firebaseDataSource.updateUserDetails(user)

    suspend fun createBike(bike: Bike) = firebaseDataSource.addBike(bike)
}