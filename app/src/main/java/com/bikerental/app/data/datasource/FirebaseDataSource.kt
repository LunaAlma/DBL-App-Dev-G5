package com.bikerental.app.data.datasource

import android.net.Uri
import android.util.Log
import com.bikerental.app.data.model.Bike
import com.bikerental.app.data.model.Rental
import com.bikerental.app.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.firestore.GeoPoint
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.UUID

/**
 * Firebase Data Source implementation handling all Firestore and Storage operations.
 *
 * Responsibilities:
 * - Manages CRUD operations for Users and Bikes in Firestore
 * - Handles bike image uploads to Firebase Storage
 * - Provides real-time data streams using Flow
 * - Abstracts all Firebase-specific implementations
 *
 * @property db Firestore database instance
 * @property auth Firebase Authentication instance
 * @property storage Firebase Storage instance
 */
class FirebaseDataSource @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage

) {
    /**
     * Creates a new user document in Firestore.
     *
     * @param uid Unique user ID from Firebase Auth
     * @param name User's full name
     * @param email User's email address
     * @param profileImageUrl User's profile picture
     * @throws Exception if document creation fails
     */
    suspend fun createUserDocument(uid: String, name: String, email: String, profileImageUrl: String) {
        db.collection("users").document(uid).set(
            User(
                uid = uid,
                name = name,
                email = email,
                profileImageUrl = profileImageUrl
            )
        ).await()
    }

    /**
     * Creates a new bike document in Firestore.
     *
     * @param uuid Unique bike ID
     * @param ownerId ID of the bike owner (user)
     * @param bikeName Display name of the bike
     * @param city City where the bike is located
     * @throws Exception if document creation fails
     */
    suspend fun createBikeDocument(
        uuid: String,
        ownerId: String,
        bikeName: String,
        bikePrice: Double,
        city: String,
        bikeImageUrl: String,
        startTime: Timestamp,
        endTime: Timestamp
        ) {
        db.collection("bikes").document(uuid).set(
            Bike(
                bikeId = uuid,
                ownerId = ownerId,
                bikeName = bikeName,
                price = bikePrice,
                city = city,
                imageUrl = bikeImageUrl,
                location = GeoPoint(0.0, 0.0),
                startTime = startTime,  // Use the provided startTime
                endTime = endTime
            )
        ).await()
    }

    /**
     * Gets a real-time stream of all users.
     *
     * @return Flow emitting List<User> that updates whenever the users collection changes
     * @throws Exception if listener registration fails
     */
    fun getUsers(): Flow<List<User>> = callbackFlow {
        val subscription = db.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val users = snapshot?.toObjects(User::class.java) ?: emptyList()
                trySend(users)
            }

        awaitClose { subscription.remove() }
    }

    /**
     * Gets a real-time stream of a specific user's data.
     *
     * @param uid User ID to fetch
     * @return Flow<User> that emits when the user document changes
     * @throws IllegalStateException if user document doesn't exist
     */
    fun fetchUserById(uid: String): Flow<User> = callbackFlow {
        val subscription = db.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val user = snapshot?.toObject(User::class.java)
                if(user != null) {
                    trySend(user)
                } else {
                    close(IllegalStateException("User document not found"))
                }
            }
        awaitClose { subscription.remove() }
    }

    /**
     * Gets a real-time stream of all bikes.
     *
     * @return Flow<List<Bike>> emitting current bike collection
     */
    fun getBikes(): Flow<List<Bike>> = callbackFlow {
        val subscription = db.collection("bikes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val bikes = snapshot?.toObjects(Bike::class.java) ?: emptyList()
                trySend(bikes)
            }
        awaitClose { subscription.remove() }
    }

    /**
     * Gets a real-time stream of a specific bike's data.
     *
     * @param uuid Bike ID to fetch
     * @return Bike object
     * @throws IllegalStateException if bike doesn't exist
     */
    suspend fun fetchBikeById(uuid: String): Bike {
        return try {
            val document = db.collection("bikes").document(uuid).get().await()
            document.toObject(Bike::class.java) ?: throw Exception("Bike not found")
        } catch (e: Exception) {
            throw Exception("Failed to fetch bike: ${e.message}")
        }
    }

    suspend fun fetchBikesByUser(userId: String): List<Bike> {
        return try {
            // Query the "bikes" collection where the "owner" field matches the given userId.
            val querySnapshot = db.collection("bikes")
                .whereEqualTo("owner", userId)
                .get()
                .await()

            // Map each document to a Bike object, filtering out any that cannot be converted.
            querySnapshot.documents.mapNotNull { it.toObject(Bike::class.java) }
        } catch (e: Exception) {
            throw Exception("Failed to fetch bikes: ${e.message}")
        }
    }


    /**
     * Deletes a bike document from Firestore.
     *
     * @param bike Bike object to delete
     */
    fun deleteBike(bike: Bike) {
        db.collection("bikes").document(bike.bikeId).delete()
    }

    /**
     * Updates a bike's location in Firestore.
     *
     * @param bikeId ID of bike to update
     * @param newLocation New LatLng coordinates
     * @throws Exception if update fails
     */
    suspend fun updateBikeLocation(bikeId: String, newLocation: LatLng) {
        db.collection("bikes").document(bikeId)
            .update("location", GeoPoint(newLocation.latitude, newLocation.longitude))
            .await()
    }

    /**
     * Uploads a bike image to Firebase Storage.
     *
     * @param imageUri URI of image to upload
     * @return Result<String> containing download URL on success
     */
    suspend fun uploadBikeImage(imageUri: Uri): Result<String> {
        return try {
            val imageRef = storage.reference
                .child("bike_images")
                .child("${UUID.randomUUID()}.jpg")

            imageRef.putFile(imageUri).await()

            val downloadUrl = imageRef.downloadUrl.await().toString()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Uploads a profile image to Firebase Storage.
     *
     * @param imageUri URI of image to upload
     * @return Result<String> containing download URL on success
     */
    suspend fun uploadProfileImage(imageUri: Uri): Result<String> {
        return try {
            val imageRef = storage.reference
                .child("profile_images")
                .child("${UUID.randomUUID()}.jpg")

            imageRef.putFile(imageUri).await()

            val downloadUrl = imageRef.downloadUrl.await().toString()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserRentals(userId: String): Flow<List<Rental>> = flow {
        val snapshot = Firebase.firestore
            .collection("rentals")
            .whereEqualTo("renterId", userId)
            .get()
            .await()

        val rentals = snapshot.documents.mapNotNull { doc ->
            Rental(
                bikeId = doc.getString("bikeId") ?: "",
                renterId = doc.getString("renterId") ?: "",
                ownerId = doc.getString("ownerId") ?: "",
                status = doc.getString("status") ?: "",
                startTime = doc.getTimestamp("startTime") ?: Timestamp.now(),
                endTime = doc.getTimestamp("endTime") ?: Timestamp.now()
            )
        }
        emit(rentals)
    }.flowOn(Dispatchers.IO)

    /**
     * Gets bikes filtered by city.
     *
     * @param city City to filter bikes by
     * @return Flow<List<Bike>> emitting bikes in the specified city
     */
    fun fetchBikesByCity(city: String): Flow<List<Bike>> = callbackFlow {
        val subscription = db.collection("bikes")
            .whereEqualTo("city", city)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val bikes = snapshot?.toObjects(Bike::class.java) ?: emptyList()
                trySend(bikes)
            }
        awaitClose { subscription.remove() }
    }

    /**
     * Gets bikes filtered by owner ID.
     *
     * @param ownerId ID of the bike owner
     * @return Flow<List<Bike>> emitting bikes owned by the specified user
     */
    fun fetchBikesByOwner(ownerId: String): Flow<List<Bike>> = callbackFlow {
        Log.d("FirebaseDebug", "Querying bikes for ownerId: $ownerId")

        val subscription = db.collection("bikes")
            .whereEqualTo("ownerId", ownerId) // Ensure this matches Firestore
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirebaseDebug", "Error: ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot == null || snapshot.isEmpty) {
                    Log.d("FirebaseDebug", "No bikes found for ownerId: $ownerId")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val bikes = snapshot.toObjects(Bike::class.java)
                Log.d("FirebaseDebug", "Fetched bikes: ${bikes.size}")
                trySend(bikes)
            }

        awaitClose { subscription.remove() }
    }


    /**
     * Gets bikes available in a date range (not rented during the specified period).
     *
     * @param startTime Start of availability period
     * @param endTime End of availability period
     * @return Flow<List<Bike>> emitting bikes available during the specified period
     */
    fun fetchAvailableBikes(startTime: Timestamp, endTime: Timestamp): Flow<List<Bike>> = callbackFlow {
        // First get all rentals that overlap with the requested period
        val rentalsQuery = db.collection("rentals")
            .whereLessThan("startTime", endTime)
            .whereGreaterThan("endTime", startTime)

        rentalsQuery.get().addOnSuccessListener { rentalsSnapshot ->
            // Get IDs of all bikes that are rented during this period
            val unavailableBikeIds = rentalsSnapshot.documents
                .mapNotNull { it.getString("bikeId") }
                .toSet()

            // Then get all bikes that are NOT in the unavailable list
            db.collection("bikes")
                .whereNotIn("bikeId", unavailableBikeIds.toList())
                .get()
                .addOnSuccessListener { bikesSnapshot ->
                    val bikes = bikesSnapshot.toObjects(Bike::class.java)
                    trySend(bikes)
                }
                .addOnFailureListener { error ->
                    close(error)
                }
        }.addOnFailureListener { error ->
            close(error)
        }

        awaitClose { /* No subscription to cancel in this implementation */ }
    }

    /**
     * Gets bikes filtered by city and available in a date range.
     *
     * @param city City to filter by
     * @param startTime Start of availability period
     * @param endTime End of availability period
     * @return Flow<List<Bike>> emitting available bikes in the specified city
     */
    fun fetchAvailableBikesByCity(city: String, startTime: Timestamp, endTime: Timestamp): Flow<List<Bike>> = callbackFlow {
        // First get all rentals that overlap with the requested period
        val rentalsQuery = db.collection("rentals")
            .whereLessThan("startTime", endTime)
            .whereGreaterThan("endTime", startTime)

        rentalsQuery.get().addOnSuccessListener { rentalsSnapshot ->
            // Get IDs of all bikes that are rented during this period
            val unavailableBikeIds = rentalsSnapshot.documents
                .mapNotNull { it.getString("bikeId") }
                .toSet()

            // Then get all bikes that are NOT in the unavailable list AND are in the specified city
            db.collection("bikes")
                .whereEqualTo("city", city)
                .whereNotIn("bikeId", unavailableBikeIds.toList())
                .get()
                .addOnSuccessListener { bikesSnapshot ->
                    val bikes = bikesSnapshot.toObjects(Bike::class.java)
                    trySend(bikes)
                }
                .addOnFailureListener { error ->
                    close(error)
                }
        }.addOnFailureListener { error ->
            close(error)
        }

        awaitClose { /* No subscription to cancel in this implementation */ }
    }

    // Fetch all bikes and filter out rented ones based on the bikeId from rentals
    fun getAvailableBikes(startTime: Timestamp, endTime: Timestamp): Flow<List<Bike>> = flow {
        // Step 1: Get all rentals
        Log.d("BikeRepository", "Fetching all rentals from Firestore...")
        val rentalsSnapshot = Firebase.firestore
            .collection("rentals")
            .get()
            .await()

        Log.d("BikeRepository", "Fetched rentals: ${rentalsSnapshot.size()}")

        // Step 2: Extract the bikeIds of rented bikes
        val rentedBikeIds = rentalsSnapshot.documents
            .mapNotNull { it.getString("bikeId") }
            .toSet()

        Log.d("BikeRepository", "Rented bike IDs: $rentedBikeIds")

        // Step 3: Fetch all bikes
        Log.d("BikeRepository", "Fetching all bikes from Firestore...")
        val bikesSnapshot = Firebase.firestore
            .collection("bikes")
            .get()
            .await()

        Log.d("BikeRepository", "Fetched bikes: ${bikesSnapshot.size()}")

        // Step 4: Filter bikes - Remove rented bikes from the list
        val availableBikes = bikesSnapshot.documents
            .mapNotNull { doc ->
                val bikeId = doc.getString("bikeId") ?: return@mapNotNull null
                val startTime = doc.getTimestamp("startTime") ?: return@mapNotNull null
                val endTime = doc.getTimestamp("endTime") ?: return@mapNotNull null
                val location = doc.getGeoPoint("location") ?: return@mapNotNull null  // Ensure location is extracted

                // Log the values we are working with for each bike
                Log.d("BikeRepository", "Processing bikeId: $bikeId, startTime: $startTime, endTime: $endTime, location: $location")

                // Filter out rented bikes (if the bikeId is in rentedBikeIds, skip it)
                if (bikeId !in rentedBikeIds && startTime <= endTime) {
                    Log.d("BikeRepository", "Bike $bikeId is available.")
                    Bike(
                        bikeId = bikeId,
                        bikeName = doc.getString("bikeName") ?: "",
                        city = doc.getString("city") ?: "",
                        price = doc.getDouble("price") ?: 0.0,
                        startTime = startTime,
                        endTime = endTime,
                        location = location, // Ensure location is passed to the Bike object
                        ownerId = doc.getString("ownerId") ?: "", // Fetch ownerId and provide a default value
                        imageUrl = doc.getString("imageUrl") ?: "",
                    )
                } else {
                    Log.d("BikeRepository", "Bike $bikeId is rented or time invalid.")
                    null
                }
            }
            .filter { bike ->
                // Step 5: Filter bikes by the provided timestamp range
                Log.d("BikeRepository", "Checking if bike is within the range: $startTime <= ${bike.startTime} && ${bike.endTime} >= $endTime")
                bike.startTime <= endTime && bike.endTime >= startTime
            }

        Log.d("BikeRepository", "Available bikes after time filtering: $availableBikes")

        emit(availableBikes)
    }.flowOn(Dispatchers.IO)

    /**
     * Gets bikes filtered by owner and available in a date range.
     *
     * @param ownerId Owner ID to filter by
     * @param startTime Start of availability period
     * @param endTime End of availability period
     * @return Flow<List<Bike>> emitting available bikes owned by the specified user
     */
    fun fetchAvailableBikesByOwner(ownerId: String, startTime: Timestamp, endTime: Timestamp): Flow<List<Bike>> = callbackFlow {
        // First get all rentals that overlap with the requested period
        val rentalsQuery = db.collection("rentals")
            .whereLessThan("startTime", endTime)
            .whereGreaterThan("endTime", startTime)

        rentalsQuery.get().addOnSuccessListener { rentalsSnapshot ->
            // Get IDs of all bikes that are rented during this period
            val unavailableBikeIds = rentalsSnapshot.documents
                .mapNotNull { it.getString("bikeId") }
                .toSet()

            // Then get all bikes that are NOT in the unavailable list AND are owned by the specified user
            db.collection("bikes")
                .whereEqualTo("ownerId", ownerId)
                .whereNotIn("bikeId", unavailableBikeIds.toList())
                .get()
                .addOnSuccessListener { bikesSnapshot ->
                    val bikes = bikesSnapshot.toObjects(Bike::class.java)
                    trySend(bikes)
                }
                .addOnFailureListener { error ->
                    close(error)
                }
        }.addOnFailureListener { error ->
            close(error)
        }
        awaitClose { /* No subscription to cancel in this implementation */ }
    }

    suspend fun deleteUsersBikes(userId: String) {
        // Get all bikes where ownerId == userId
        val querySnapshot = db.collection("bikes")
            .whereEqualTo("ownerId", userId)
            .get()
            .await()

        // Delete each bike in batch
        val batch = db.batch()
        for (document in querySnapshot.documents) {
            batch.delete(document.reference)
        }
        batch.commit().await()
    }

    suspend fun deleteUserDetails(uid: String) {
        try {
            Log.d("Deletion", "Starting deletion for user: $uid")

            // 1. Get user document
            val userDoc = db.collection("users").document(uid).get().await()
            Log.d("Deletion", "Retrieved user document")

            // 2. Delete profile image
            val profileImageUrl = userDoc.getString("profileImageUrl")
            profileImageUrl?.let { url ->
                try {
                    Log.d("Deletion", "Attempting to delete profile image: $url")
                    val imageRef = storage.getReferenceFromUrl(url)
                    imageRef.delete().await()
                    Log.d("Deletion", "Successfully deleted profile image")
                } catch (e: Exception) {
                    Log.e("Deletion", "Profile image deletion failed", e)
                }
            }

            // 3. Get all bikes
            val bikesQuery = db.collection("bikes").whereEqualTo("ownerId", uid).get().await()
            Log.d("Deletion", "Found ${bikesQuery.size()} bikes to delete")

            // 4. Delete each bike and its image
            bikesQuery.documents.forEach { bikeDoc ->
                try {
                    Log.d("Deletion", "Processing bike: ${bikeDoc.id}")

                    val bikeImageUrl = bikeDoc.getString("imageUrl")
                    bikeImageUrl?.let { url ->
                        try {
                            Log.d("Deletion", "Attempting to delete bike image: $url")
                            val bikeImageRef = storage.getReferenceFromUrl(url)
                            bikeImageRef.delete().await()
                            Log.d("Deletion", "Successfully deleted bike image")
                        } catch (e: Exception) {
                            Log.e("Deletion", "Bike image deletion failed", e)
                        }
                    }

                    bikeDoc.reference.delete().await()
                    Log.d("Deletion", "Successfully deleted bike document")
                } catch (e: Exception) {
                    Log.e("Deletion", "Error processing bike ${bikeDoc.id}", e)
                }
            }

            // 5. Delete user document
            db.collection("users").document(uid).delete().await()
            Log.d("Deletion", "Successfully deleted user document")

        } catch (e: Exception) {
            Log.e("Deletion", "Critical error in deleteUserDetails", e)
            throw e
        }
    }

    suspend fun updateUserImage(uid: String, imageUrl: String) {
        val userDoc = db.collection("users").document(uid).get().await()

        // Check if there's an existing profile image URL
        val oldImageUrl = userDoc.getString("profileImageUrl")

        // If there is an existing image, delete it from Storage
        oldImageUrl?.let { url ->
            try {
                // Get reference to the old image in Storage
                val oldImageRef = storage.getReferenceFromUrl(url)
                // Delete the file
                oldImageRef.delete().await()
            } catch (e: Exception) {
                // Log error but continue with the update
                Log.e("UserRepository", "Failed to delete old profile image: ${e.message}")
            }
        }
        db.collection("users").document(uid).update("profileImageUrl", imageUrl).await()
    }

    suspend fun updateUserName(uid: String, name: String) {
        db.collection("users").document(uid).update("name", name).await()
    }
}
