package com.bikerental.app.data.datasource

import android.net.Uri
import com.bikerental.app.data.model.Bike
import com.bikerental.app.data.model.Transaction
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
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class FirebaseDataSource @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage

){
    suspend fun createUserDocument(uid: String, name: String, email: String) {
        db.collection("users").document(uid).set(
            User(
                uid = uid,
                name = name,
                email = email
            )
        ).await()
    }

    suspend fun createBikeDocument(uuid: String, ownerId: String, bikeName: String, city: String, description: String) {
        db.collection("bikes").document(uuid).set(
            Bike(
                bikeId = uuid,
                ownerId = ownerId,
                bikeName = bikeName,
                city = city,
                description = description,
            )
        ).await()
    }

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

    fun getUserDetails(): Flow<User> = callbackFlow {
        val subscription = db.collection("users").document(auth.currentUser!!.uid)
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

    suspend fun updateUserDetails(user: User) {
        if(auth.currentUser?.uid != null) {
            db.collection("users").document(auth.currentUser!!.uid).set(user).await()
        }
    }

    fun getBikes(): Flow<List<Bike>> = callbackFlow {
        val subscription = db.collection("bike_rentals")
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

    fun deleteBike(bike: Bike) {
        db.collection("bikes").document(bike.bikeId).delete()
    }

    suspend fun updateBikeLocation(bikeId: String, newLocation: LatLng) {
        db.collection("bikes").document(bikeId)
            .update("location", GeoPoint(newLocation.latitude, newLocation.longitude))
            .await()
    }

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

}