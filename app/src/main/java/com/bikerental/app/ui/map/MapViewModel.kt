package com.bikerental.app.ui.map

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bikerental.app.data.model.Bike
import com.bikerental.app.data.model.User
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Destination
import com.bikerental.app.ui.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.util.Date
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.emitAll

/**
 * ViewModel for the Map screen, responsible for managing data and business logic
 * related to bike rentals and user interactions with the map.
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    navigator: Navigator,
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val bikeRepository: BikeRepository,
    private val userRepository: UserRepository,
) : BaseViewModel(navigator) {

    /**
     * The unique identifier of the bike.
     */
    val bikeId: String = savedStateHandle.get<String>("bikeId") ?: ""

    /**
     * Mutable state flow holding bike data for the selected bike.
     */
    val addedBike = MutableStateFlow<Bike?>(null)

    init {
        loadBikeData()
    }

    /**
     * Loads the bike data for the selected bike using the bikeId.
     * Sets the addedBike state with the first bike that matches the bikeId.
     */
    fun loadBikeData() {
        Log.d("MapViewModel", "Loading bike data for bikeId: $bikeId")
        launchFirebase {
            bikeRepository.getBikes().collect { bikes ->
                addedBike.value = bikes.firstOrNull { it.bikeId == bikeId }
            }
        }
        Log.d("MapViewModel", "Bike data loaded: $addedBike")
    }

    /**
     * A flow that emits a list of all bikes.
     */
    val bikes: StateFlow<List<Bike>> = bikeRepository.getBikes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * Mutable state flow holding the list of available bikes based on start and end time.
     */
    private val _availableBikes: MutableStateFlow<List<Bike>> = MutableStateFlow(emptyList())

    /**
     * A flow that emits a list of available bikes based on the start and end time.
     */
    val availableBikes: StateFlow<List<Bike>> = _availableBikes

    /**
     * The start time for the bike rental.
     */
    var startTime: Timestamp = Timestamp.now()

    /**
     * The end time for the bike rental.
     */
    var endTime: Timestamp = Timestamp.now()

    /**
     * Fetches available bikes based on the start and end time.
     *
     * @param startTime The start time for the bike rental.
     * @param endTime The end time for the bike rental.
     */
    fun fetchAvailableBikes(startTime: Timestamp, endTime: Timestamp) {
        this.startTime = startTime
        this.endTime = endTime

        viewModelScope.launch {
            bikeRepository.getAvailableBikes2(startTime, endTime)
                .catch { e ->
                    // Handle error
                }
                .collect { bikes ->
                    _availableBikes.value = bikes
                }
        }
    }

    /**
     * Mutable state flow to track the selected owner's ID.
     */
    private val _ownerID = MutableStateFlow<String?>(null)

    /**
     * A flow that emits the user details for the selected owner when the owner ID changes.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val ownerDetails: StateFlow<User?> = _ownerID
        .filterNotNull()
        .flatMapLatest { userId ->
            userRepository.getUserById(userId)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    /**
     * Sets the owner ID to track the selected owner.
     *
     * @param ownerID The ID of the owner to track.
     */
    fun setOwnerId(ownerID: String) {
        _ownerID.value = ownerID
    }

    /**
     * Navigates to the search screen.
     */
    fun onSearchBarClick() {
        navigator.navigateTo(Destination.Home.Search.route)
    }

    /**
     * Updates the location of the bike.
     *
     * @param bikeId The ID of the bike to update.
     * @param newLocation The new location to set for the bike.
     */
    fun updateBikeLocation(bikeId: String, newLocation: LatLng) {
        viewModelScope.launch {
            try {
                bikeRepository.updateBikeLocation(bikeId, newLocation)
                // Log success
                Log.d("MapViewModel", "Location updated successfully")
            } catch (e: Exception) {
                Log.e("MapViewModel", "Update failed", e)
                // Handle error (e.g., show snackbar)
            }
        }
    }

    /**
     * Navigates back to the map screen.
     */
    fun goToMap() {
        viewModelScope.launch {
            try {
                // Navigate back to Map screen
                navigator.navigateTo(Destination.Home.Map.route)
            } catch (e: Exception) {
                Log.e("Navigation", "Could not go back to Map", e)
            }
        }
    }

    /**
     * Navigates to the bike details screen.
     *
     * @param bikeId The ID of the bike to navigate to.
     */
    fun navigateToBikeDetails(bikeId: String) {
        val route = Destination.Home.BikeDetails.route
            .replace("{bikeId}", bikeId)
        navigator.navigateTo(route)
    }
}
