package com.bikerental.app.ui.currentrentals

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
 * ViewModel for managing the current rentals.
 *
 * This ViewModel is responsible for loading the current rental data for the logged-in user
 * and exposing it to the UI. It handles filtering the active rentals and formatting them
 * into displayable data, which is then provided to the UI.
 *
 * @param userRepository The repository responsible for user-related data operations.
 * @param authRepository The repository responsible for authentication-related operations.
 * @param bikeRepository The repository responsible for bike-related data operations.
 * @param navigator The navigator used for handling screen navigation.
 */
@HiltViewModel
class CurrentRentalsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val bikeRepository: BikeRepository,
    navigator: Navigator
) : BaseViewModel(navigator) {

    private val _currentRentalDisplays = MutableStateFlow<List<PastRentalDisplay>>(emptyList())
    val currentRentalDisplays: StateFlow<List<PastRentalDisplay>> = _currentRentalDisplays

    /**
     * Initializes the ViewModel and loads the current rentals for the user.
     */
    init {
        loadCurrentRentals()
    }

    /**
     * Loads the current rentals for the logged-in user.
     *
     * This function fetches the current rentals by first getting the logged-in user's ID.
     * It then filters the active rentals and maps them to a list of `PastRentalDisplay` objects.
     * If an error occurs during the data fetching process, it is logged.
     */
    fun loadCurrentRentals() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser?.uid ?: run {
                Log.e("CurrentRentals", "User not logged in")
                return@launch
            }
            Log.d("CurrentRental", "Loading rentals for user: $userId")

            userRepository.getUserRentals(userId)
                .catch { e -> Log.e("CurrentRentals", "Error: ${e.message}") }
                .collect { rentals ->
                    // Filter active rentals
                    val activeRentals = rentals.filter { it.status == "active" }
                    // Map rentals to displayable data
                    val displays = activeRentals.map { rental ->
                        val bike = bikeRepository.getBikeDetailsById(rental.bikeId)
                        PastRentalDisplay(
                            bikeName = bike.bikeName,
                            bikeCity = bike.city,
                            startTime = rental.startTime,
                            endTime = rental.endTime,
                            status = rental.status
                        )
                    }
                    // Update the state with the rental displays
                    _currentRentalDisplays.value = displays
                }
        }
    }
}