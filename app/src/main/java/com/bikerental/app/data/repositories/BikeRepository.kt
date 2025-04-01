package com.bikerental.app.data.repositories

import android.net.Uri
import com.bikerental.app.data.datasource.FirebaseDataSource
import com.bikerental.app.data.model.Bike
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.google.android.gms.maps.model.LatLng

class BikeRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {
    fun getBikes(): Flow<List<Bike>> = firebaseDataSource.getBikes()

    fun getBikeDetailsById(bikeId: String): Flow<Bike> = firebaseDataSource.fetchBikeById(bikeId)

    suspend fun addBike(uuid: String, ownerId: String, bikePrice: Double, bikeName: String, city: String, bikeImageUrl: String) =
        firebaseDataSource.createBikeDocument(uuid, ownerId, bikeName, bikePrice, city, bikeImageUrl)

    fun removeBike(bike: Bike) = firebaseDataSource.deleteBike(bike)

    suspend fun updateBikeLocation(bikeId: String, newLocation: LatLng) {
        firebaseDataSource.updateBikeLocation(bikeId, newLocation)
    }

    suspend fun addBikeImage(imageUri: Uri): Result<String> {
        return firebaseDataSource.uploadBikeImage(imageUri)
    }
}