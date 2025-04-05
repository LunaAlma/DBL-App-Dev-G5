package com.bikerental.app

import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.ui.login.LoginViewModel
import com.bikerental.app.ui.navigation.Destination
import com.bikerental.app.ui.navigation.Navigator
import com.google.firebase.auth.FirebaseAuth
import io.mockk.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class LoginUnitTest {

    private lateinit var viewModel: LoginViewModel
    private val navigator: Navigator = mockk(relaxed = true)
    private val authRepository: AuthRepository = mockk(relaxed = true)
    private val firebaseAuth: FirebaseAuth = mockk(relaxed = true)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        // Set up the viewModel
        viewModel = LoginViewModel(navigator, authRepository, firebaseAuth)
    }

    // Test: onEmailChange updates the email
    @Test
    fun `test onEmailChange updates email`() = runTest {
        val email = "test@example.com"
        viewModel.onEmailChange(email)

        assertEquals(email, viewModel.email.first())
    }

    // Test: onPasswordChange updates the password
    @Test
    fun `test onPasswordChange updates password`() = runTest {
        val password = "password123"
        viewModel.onPasswordChange(password)

        assertEquals(password, viewModel.password.first())
    }

    // Test: validate returns true for valid input
    @Test
    fun `test validate returns true for valid input`() {
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("password123")

        val isValid = viewModel.validate()

        assertTrue(isValid)
    }

    // Test: validate returns false if email is invalid
    @Test
    fun `test validate returns false if email is invalid`() {
        viewModel.onEmailChange("invalid-email")
        viewModel.onPasswordChange("password123")

        val isValid = viewModel.validate()

        assertTrue(!isValid)
    }

    // Test: validate returns false if password is too short
    @Test
    fun `test validate returns false if password is too short`() {
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("123")

        val isValid = viewModel.validate()

        assertTrue(!isValid)
    }

    // Test: basicLogin shows error on failure
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test basicLogin shows error on failure`() = runTest {
        // Mock failed login
        coEvery { authRepository.firebaseLogin(any(), any()) } throws Exception("Login failed")

        // Set valid email and password
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("password123")

        // Call basicLogin
        viewModel.basicLogin()

        // Verify loginError was emitted
        assertEquals("Login failed", viewModel.loginError.first())
    }

    // Test: resetPassword calls the password reset function
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test resetPassword calls reset email function`() = runTest {
        val email = "test@example.com"
        viewModel.onEmailChange(email)

        // Call resetPassword
        viewModel.resetPassword()

        // Verify the reset password method was called
        coVerify { authRepository.sendPasswordResetEmail(email) }
    }

    // Test: resetPassword does nothing if email is invalid
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test resetPassword does nothing if email is invalid`() = runTest {
        val invalidEmail = "invalid-email"
        viewModel.onEmailChange(invalidEmail)

        // Call resetPassword
        viewModel.resetPassword()

        // Verify the reset password method was NOT called
        coVerify(exactly = 0) { authRepository.sendPasswordResetEmail(any()) }
    }

    // Test: switchSignUp navigates to the SignUp screen
    @Test
    fun `test switchSignUp navigates to SignUp screen`() {
        viewModel.switchSignUp()

        // Verify navigation to the SignUp screen
        coVerify { navigator.navigateTo(Destination.SignUp.route, true) }
    }
}
