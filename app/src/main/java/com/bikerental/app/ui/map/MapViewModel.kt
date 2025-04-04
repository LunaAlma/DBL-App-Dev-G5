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

@HiltViewModel
class MapViewModel @Inject constructor(
    navigator: Navigator,
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val bikeRepository: BikeRepository, // Added private val!
    private val userRepository: UserRepository,
) : BaseViewModel(navigator) {


    val bikeId: String = savedStateHandle.get<String>("bikeId") ?: ""

    val addedBike = MutableStateFlow<Bike?>(null)

    init {
        loadBikeData()
    }

    fun loadBikeData() {
        Log.d("MapViewModel", "Loading bike data for bikeId: $bikeId")
        launchFirebase {
            bikeRepository.getBikes().collect { bikes ->
                addedBike.value = bikes.firstOrNull { it.bikeId == bikeId }
            }
        }
        Log.d("MapViewModel", "Bike data loaded: $addedBike")
    }

    // Classify bikes as a StateFlow with an initial empty list
    val bikes: StateFlow<List<Bike>> = bikeRepository.getBikes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _availableBikes: MutableStateFlow<List<Bike>> = MutableStateFlow(emptyList())
    val availableBikes: StateFlow<List<Bike>> = _availableBikes

    // Assuming you are calling the method to fetch available bikes with these time parameters
    var startTime: Timestamp = Timestamp.now()  // Set this to the required value
    var endTime: Timestamp = Timestamp.now()    // Set this to the required value

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


    // Mutable state to track selected ownerID
    private val _ownerID = MutableStateFlow<String?>(null)

    // Fetch user details when ownerID changes
    @OptIn(ExperimentalCoroutinesApi::class)
    val ownerDetails: StateFlow<User?> = _ownerID
        .filterNotNull()
        .flatMapLatest { userId ->
            userRepository.getUserById(userId)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Update ownerId dynamically
    fun setOwnerId(ownerID: String) {
        _ownerID.value = ownerID
    }

    fun onSearchBarClick() {
        navigator.navigateTo(Destination.Home.Search.route)
    }

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

}