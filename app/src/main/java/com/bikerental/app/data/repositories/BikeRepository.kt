package com.bikerental.app.data.repositories

import android.net.Uri
import com.bikerental.app.data.datasource.FirebaseDataSource
import com.bikerental.app.data.model.Bike
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp

class BikeRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {
    fun getBikes(): Flow<List<Bike>> = firebaseDataSource.getBikes()

    suspend fun getBikeDetailsById(bikeId: String): Bike = firebaseDataSource.fetchBikeById(bikeId)

    suspend fun addBike(uuid: String, ownerId: String, bikePrice: Double, bikeName: String, city: String, bikeImageUrl: String, startDate: Timestamp, endDate: Timestamp) =
        firebaseDataSource.createBikeDocument(uuid, ownerId, bikeName, bikePrice, city, bikeImageUrl)

    fun removeBike(bike: Bike) = firebaseDataSource.deleteBike(bike)

    suspend fun updateBikeLocation(bikeId: String, newLocation: LatLng) {
        firebaseDataSource.updateBikeLocation(bikeId, newLocation)
    }

    suspend fun addBikeImage(imageUri: Uri): Result<String> {
        return firebaseDataSource.uploadBikeImage(imageUri)
    }

    fun getBikeByCity(city: String): Flow<List<Bike>> {
        return firebaseDataSource.fetchBikesByCity(city)
    }

    fun getBikeByOwner(ownerId: String): Flow<List<Bike>> {
        return firebaseDataSource.fetchBikesByOwner(ownerId)
    }

    fun getAvailableBikes(startTime: Timestamp, endTime: Timestamp): Flow<List<Bike>> {
        return firebaseDataSource.fetchAvailableBikes(startTime, endTime)
    }

    fun getAvailableBikes2(startTime: Timestamp, endTime: Timestamp): Flow<List<Bike>> {
        return firebaseDataSource.getAvailableBikes(startTime, endTime)
    }

    fun getAvailableBikesByCity(city: String, startTime: Timestamp, endTime: Timestamp): Flow<List<Bike>> {
        return firebaseDataSource.fetchAvailableBikesByCity(city, startTime, endTime)
    }

    suspend fun deleteUsersBikes(userId: String) {
        firebaseDataSource.deleteUsersBikes(userId)
    }
}
