package com.bikerental.app.ui.search

import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.bikerental.app.data.repositories.BikeRepository

@HiltViewModel
class SearchViewModel @Inject constructor(
    navigator: Navigator,
    private val bikeRepository: BikeRepository,

) : BaseViewModel(navigator) {
    companion object {
        const val TAG = "SearchViewModel"
    }


    private val _selectedCity = MutableStateFlow("")

    val selectedCity = _selectedCity.asStateFlow()
}