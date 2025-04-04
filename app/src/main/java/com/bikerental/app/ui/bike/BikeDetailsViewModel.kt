package com.bikerental.app.ui.bike

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikerental.app.data.model.Bike
import com.bikerental.app.data.model.User
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.ui.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BikeDetailsViewModel @Inject constructor(
    private val bikeRepository: BikeRepository,
    private val userRepository: UserRepository,
    val navigator: Navigator,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val bikeId = savedStateHandle.get<String>("bikeId") ?: ""

    private val _bikeDetails = MutableStateFlow<Bike?>(null)
    val bikeDetails: StateFlow<Bike?> = _bikeDetails.asStateFlow()

    private val _ownerDetails = MutableStateFlow<User?>(null)
    val ownerDetails: StateFlow<User?> = _ownerDetails.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _showConfirmationDialog = MutableStateFlow(false)
    val showConfirmationDialog: StateFlow<Boolean> = _showConfirmationDialog.asStateFlow()

    fun confirmRentBike() {
        _showConfirmationDialog.value = true
    }

    fun rentBikeConfirmed() {
        _showConfirmationDialog.value = false
        rentBike() // Call your existing rentBike function
    }

    fun cancelRentBike() {
        _showConfirmationDialog.value = false
    }

    init {
        fetchBikeDetails()
    }

    private fun fetchBikeDetails() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 🛠️ Assuming getBikeDetailsById returns a Bike?
                val bike = bikeRepository.getBikeDetailsById(bikeId)
                _bikeDetails.value = bike
                bike?.ownerId?.let { fetchOwnerDetails(it) }
            } catch (e: Exception) {
                _bikeDetails.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchOwnerDetails(ownerId: String) {
        viewModelScope.launch {
            userRepository.getUserById(ownerId).collect { user ->
                _ownerDetails.value = user
            }
        }
    }

    fun navigateBack() {
        navigator.navigateBack()
    }
    fun rentBike() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val bike = _bikeDetails.value
                if (bike != null) {
                    bikeRepository.rentBike(
                        bikeId = bikeId,
                        startTime = bike.startTime,
                        endTime = bike.endTime
                    )
                    navigator.navigateBack()
                } else {
                    _errorMessage.value = "Bike information not available"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Rental failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

