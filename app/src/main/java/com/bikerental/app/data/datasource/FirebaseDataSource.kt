package com.bikerental.app.data.datasource

import android.net.Uri
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
        city: String,
        ) {
        db.collection("bikes").document(uuid).set(
            Bike(
                bikeId = uuid,
                ownerId = ownerId,
                bikeName = bikeName,
                city = city,
                price = 5,
                imageUrl = "",
                location = GeoPoint(0.0, 0.0),
                startTime = Timestamp.now(),
                endTime = Timestamp.now(),
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
     * @param uid Bike ID to fetch
     * @return Flow<Bike> emitting bike data updates
     * @throws IllegalStateException if bike doesn't exist
     */
    fun fetchBikeById(uid: String): Flow<Bike> = callbackFlow {
        val subscription = db.collection("bikes").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val bike = snapshot?.toObject(Bike::class.java)
                if(bike != null) {
                    trySend(bike)
                } else {
                    close(IllegalStateException("User document not found"))
                }
            }
        awaitClose { subscription.remove() }
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
            val downloadUrl = imageRef.downloadUrl
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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

}
