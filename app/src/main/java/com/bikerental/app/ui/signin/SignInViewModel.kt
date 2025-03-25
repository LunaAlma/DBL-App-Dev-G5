package com.bikerental.app.ui.signin

import android.os.Messenger
import com.bikerental.app.ui.MainViewModel
import com.bikerental.app.data.model.ErrorMessage
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    navigator: Navigator,
    private val authRepository: AuthRepository
) : BaseViewModel(navigator) {

    private val _shouldRestartApp = MutableStateFlow(false)
    val shouldRestartApp: StateFlow<Boolean>
        get() = _shouldRestartApp.asStateFlow()

//    fun signIn(
//        email: String,
//        password: String,
//        showErrorSnackbar: (ErrorMessage) -> Unit
//    ) {
//        launchCatching(showErrorSnackbar) {
//            authRepository.signIn(email, password)
//            _shouldRestartApp.value = true
//        }
//    }
}