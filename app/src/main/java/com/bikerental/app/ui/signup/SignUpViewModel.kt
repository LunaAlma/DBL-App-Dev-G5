package com.bikerental.app.ui.signup

import android.net.Uri
import androidx.core.util.PatternsCompat
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Destination
import com.bikerental.app.ui.navigation.Navigator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel for the Sign Up screen.
 *
 * Responsibilities:
 * - Manages form state (name, email, password)
 * - Handles input validation
 * - Coordinates authentication flow with AuthRepository
 * - Manages user creation in FireStore
 * - Handles navigation events
 *
 * @property authRepository Handles Firebase authentication
 * @property userRepository Manages user data in FireStore
 * @property auth FirebaseAuth instance for current user access
 */
@HiltViewModel
class SignUpViewModel @Inject constructor(
    navigator: Navigator,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : BaseViewModel(navigator) {

    // Form state flows
    private val _name = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _nameError = MutableStateFlow("")
    private val _emailError = MutableStateFlow("")
    private val _passwordError = MutableStateFlow("")
    private val _signUpError = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _profileImageUri = MutableStateFlow<Uri?>(null)

    val name = _name.asStateFlow()
    val email = _email.asStateFlow()
    val password = _password.asStateFlow()
    val nameError = _nameError.asStateFlow()
    val emailError = _emailError.asStateFlow()
    val passwordError = _passwordError.asStateFlow()
    val signUpError = _signUpError.asStateFlow()
    val isLoading = _isLoading.asStateFlow()
    val profileImageUri = _profileImageUri.asStateFlow()

    fun onProfileImageChange(uri: Uri) {
        _profileImageUri.value = uri
    }

    /**
     * Handles name field changes and clears related errors
     */
    fun onNameChange(input: String) {
        _name.tryEmit(input)
        if (nameError.value.isNotEmpty()) _nameError.tryEmit("")
        _signUpError.tryEmit("")
    }

    /**
     * Handles email field changes and clears related errors
     */
    fun onEmailChange(input: String) {
        _email.tryEmit(input)
        if (emailError.value.isNotEmpty()) _emailError.tryEmit("")
        _signUpError.tryEmit("")
    }

    /**
     * Handles password field changes and clears related errors
     */
    fun onPasswordChange(input: String) {
        _password.tryEmit(input)
        if (passwordError.value.isNotEmpty()) _passwordError.tryEmit("")
        _signUpError.tryEmit("")
    }

    /**
     * Initiates the sign-up process:
     * 1. Validates inputs
     * 2. Creates Firebase auth user
     * 3. Creates user document in FireStore
     * 4. Navigates to Home on success
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun basicSignUp() {
        if (validate()) {
            _isLoading.value = true

            launchFirebase {
                try {
                    // 1. Firebase Authentication
                    authRepository.firebaseSignUp(email.value, password.value)

                    val imageUri = _profileImageUri.value ?: throw Exception("Profile image is required")
                    val uploadResult = userRepository.addProfileImage(imageUri)
                    val downloadUrl = uploadResult.getOrElse { throw Exception("Profile image upload failed") }

                    // 2. FireStore User document creation
                    auth.currentUser?.let { user ->
                        userRepository.createUserDocument(
                            uid = user.uid,
                            name = name.value,
                            email = email.value,
                            profileImageUrl = downloadUrl,
                        )

                        // 3. Navigation to Home
                        navigator.navigateTo(Destination.Home.route, true)
                    }
                } catch (e: Exception) {
                    _signUpError.value = e.message ?: "Sign Up Failed"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    /**
     * Validates form inputs and sets error messages
     * @return Boolean indicating if all inputs are valid
     */
    private fun validate(): Boolean {
        var error = false
        if(_profileImageUri.value == null) _signUpError.tryEmit("Profile picture is required").run { error = true }
        if (name.value.isBlank()) _nameError.tryEmit("Name is required").run { error = true }
        if (!email.value.isValidEmail()) _emailError.tryEmit("Invalid Email").run { error = true }
        if (password.value.length < 6) {
            _passwordError.tryEmit("Password length should be at least 6")
                .run { error = true }
        }
        return !error
    }

    /**
     * Custom email validator with domain check
     */
    fun String.isValidEmail(): Boolean {
        val isEmailValid = this.isNotEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()
        val hasStudentDomain = Regex("^[^@]+@student\\.[^.]+\\.nl$", RegexOption.IGNORE_CASE).matches(this)

        return isEmailValid && hasStudentDomain
    }

    /**
     * Navigates to Login screen with clean back stack
     */
    fun switchLogin() {
        navigator.navigateTo(Destination.Login.route, true)
    }
}