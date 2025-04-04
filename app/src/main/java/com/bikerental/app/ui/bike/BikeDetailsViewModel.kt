package com.bikerental.app.ui.bike

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
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel class for handling the bike details screen logic.
 *
 * This class is responsible for managing bike-related data, including fetching bike details,
 * displaying owner information, handling rental processes, and navigating between screens.
 * The ViewModel interacts with the [BikeRepository] and [UserRepository] for data management.
 *
 * @constructor Creates an instance of [BikeDetailsViewModel].
 * @param bikeRepository The repository responsible for managing bike-related data.
 * @param userRepository The repository responsible for managing user-related data.
 * @param navigator The navigator responsible for handling screen navigation.
 * @param savedStateHandle The saved state handle used to retrieve the bikeId from the saved state.
 */
@HiltViewModel
class BikeDetailsViewModel @Inject constructor(
    private val bikeRepository: BikeRepository,
    private val userRepository: UserRepository,
    val navigator: Navigator,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /**
     * The ID of the bike being viewed.
     */
    private val bikeId = savedStateHandle.get<String>("bikeId") ?: ""

    /**
     * Mutable state flow for the bike details.
     * Holds the bike details that are fetched from the repository.
     */
    private val _bikeDetails = MutableStateFlow<Bike?>(null)

    /**
     * Immutable state flow for the bike details.
     * Exposes the current state of the bike details to the UI.
     */
    val bikeDetails: StateFlow<Bike?> = _bikeDetails.asStateFlow()

    /**
     * Mutable state flow for the owner details.
     * Holds the details of the owner of the bike.
     */
    private val _ownerDetails = MutableStateFlow<User?>(null)

    /**
     * Immutable state flow for the owner details.
     * Exposes the current state of the owner details to the UI.
     */
    val ownerDetails: StateFlow<User?> = _ownerDetails.asStateFlow()

    /**
     * Mutable state flow for the loading status.
     * Tracks the loading state to show loading indicators.
     */
    private val _isLoading = MutableStateFlow(true)

    /**
     * Immutable state flow for the loading status.
     * Exposes the current state of the loading status to the UI.
     */
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Mutable state flow for the error message.
     * Holds any error messages that need to be displayed in the UI.
     */
    private val _errorMessage = MutableStateFlow<String?>(null)

    /**
     * Immutable state flow for the error message.
     * Exposes the current state of the error message to the UI.
     */
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Mutable state flow for showing the confirmation dialog.
     * Tracks whether the confirmation dialog should be shown for renting the bike.
     */
    private val _showConfirmationDialog = MutableStateFlow(false)

    /**
     * Immutable state flow for showing the confirmation dialog.
     * Exposes the current state of the confirmation dialog to the UI.
     */
    val showConfirmationDialog: StateFlow<Boolean> = _showConfirmationDialog.asStateFlow()

    /**
     * Initiates the rental confirmation process.
     * Triggers the display of the confirmation dialog.
     */
    fun confirmRentBike() {
        _showConfirmationDialog.value = true
    }

    /**
     * Confirms the bike rental and proceeds with the rental process.
     * Hides the confirmation dialog and calls the rental logic.
     */
    fun rentBikeConfirmed() {
        _showConfirmationDialog.value = false
        rentBike() // Call your existing rentBike function
    }

    /**
     * Cancels the bike rental process and hides the confirmation dialog.
     */
    fun cancelRentBike() {
        _showConfirmationDialog.value = false
    }

    /**
     * Initializes the ViewModel by fetching the bike details.
     * This is called when the ViewModel is first created.
     */
    init {
        fetchBikeDetails()
    }

    /**
     * Fetches the details of the bike from the repository.
     * Updates the [bikeDetails] and [ownerDetails] state flows.
     */
    private fun fetchBikeDetails() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 🛠️ Assuming getBikeDetailsById returns a Bike?
                val bike = bikeRepository.getBikeDetailsById(bikeId)
                _bikeDetails.value = bike
                bike.ownerId.let { fetchOwnerDetails(it) }
            } catch (e: Exception) {
                _bikeDetails.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Fetches the details of the bike owner from the repository.
     * Updates the [ownerDetails] state flow.
     *
     * @param ownerId The unique identifier of the bike owner.
     */
    private fun fetchOwnerDetails(ownerId: String) {
        viewModelScope.launch {
            userRepository.getUserById(ownerId).collect { user ->
                _ownerDetails.value = user
            }
        }
    }

    /**
     * Navigates back to the previous screen.
     */
    fun navigateBack() {
        navigator.navigateBack()
    }

    /**
     * Initiates the bike rental process.
     * Calls the repository to create a rental and navigates back on success.
     */
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