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

@HiltViewModel
class PastRentalsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val bikeRepository: BikeRepository,
    navigator: Navigator
) : BaseViewModel(navigator) {

    private val _pastRentalDisplays = MutableStateFlow<List<PastRentalDisplay>>(emptyList())
    val pastRentalDisplays: StateFlow<List<PastRentalDisplay>> = _pastRentalDisplays

    init {
        loadPastRentals()
    }

//    private fun loadPastRentals() {
//        viewModelScope.launch { // Now using proper coroutine scope
//            val userId = authRepository.getCurrentUser?.uid ?: return@launch
//            userRepository.getUserRentals(userId)
//                .catch { _ -> /* Handle error silently */ }
//                .collect { rentals ->
//                    _rentals.value = rentals
//                        .filter { it.status == "completed" }
//                }
//        }
//    }
private fun loadPastRentals() {
    viewModelScope.launch {
        val userId = authRepository.getCurrentUser?.uid ?: run {
            Log.e("PastRentals", "User not logged in")
            return@launch
        }
        Log.d("PastRentals", "Loading rentals for user: $userId")

        userRepository.getUserRentals(userId)
            .catch { e -> Log.e("PastRentals", "Error: ${e.message}") }
            .collect { rentals ->
                val completedRentals = rentals.filter { it.status == "completed" }
                // For each rental, fetch the bike details and create a display model
                val displays = completedRentals.map { rental ->
                    // Collect the first (and only) Bike value from the Flow
                    val bike = bikeRepository.getBikeDetailsById(rental.bikeId)
                    PastRentalDisplay(
                        bikeName = bike.bikeName,
                        bikeCity = bike.city,
                        startTime = rental.startTime,
                        endTime = rental.endTime,
                        status = rental.status
                    )
                }
                _pastRentalDisplays.value = displays
            }
    }
}
}