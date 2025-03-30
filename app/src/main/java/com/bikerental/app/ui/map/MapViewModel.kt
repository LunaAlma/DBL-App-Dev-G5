package com.bikerental.app.ui.map

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
import kotlinx.coroutines.launch

@HiltViewModel
class MapViewModel @Inject constructor(
    navigator: Navigator,
    private val authRepository: AuthRepository,
    private val bikeRepository: BikeRepository, // Added private val!
    private val userRepository: UserRepository,
) : BaseViewModel(navigator) {

    // Classify bikes as a StateFlow with an initial empty list
    val bikes: StateFlow<List<Bike>> = bikeRepository.getBikes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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
            bikeRepository.updateBikeLocation(bikeId, newLocation)
        }
    }

}