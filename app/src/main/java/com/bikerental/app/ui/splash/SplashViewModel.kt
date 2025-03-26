package com.bikerental.app.ui.splash

import androidx.lifecycle.viewModelScope
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Destination
import com.bikerental.app.ui.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    navigator: Navigator,
    private val authRepository: AuthRepository
) : BaseViewModel(navigator) {
    companion object {
        const val TAG = "SplashViewModel"
    }

    init {
        viewModelScope.launch {
            delay(1000)

            val exists = authRepository.getCurrentUser != null
            if (exists) {
                navigator.navigateTo(Destination.Home.route, true)
            } else {
                navigator.navigateTo(Destination.SignUp.route, true)
            }
        }
    }
}