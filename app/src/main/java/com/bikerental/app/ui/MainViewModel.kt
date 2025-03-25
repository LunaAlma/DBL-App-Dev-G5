package com.bikerental.app.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Navigator


@HiltViewModel
class MainViewModel @Inject constructor(
    val navigator: Navigator

) : BaseViewModel(navigator) {
    init {
        viewModelScope.launch {

        }
    }
}