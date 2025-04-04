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

@HiltViewModel
class CurrentRentalsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val bikeRepository: BikeRepository,
    navigator: Navigator
) : BaseViewModel(navigator) {

    private val _currentRentalDisplays = MutableStateFlow<List<PastRentalDisplay>>(emptyList())
    val currentRentalDisplays: StateFlow<List<PastRentalDisplay>> = _currentRentalDisplays

    init {
        loadCurrentRentals()
    }

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
                    val activeRentals = rentals.filter { it.status == "active" }
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
                    _currentRentalDisplays.value = displays
                }
        }
    }
}