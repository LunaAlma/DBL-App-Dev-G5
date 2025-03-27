package com.bikerental.app.data.repository

import com.bikerental.app.data.model.BikeRental
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BikeRentalRepository @Inject constructor() {
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun addBikeRental(bikeRental: BikeRental): Result<String> {
        return try {
            val docRef = db.collection("bike_rentals").document()
            val bikeRentalWithId = bikeRental.copy(id = docRef.id)
            docRef.set(bikeRentalWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBikeRentals(): Result<List<BikeRental>> {
        return try {
            val snapshot = db.collection("bike_rentals").get().await()
            val bikeRentals = snapshot.toObjects(BikeRental::class.java)
            Result.success(bikeRentals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBikeRentalById(id: String): Result<BikeRental?> {
        return try {
            val doc = db.collection("bike_rentals").document(id).get().await()
            val bikeRental = doc.toObject(BikeRental::class.java)
            Result.success(bikeRental)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 