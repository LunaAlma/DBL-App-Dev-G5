package com.bikerental.app.ui.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikerental.app.data.model.Bike
import com.bikerental.app.data.model.User
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    bikeRepository: BikeRepository,
    private val userRepository: UserRepository
) : ViewModel() {
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
            userRepository.getUserDetailsBasedOnID(userId)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Update ownerId dynamically
    fun setOwnerId(ownerID: String) {
        _ownerID.value = ownerID
    }
}
