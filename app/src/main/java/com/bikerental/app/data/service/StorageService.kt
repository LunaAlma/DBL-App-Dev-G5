package com.bikerental.app.data.service

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageService @Inject constructor() {
    private val storage: FirebaseStorage = Firebase.storage

    suspend fun uploadBikeImage(imageUri: Uri): Result<String> {
        return try {
            val imageRef = storage.reference
                .child("bike_images")
                .child("${UUID.randomUUID()}.jpg")

            imageRef.putFile(imageUri).await()
            val downloadUrl = imageRef.downloadUrl.await()
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 