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


@HiltViewModel
class ProfileViewModel @Inject constructor(
    navigator: Navigator,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val bikeRepository: BikeRepository,
    private val auth: FirebaseAuth
) : BaseViewModel(navigator) {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    // Form state flows
    private val _name = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _profileImageUri = MutableStateFlow<Uri?>(null)

    val name = _name.asStateFlow()
    val email = _email.asStateFlow()
    val isLoading = _isLoading.asStateFlow()

    fun onProfileImageChange(uri: Uri) {
        _profileImageUri.value = uri

        launchFirebase {
            val imageUri = _profileImageUri.value ?: throw Exception("Profile image is required")
            val uploadResult = userRepository.addProfileImage(imageUri)
            val downloadUrl = uploadResult.getOrElse { throw Exception("Profile image upload failed") }
            val uid = authRepository.getCurrentUser?.uid ?: throw Exception("User is not authenticated")

            userRepository.updateUserImage(uid, downloadUrl)
        }
    }

    /**
     * Handles name field changes and clears related errors
     */
    fun onNameChanged(input: String) {
        launchFirebase {
            auth.currentUser?.let { user ->
                userRepository.updateUserName(user.uid, input)
            }
        }
    }

    init {
       loadUserDetails()
    }

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

    fun navigateToPastRentals() {
        navigator.navigateTo(Destination.Home.PastRentals.route)
    }

    fun navigateToMyBikes() {
        navigator.navigateTo(Destination.Home.MyBikes.route)
    }

    fun navigateToDetails() {
        navigator.navigateTo(Destination.Home.ProfileDetails.route)
    }
    fun navigateToCurrentRentals() {
        navigator.navigateTo(Destination.Home.CurrentRentals.route)
    }

    fun onLogout() {
        authRepository.logout()
        navigator.navigateTo(Destination.Login.route, true)
    }

    fun onDeleteAccount() {
        launchFirebase {
            val user = auth.currentUser
            val userId = user!!.uid

            bikeRepository.deleteUsersBikes(userId)
            userRepository.deleteUserDetails(userId)
            user.delete().await()

            navigator.navigateTo(Destination.SignUp.route, true)
        }
    }
}

