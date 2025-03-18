package com.bikerental.app.data.repositories

import com.bikerental.app.data.datasource.FirebaseDataSource
import com.bikerental.app.data.model.Bike
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class BikeRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {
    fun getBikes(): Flow<List<Bike>> = firebaseDataSource.getBikes()

    suspend fun createBike(bike: Bike) = firebaseDataSource.addBike(bike)

    suspend fun deleteBike(bike: Bike) = firebaseDataSource.deleteBike(bike)
}