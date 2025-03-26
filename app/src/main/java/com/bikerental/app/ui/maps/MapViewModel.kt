package com.bikerental.app.ui.maps

import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    navigator: Navigator,
    private val authRepository: AuthRepository
) : BaseViewModel(navigator) {
    companion object {
        const val TAG = "MapViewModel"
    }

}