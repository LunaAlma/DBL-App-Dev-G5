package com.bikerental.app.ui.bike

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikerental.app.data.model.Bike
import com.bikerental.app.data.repositories.BikeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BikeDetailsViewModel @Inject constructor(
    private val bikeRepository: BikeRepository
) : ViewModel() {
    private val _bikeDetails = MutableStateFlow<Bike?>(null)
    val bikeDetails: StateFlow<Bike?> = _bikeDetails.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchBikeDetails(bikeId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _bikeDetails.value = bikeRepository.getBikeDetailsById(bikeId)
            } catch (e: Exception) {
                // Handle error
                _bikeDetails.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}