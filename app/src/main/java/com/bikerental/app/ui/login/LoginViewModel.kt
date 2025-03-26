package com.bikerental.app.ui.login

import androidx.core.util.PatternsCompat
import com.bikerental.app.data.datasource.AuthRemoteDataSource
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Destination
import com.bikerental.app.ui.navigation.Navigator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@HiltViewModel
class LoginViewModel @Inject constructor(
    navigator: Navigator,
    private val authRepository: AuthRepository,
    private val auth: FirebaseAuth
) : BaseViewModel(navigator) {

    companion object {
        const val TAG = "LoginViewModel"
    }

    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _emailError = MutableStateFlow("")
    private val _passwordError = MutableStateFlow("")

    val email = _email.asStateFlow()
    val password = _password.asStateFlow()
    val emailError = _emailError.asStateFlow()
    val passwordError = _passwordError.asStateFlow()

    fun onEmailChange(input: String) {
        _email.tryEmit(input)
        if (emailError.value.isNotEmpty()) _emailError.tryEmit("")
    }

    fun onPasswordChange(input: String) {
        _password.tryEmit(input)
        if (passwordError.value.isNotEmpty()) _passwordError.tryEmit("")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun basicLogin() {
        if(validate()) {
            launchFirebase {
                authRepository.firebaseLogin(email.value, password.value)
                if(auth.currentUser != null) {
                    withContext(Dispatchers.Main) {
                        navigator.navigateTo(Destination.Home.route, true)
                    }
                } else {
                    // TODO SHOW ERROR
                }
            }
        }
    }

    fun switchSignUp() {
        navigator.navigateTo(Destination.SignUp.route)
    }

    private fun validate(): Boolean {
        var error = false
        if (!email.value.isValidEmail()) _emailError.tryEmit("Invalid Email").run { error = true }
        if (password.value.length < 6) _passwordError.tryEmit("Password length should be at least 6")
            .run { error = true }
        return !error
    }

    fun String.isValidEmail() =
        this.isNotEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()
}