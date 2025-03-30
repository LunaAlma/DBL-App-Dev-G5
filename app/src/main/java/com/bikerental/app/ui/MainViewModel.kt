package com.bikerental.app.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Navigator

/**
 * Primary ViewModel for the main screen of the application.
 *
 * Responsibilities:
 * - Handles business logic for the main/root composable
 * - Manages navigation events through the injected Navigator
 * - Coordinates data loading and UI state for the main screen
 *
 * Inherits common ViewModel functionality from BaseViewModel.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    navigator: Navigator

) : BaseViewModel(navigator) {
    init {
        viewModelScope.launch {
        }
    }
}