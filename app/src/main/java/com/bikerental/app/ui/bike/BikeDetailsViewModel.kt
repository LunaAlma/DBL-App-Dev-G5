package com.bikerental.app.ui.bike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikerental.app.data.model.Bike
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.ui.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class BikeDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bikeRepository: BikeRepository
) : ViewModel() {
    val bikeId: String = savedStateHandle.get<String>("bikeId") ?: "Unknown"

    // Optionally, you can load further details based on bikeId here.
}

//class BikeDetailsViewModel  @Inject constructor(
//    private val savedStateHandle: SavedStateHandle,
//    navigator: Navigator,
//    private val bikeRepository: BikeRepository,
//    private val auth: AuthRepository
//) : BaseViewModel(navigator) {
//    val bikeId: String = savedStateHandle.get<String>("bikeId") ?: ""
//    val otherBike = MutableStateFlow<Bike?>(null)
//
//    init {
//        loadBikeData()
//    }
//    private fun loadBikeData() {
//        launchFirebase {
//            bikeRepository.getBikes().collect { bikes ->
//                otherBike.value = bikes.firstOrNull { it.bid == bikeId }
//            }
//        }
//
//    }
//}