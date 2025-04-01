package com.bikerental.app.data.repositories

import android.net.Uri
import com.bikerental.app.data.datasource.FirebaseDataSource
import com.bikerental.app.data.model.Rental
import com.bikerental.app.data.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository class for user-related data operations.
 *
 * Acts as an abstraction layer between the data source and domain layers,
 * providing clean API for user data operations while encapsulating
 * the data source implementation details.
 *
 * Responsibilities:
 * - Managing user document creation in the database
 * - Providing streams of user data
 * - Serving as single source of truth for user-related data
 *
 * @property firebaseDataSource The underlying data source that handles
 *                              actual Firebase operations
 */
class UserRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {
    /**
     * Creates a new user document in the database.
     *
     * @param uid Unique identifier for the user (from Firebase Auth)
     * @param name Full name of the user
     * @param email Email address of the user
     * @param profileImageUrl Profile picture of the user
     * @throws Exception if document creation fails
     */
    suspend fun createUserDocument(uid: String, name: String, email: String, profileImageUrl: String) =
        firebaseDataSource.createUserDocument(uid, name, email, profileImageUrl)

    /**
     * Gets a stream of all users in the database.
     *
     * @return Flow emitting List<User> that updates automatically when
     *         the user collection changes
     */
    fun getUsers(): Flow<List<User>> = firebaseDataSource.getUsers()

    /**
     * Gets a stream of a specific user's data by their UID.
     *
     * @param uid The user ID to look up
     * @return Flow<User> that emits the current user data and updates
     *         when the document changes
     */
    fun getUserById(uid: String): Flow<User> = firebaseDataSource.fetchUserById(uid)

    fun getUserRentals(userId: String): Flow<List<Rental>> {
        return firebaseDataSource.getUserRentals(userId)
    }

    suspend fun addProfileImage(imageUri: Uri): Result<String> {
        return firebaseDataSource.uploadProfileImage(imageUri)
    }

    suspend fun deleteUserDetails(uid: String) {
        firebaseDataSource.deleteUserDetails(uid)
    }
}