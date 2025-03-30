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

/**
 * ViewModel for the splash screen that:
 * - Checks authentication state after a brief delay
 * - Routes to appropriate screens (Home or SignUp)
 * - Handles dependency injection for auth services
 *
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    navigator: Navigator,
    private val authRepository: AuthRepository
) : BaseViewModel(navigator) {

    init {
        viewModelScope.launch {
            // Brief delay for branding visibility (1000ms)
            delay(1000)
            // Check current auth state
            val exists = authRepository.getCurrentUser != null

            // Navigate based on auth status
            if (exists) {
                // Clear back stack and go to home if authenticated
                navigator.navigateTo(Destination.Home.route, true)
            } else {
                // Clear back stack and go to signup if unauthenticated
                navigator.navigateTo(Destination.SignUp.route, true)
            }
        }
    }
}