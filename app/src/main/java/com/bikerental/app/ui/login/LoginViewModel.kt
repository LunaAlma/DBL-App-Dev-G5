package com.bikerental.app.ui.login

import androidx.core.util.PatternsCompat
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
import kotlinx.coroutines.withContext

/**
 * ViewModel for managing the login flow, including handling user input, validation, and login actions.
 *
 * @param navigator The navigator for navigating between screens.
 * @param authRepository The repository for authentication-related actions.
 * @param auth Firebase authentication instance used to interact with Firebase Authentication.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    navigator: Navigator,
    private val authRepository: AuthRepository,
    private val auth: FirebaseAuth
) : BaseViewModel(navigator) {

    // Internal state to hold the values and errors for email, password, login status, and loading state
    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _emailError = MutableStateFlow("")
    private val _passwordError = MutableStateFlow("")
    private val _loginError = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)

    // Exposed state as immutable Flow to be observed in the UI
    val email = _email.asStateFlow()
    val password = _password.asStateFlow()
    val emailError = _emailError.asStateFlow()
    val passwordError = _passwordError.asStateFlow()
    val loginError = _loginError.asStateFlow()
    val isLoading = _isLoading.asStateFlow()

    /**
     * Handles email input change and clears errors if any.
     *
     * @param input The new email input from the user.
     */
    fun onEmailChange(input: String) {
        _email.tryEmit(input)
        if (emailError.value.isNotEmpty()) _emailError.tryEmit("")
        _loginError.tryEmit("")
    }

    /**
     * Handles password input change and clears errors if any.
     *
     * @param input The new password input from the user.
     */
    fun onPasswordChange(input: String) {
        _password.tryEmit(input)
        if (passwordError.value.isNotEmpty()) _passwordError.tryEmit("")
        _loginError.tryEmit("")
    }

    /**
     * Executes the login process by validating inputs, sending the login request,
     * and navigating to the home screen if successful.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun basicLogin() {
        if (validate()) {
            _isLoading.tryEmit(true)
            _loginError.tryEmit("")

            // Perform Firebase login in a background coroutine
            launchFirebase {
                try {
                    authRepository.firebaseLogin(email.value, password.value)
                    if (auth.currentUser != null) {
                        withContext(Dispatchers.Main) {
                            // Navigate to home screen if login is successful
                            navigator.navigateTo(Destination.Home.route, true)
                        }
                    } else {
                        _loginError.tryEmit("Login failed: User not found")
                    }
                } catch (e: Exception) {
                    _loginError.tryEmit(e.message ?: "Login failed")
                } finally {
                    _isLoading.tryEmit(false)
                }
            }
        }
    }

    /**
     * Sends a password reset email if the email address is valid.
     */
    fun resetPassword() {
        if (email.value.isValidEmail()) {
            launchFirebase {
                authRepository.sendPasswordResetEmail(email.value)
            }
        }
    }

    /**
     * Navigates the user to the sign-up screen.
     */
    fun switchSignUp() {
        navigator.navigateTo(Destination.SignUp.route, true)
    }

    /**
     * Validates the email and password input fields.
     *
     * @return True if the inputs are valid, otherwise false.
     */
    fun validate(): Boolean {
        var error = false
        if (!email.value.isValidEmail()) _emailError.tryEmit("Invalid Email").run { error = true }
        if (password.value.length < 6) {
            _passwordError.tryEmit("Password length should be at least 6")
                .run { error = true }
        }
        return !error
    }

    /**
     * Extension function to check if a string is a valid email address.
     *
     * @return True if the string matches the email pattern, otherwise false.
     */
    fun String.isValidEmail() =
        this.isNotEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()
}
