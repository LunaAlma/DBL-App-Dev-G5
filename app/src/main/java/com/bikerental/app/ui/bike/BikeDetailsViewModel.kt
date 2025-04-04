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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BikeDetailsViewModel @Inject constructor(
    private val bikeRepository: BikeRepository,
    private val userRepository: UserRepository,
    val navigator: Navigator,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bikeId = savedStateHandle.get<String>("bikeId") ?: ""

    private val _bikeDetails = MutableStateFlow<Bike?>(null)
    val bikeDetails: StateFlow<Bike?> = _bikeDetails.asStateFlow()

    private val _ownerDetails = MutableStateFlow<User?>(null)
    val ownerDetails: StateFlow<User?> = _ownerDetails.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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
}
