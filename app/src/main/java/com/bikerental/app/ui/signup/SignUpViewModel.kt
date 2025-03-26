package com.bikerental.app.ui.signup

import androidx.core.util.PatternsCompat
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Destination
import com.bikerental.app.ui.navigation.Navigator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    navigator: Navigator,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : BaseViewModel(navigator) {
    companion object {
        const val TAG = "SignUpViewModel"
    }

    private val _name = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _nameError = MutableStateFlow("")
    private val _emailError = MutableStateFlow("")
    private val _passwordError = MutableStateFlow("")

    val name = _name.asStateFlow()
    val email = _email.asStateFlow()
    val password = _password.asStateFlow()
    val nameError = _nameError.asStateFlow()
    val emailError = _emailError.asStateFlow()
    val passwordError = _passwordError.asStateFlow()

    fun onNameChange(input: String) {
        _name.tryEmit(input)
        if (nameError.value.isNotEmpty()) _nameError.tryEmit("")
    }

    fun onEmailChange(input: String) {
        _email.tryEmit(input)
        if (emailError.value.isNotEmpty()) _emailError.tryEmit("")
    }

    fun onPasswordChange(input: String) {
        _password.tryEmit(input)
        if (passwordError.value.isNotEmpty()) _passwordError.tryEmit("")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun basicSignUp() {
        if(validate()) {
            launchFirebase {
                authRepository.firebaseSignUp(email.value, password.value)
                if(auth.currentUser != null) {
                    val uid = auth.currentUser!!.uid
                    userRepository.createUserDocument(uid, name.value, email.value)

                    withContext(Dispatchers.Main) {
                        navigator.navigateTo(Destination.Home.route, true)
                    }
                } else {
//                    showErrorSnackbar(ErrorMessage.IdError(R.string.passwords_do_not_match))

                }
            }
        }
    }

    fun switchLogin() {
        navigator.navigateTo(Destination.Login.route)
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