package com.bikerental.app.ui.search

import com.bikerental.app.data.model.Bike
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.ui.navigation.Destination
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

@HiltViewModel
class SearchViewModel @Inject constructor(
    navigator: Navigator,
    private val bikeRepository: BikeRepository,
    private val auth: FirebaseAuth
) : BaseViewModel(navigator) {

    private val _bikes = MutableStateFlow<List<Bike>>(emptyList())
    val bikes = _bikes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _selectedCity = MutableStateFlow("")
    val selectedCity = _selectedCity.asStateFlow()

    private val _selectedStartDate = MutableStateFlow<Timestamp?>(null)
    val selectedStartDate = _selectedStartDate.asStateFlow()

    private val _selectedEndDate = MutableStateFlow<Timestamp?>(null)
    val selectedEndDate = _selectedEndDate.asStateFlow()


    fun onSelectedStartDateChange(timestamp: Timestamp) {
        _selectedStartDate.tryEmit(timestamp)
    }

    init {
        loadBikes()
    }

    private fun loadBikes() {
        launchFirebase {
            _isLoading.value = true
            try {
                bikeRepository.getBikes().collect { bikes ->
                    _bikes.value = bikes.filter { it.ownerId != auth.currentUser?.uid }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
                // Handle error
            }
        }
    }

    fun goToBikeDetails(uid: String) {
        navigator.navigateTo(Destination.Home.BikeDetails.route + uid)
    }
}