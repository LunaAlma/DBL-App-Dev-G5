package com.bikerental.app.ui.profile

import android.util.Log
import com.bikerental.app.data.model.Bike
import com.bikerental.app.data.model.User
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Navigator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class MyBikesViewModel @Inject constructor(
    navigator: Navigator,
    private val bikeRepository: BikeRepository,
    private val db: FirebaseFirestore,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : BaseViewModel(navigator) {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _bikes = MutableStateFlow<List<Bike>>(emptyList())
    val bikes: StateFlow<List<Bike>> = _bikes

//    private val _users = MutableStateFlow<List<User>>(emptyList())
//    val users = _users.asStateFlow()
//
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading = _isLoading.asStateFlow()
//
//    init {
//        loadUsers()
//    }
//
//    private fun loadUsers() {
//        launchFirebase {
//            _isLoading.value = true
//            try {
//                userRepository.getUsers().collect { users ->
//                    _users.value = users.filter { it.uid == auth.currentUser?.uid }
//                    _isLoading.value = false
//                }
//            } catch (e: Exception) {
//                _isLoading.value = false
//                // Handle error
//            }
//        }
//    }


    init {
        loadBikes()
    }

    private fun loadBikes() {
        viewModelScope.launch {
            // Retrieve current user id or log an error and exit if not logged in.
            val userId = auth.currentUser?.uid ?: run {
                Log.e("MyBikesViewModel", "User not logged in")
                return@launch
            }
            Log.d("MyBikesViewModel", "Loading bikes for user: $userId")
            _isLoading.value = true

            bikeRepository.getBikeByOwner(userId)
                .catch { e ->
                    _isLoading.value = false
                    Log.e("MyBikesViewModel", "Error loading bikes: ${e.message}", e)
                }
                .collect { bikes ->
                    // Update the bikes state (no filtering is applied here).
                    _bikes.value = bikes
                    _isLoading.value = false
                    Log.d("MyBikesViewModel", "Fetched ${bikes.size} bikes for user: $userId")
                }
        }
    }

    fun deleteBike(bike: Bike) {
        viewModelScope.launch {
            try {
                bikeRepository.removeBike(bike)
                // No need to update _bikes manually - Flow will auto-refresh
            } catch (e: Exception) {
                Log.e("MyBikesViewModel", "Delete failed", e)
            }
        }
    }

}