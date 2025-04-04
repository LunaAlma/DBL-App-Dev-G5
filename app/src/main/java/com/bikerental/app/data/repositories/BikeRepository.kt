package com.bikerental.app.data.repositories

import android.net.Uri
import com.bikerental.app.data.datasource.FirebaseDataSource
import com.bikerental.app.data.model.Bike
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp

/**
 * Repository class that handles all bike-related data operations and interactions with the Firebase data source.
 *
 * This class provides methods to manage bikes, including fetching bike details, adding new bikes,
 * removing bikes, updating bike locations, and more. It acts as a middle layer between the data source
 * (Firebase) and the application logic.
 *
 * @constructor Creates an instance of BikeRepository.
 * @param firebaseDataSource The data source used for fetching and managing bike-related data.
 */
class BikeRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {

    /**
     * Retrieves a flow of all bikes.
     *
     * @return A [Flow] of a list of [Bike] objects.
     */
    fun getBikes(): Flow<List<Bike>> = firebaseDataSource.getBikes()

    /**
     * Fetches the details of a bike by its ID.
     *
     * @param bikeId The unique identifier for the bike.
     * @return A [Bike] object containing details about the specified bike.
     */
    suspend fun getBikeDetailsById(bikeId: String): Bike = firebaseDataSource.fetchBikeById(bikeId)

    /**
     * Adds a new bike to the Firebase database.
     *
     * @param uuid The unique identifier for the bike.
     * @param ownerId The unique identifier for the bike's owner.
     * @param bikePrice The price of the bike per day.
     * @param bikeName The name of the bike.
     * @param city The city where the bike is located.
     * @param bikeImageUrl The URL of the bike's image.
     * @param startDate The start date of the bike's availability.
     * @param endDate The end date of the bike's availability.
     */
    suspend fun addBike(
        uuid: String,
        ownerId: String,
        bikePrice: Double,
        bikeName: String,
        city: String,
        bikeImageUrl: String,
        startDate: Timestamp,
        endDate: Timestamp
    ) = firebaseDataSource.createBikeDocument(
        uuid = uuid,
        ownerId = ownerId,
        bikeName = bikeName,
        bikePrice = bikePrice,
        city = city,
        bikeImageUrl = bikeImageUrl,
        startTime = startDate,
        endTime = endDate
    )

    /**
     * Removes a bike from the Firebase database.
     *
     * @param bike The [Bike] object to be removed.
     */
    fun removeBike(bike: Bike) = firebaseDataSource.deleteBike(bike)

    /**
     * Updates the location of a bike in the Firebase database.
     *
     * @param bikeId The unique identifier of the bike to be updated.
     * @param newLocation The new location (latitude and longitude) of the bike.
     */
    suspend fun updateBikeLocation(bikeId: String, newLocation: LatLng) {
        firebaseDataSource.updateBikeLocation(bikeId, newLocation)
    }

    /**
     * Uploads a bike image to Firebase and returns the image URL.
     *
     * @param imageUri The URI of the image to be uploaded.
     * @return A [Result] containing the URL of the uploaded image, or an error.
     */
    suspend fun addBikeImage(imageUri: Uri): Result<String> {
        return firebaseDataSource.uploadBikeImage(imageUri)
    }

    /**
     * Retrieves bikes available in a specific city.
     *
     * @param city The city where to search for bikes.
     * @return A [Flow] of a list of [Bike] objects available in the specified city.
     */
    fun getBikeByCity(city: String): Flow<List<Bike>> {
        return firebaseDataSource.fetchBikesByCity(city)
    }

    /**
     * Retrieves bikes owned by a specific user.
     *
     * @param ownerId The unique identifier of the bike's owner.
     * @return A [Flow] of a list of [Bike] objects owned by the specified user.
     */
    fun getBikeByOwner(ownerId: String): Flow<List<Bike>> {
        return firebaseDataSource.fetchBikesByOwner(ownerId)
    }

    /**
     * Retrieves bikes available during a specified time range.
     *
     * @param startTime The start date of the rental period.
     * @param endTime The end date of the rental period.
     * @return A [Flow] of a list of available [Bike] objects.
     */
    fun getAvailableBikes(startTime: Timestamp, endTime: Timestamp): Flow<List<Bike>> {
        return firebaseDataSource.fetchAvailableBikes(startTime, endTime)
    }

    /**
     * Retrieves bikes available during a specified time range.
     *
     * @param startTime The start date of the rental period.
     * @param endTime The end date of the rental period.
     * @return A [Flow] of a list of available [Bike] objects.
     */
    fun getAvailableBikes2(startTime: Timestamp, endTime: Timestamp): Flow<List<Bike>> {
        return firebaseDataSource.getAvailableBikes(startTime, endTime)
    }

    /**
     * Retrieves bikes available in a specific city during a specified time range.
     *
     * @param city The city where to search for available bikes.
     * @param startTime The start date of the rental period.
     * @param endTime The end date of the rental period.
     * @return A [Flow] of a list of available [Bike] objects.
     */
    fun getAvailableBikesByCity(city: String, startTime: Timestamp, endTime: Timestamp): Flow<List<Bike>> {
        return firebaseDataSource.fetchAvailableBikesByCity(city, startTime, endTime)
    }

    /**
     * Deletes all bikes belonging to a specific user.
     *
     * @param userId The unique identifier of the user whose bikes are to be deleted.
     */
    suspend fun deleteUsersBikes(userId: String) {
        firebaseDataSource.deleteUsersBikes(userId)
    }

    /**
     * Creates a rental for a bike.
     *
     * @param bikeId The unique identifier of the bike being rented.
     * @param startTime The start date of the rental period.
     * @param endTime The end date of the rental period.
     */
    suspend fun rentBike(bikeId: String, startTime: Timestamp, endTime: Timestamp) {
        firebaseDataSource.createRental(bikeId, startTime, endTime)
    }
}
