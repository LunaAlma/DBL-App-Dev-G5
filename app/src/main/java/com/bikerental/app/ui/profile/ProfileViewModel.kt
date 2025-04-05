package com.bikerental.app.ui.profile

import android.net.Uri
import android.util.Log
import com.bikerental.app.data.model.User
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Destination
import com.bikerental.app.ui.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel for managing the user's profile in the application.
 * Handles user profile data, image updates, and navigation actions.
 *
 * @param navigator The Navigator used for navigation between screens.
 * @param authRepository The repository for authentication-related actions.
 * @param userRepository The repository for user data-related actions.
 * @param bikeRepository The repository for managing bikes associated with the user.
 * @param auth The Firebase authentication instance for managing user sessions.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    navigator: Navigator,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val bikeRepository: BikeRepository,
    private val auth: FirebaseAuth
) : BaseViewModel(navigator) {

    // StateFlow to hold user data
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    // StateFlow for form-related fields like name, email, loading state, and profile image URI
    private val _name = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _profileImageUri = MutableStateFlow<Uri?>(null)

    val name = _name.asStateFlow()
    val email = _email.asStateFlow()
    val isLoading = _isLoading.asStateFlow()

    /**
     * Updates the profile image of the user.
     * Uploads the new image and updates the user's image URL in the repository.
     *
     * @param uri The URI of the selected profile image.
     */
    fun onProfileImageChange(uri: Uri) {
        _profileImageUri.value = uri

        launchFirebase {
            val imageUri = _profileImageUri.value ?: throw Exception("Profile image is required")
            val uploadResult = userRepository.addProfileImage(imageUri)
            val downloadUrl = uploadResult.getOrElse { throw Exception("Profile image upload failed") }
            val uid = authRepository.getCurrentUser?.uid ?: throw Exception("User is not authenticated")

            // Update the user profile image in the repository
            userRepository.updateUserImage(uid, downloadUrl)
        }
    }

    /**
     * Handles changes to the user's name and updates it in the repository.
     *
     * @param input The new name input from the user.
     */
    fun onNameChanged(input: String) {
        launchFirebase {
            auth.currentUser?.let { user ->
                userRepository.updateUserName(user.uid, input)
            }
        }
    }

    /**
     * Initializes the ViewModel and loads the user details.
     */
    init {
        loadUserDetails()
    }

    /**
     * Loads the user's details from the repository.
     * If the user is authenticated, it fetches the user data based on the user's ID.
     * Otherwise, sets a default error message.
     */
    fun loadUserDetails() {
        viewModelScope.launch {
            authRepository.getCurrentUser?.uid?.let { uid ->
                userRepository.getUserById(uid)
                    .catch { e: Throwable ->
                        Log.e("ProfileVM", "Error: ${e.message}")
                        _user.value = User(name = "Error loading profile ${uid}")
                    }
                    .collect { user: User ->
                        _user.value = user
                    }
            } ?: run {
                _user.value = User(name = "Not authenticated")
            }
        }
    }

    /**
     * Navigates to the past rentals screen.
     */
    fun navigateToPastRentals() {
        navigator.navigateTo(Destination.Home.PastRentals.route)
    }

    /**
     * Navigates to the user's bikes screen.
     */
    fun navigateToMyBikes() {
        navigator.navigateTo(Destination.Home.MyBikes.route)
    }

    /**
     * Navigates to the profile details screen.
     */
    fun navigateToDetails() {
        navigator.navigateTo(Destination.Home.ProfileDetails.route)
    }

    /**
     * Navigates to the current rentals screen.
     */
    fun navigateToCurrentRentals() {
        navigator.navigateTo(Destination.Home.CurrentRentals.route)
    }

    /**
     * Logs the user out and navigates to the login screen.
     */
    fun onLogout() {
        authRepository.logout()
        navigator.navigateTo(Destination.Login.route, true)
    }

    /**
     * Deletes the user's account and associated data.
     * Removes all bikes related to the user and deletes the user details from the repository.
     * Finally, the user is deleted from Firebase and navigates to the sign-up screen.
     */
    fun onDeleteAccount() {
        launchFirebase {
            val user = auth.currentUser
            val userId = user!!.uid

            // Delete user's bikes and details from the repository
            bikeRepository.deleteUsersBikes(userId)
            userRepository.deleteUserDetails(userId)
            user.delete().await()

            // Navigate to the sign-up screen after account deletion
            navigator.navigateTo(Destination.SignUp.route, true)
        }
    }
}
