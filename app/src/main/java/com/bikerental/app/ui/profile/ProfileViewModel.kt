package com.bikerental.app.ui.profile

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.bikerental.app.data.model.User
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Destination
import com.bikerental.app.ui.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


@HiltViewModel
class ProfileViewModel @Inject constructor(
    navigator: Navigator,
    private val authRepository: AuthRepository,
    private val bikeRepository: BikeRepository,
    private val userRepository: UserRepository
) : BaseViewModel(navigator) {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    init {
       loadUserDetails()
    }
companion object {
    const val TAG = "ProfileViewModel"
}

/*    var userName by mutableStateOf("Firstname Lastname")
    var userEmail by mutableStateOf("example@student.tue.nl")
    var userProfilePicture by mutableStateOf("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png")*/

//    private fun loadUserDetails() {
//        viewModelScope.launch {
//            userRepository.getUserDetails()
//                .onStart { _isLoading.value = true }
//                .catch { e ->
//                    _error.value = e.message
//                    _isLoading.value = false
//                }
//                .collect { user ->
//                    _user.value = user
//                    _isLoading.value = false
//                }
//        }
//    }
// ProfileViewModel.kt
    private fun loadUserDetails() {
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

    fun onLogout() {
        authRepository.logout()
        navigator.navigateTo(Destination.Login.route, true)
    }

    fun deleteAccount() {
        // TODO: Implement account deletion logic
    }
}

