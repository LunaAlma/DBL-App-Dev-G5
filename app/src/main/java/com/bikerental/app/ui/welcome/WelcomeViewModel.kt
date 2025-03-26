package com.bikerental.app.ui.welcome

import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    navigator: Navigator,
): BaseViewModel(navigator) {
    companion object {
        const val TAG = "WelcomeViewModel"
    }
}