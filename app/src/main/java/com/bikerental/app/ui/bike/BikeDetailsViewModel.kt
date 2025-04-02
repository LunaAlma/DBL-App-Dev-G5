package com.bikerental.app.ui.bike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bikerental.app.data.model.Bike
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Navigator
import javax.inject.Inject
import kotlinx.coroutines.launch

class BikeDetailsViewModel  @Inject constructor(
    navigator: Navigator,
    private val bikeRepository: BikeRepository,
    private val auth: AuthRepository
) : BaseViewModel(navigator) {
    private val _bike = MutableLiveData<Bike>()
    val bike: LiveData<Bike> = _bike

    fun getBikeDetails(bikeId: String) {
        viewModelScope.launch {
            val bike = bikeRepository.getBikeDetailsById(bikeId)
            _bike.postValue(bike)
        }
    }
}