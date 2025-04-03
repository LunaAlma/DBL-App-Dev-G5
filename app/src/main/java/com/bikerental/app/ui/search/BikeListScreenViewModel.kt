package com.bikerental.app.ui.search

import androidx.lifecycle.SavedStateHandle
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class BikeListScreenViewModel @Inject constructor(
    navigator: Navigator,
    private val savedStateHandle: SavedStateHandle,
    private val bikeRepository: BikeRepository
) : BaseViewModel(navigator) {
    val bikeId: String = savedStateHandle.get<String>("bikeId") ?: ""
    private val _bikeId = MutableStateFlow<String?>(null)
    fun setBikeId(bikeId: String) {
        //savedStateHandle["bikeId"] = bikeId
        _bikeId.value = bikeId
    }
    fun goToBikeDetails(bikeId: String){
        navigator.navigateTo("bike_details/$bikeId")

    }
}