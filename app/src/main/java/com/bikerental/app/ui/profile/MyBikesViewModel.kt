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

/**
 * ViewModel responsible for managing the user's bikes.
 *
 * This ViewModel handles the logic for fetching, displaying, and deleting the bikes owned by the current user.
 * It communicates with the BikeRepository to fetch and delete bikes from the database and manages loading states.
 * It also exposes state flows for observing the bikes and loading indicators.
 *
 * @param navigator The navigator to handle navigation-related tasks.
 * @param bikeRepository The repository responsible for fetching and managing bike data.
 * @param db The Firestore database instance.
 * @param userRepository The repository responsible for managing user data.
 * @param auth The FirebaseAuth instance for handling authentication.
 */
@HiltViewModel
class MyBikesViewModel @Inject constructor(
    navigator: Navigator,
    private val bikeRepository: BikeRepository,
    private val db: FirebaseFirestore,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : BaseViewModel(navigator) {

    /**
     * Mutable state flow to track the loading state.
     */
    private val _isLoading = MutableStateFlow(false)

    /**
     * Exposed state flow for observing the loading state.
     */
    val isLoading = _isLoading.asStateFlow()

    /**
     * Mutable state flow to hold the list of bikes for the current user.
     */
    private val _bikes = MutableStateFlow<List<Bike>>(emptyList())

    /**
     * Exposed state flow for observing the list of bikes owned by the current user.
     */
    val bikes: StateFlow<List<Bike>> = _bikes

    /**
     * Initializes the ViewModel by loading the bikes for the current user.
     */
    init {
        loadBikes()
    }

    /**
     * Fetches the bikes for the current user.
     *
     * This function checks if the user is authenticated and fetches the bikes owned by the current user
     * using the BikeRepository. It updates the loading state during the fetching process.
     */
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

    /**
     * Deletes a bike from the database.
     *
     * This function calls the BikeRepository to remove the specified bike from the database.
     * It doesn't need to manually update the bikes list as the Flow will automatically refresh.
     *
     * @param bike The bike to delete.
     */
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
