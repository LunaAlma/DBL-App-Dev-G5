package com.bikerental.app.ui.pastrentals

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.bikerental.app.data.model.PastRentalDisplay
import com.bikerental.app.data.model.Rental
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.ui.navigation.Navigator
import com.bikerental.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing past rental data.
 *
 * This ViewModel interacts with the user repository to fetch the past rentals for the
 * currently authenticated user and filters the rentals that have been marked as "completed".
 * It also interacts with the bike repository to fetch details for each bike in the completed rentals.
 *
 * @param userRepository The repository that provides user-related data.
 * @param authRepository The repository that manages authentication and current user data.
 * @param bikeRepository The repository that provides bike-related data.
 * @param navigator The navigator used to handle navigation events in the app.
 */
@HiltViewModel
class PastRentalsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val bikeRepository: BikeRepository,
    navigator: Navigator
) : BaseViewModel(navigator) {

    // Private mutable state flow for storing past rental displays
    private val _pastRentalDisplays = MutableStateFlow<List<PastRentalDisplay>>(emptyList())

    // Public immutable state flow for observing past rental displays
    val pastRentalDisplays: StateFlow<List<PastRentalDisplay>> = _pastRentalDisplays

    // Initialize and load past rentals on ViewModel creation
    init {
        loadPastRentals()
    }

    /**
     * Loads the past rentals for the currently authenticated user.
     *
     * The function fetches the rentals from the `UserRepository` and filters the rentals
     * based on the status ("completed"). For each completed rental, the corresponding bike details
     * are fetched from the `BikeRepository` to create a `PastRentalDisplay` object that is
     * used for displaying the rental information in the UI.
     */
    fun loadPastRentals() {
        viewModelScope.launch {
            // Retrieve the user ID of the currently authenticated user
            val userId = authRepository.getCurrentUser?.uid ?: run {
                Log.e("PastRentals", "User not logged in")
                return@launch
            }

            // Log the user ID for debugging purposes
            Log.d("PastRentals", "Loading rentals for user: $userId")

            // Fetch rentals for the user
            userRepository.getUserRentals(userId)
                .catch { e -> Log.e("PastRentals", "Error: ${e.message}") } // Handle errors during the fetch
                .collect { rentals ->
                    // Filter rentals to only include completed rentals
                    val completedRentals = rentals.filter { it.status == "completed" }

                    // Map each completed rental to a display model with bike details
                    val displays = completedRentals.map { rental ->
                        // Fetch the bike details for the rental's bike ID
                        val bike = bikeRepository.getBikeDetailsById(rental.bikeId)

                        // Create a PastRentalDisplay object for the rental
                        PastRentalDisplay(
                            bikeName = bike.bikeName,
                            bikeCity = bike.city,
                            startTime = rental.startTime,
                            endTime = rental.endTime,
                            status = rental.status
                        )
                    }

                    // Update the state flow with the new list of past rental displays
                    _pastRentalDisplays.value = displays
                }
        }
    }
}
