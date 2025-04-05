package com.bikerental.app

import android.net.Uri
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.ui.navigation.Navigator
import com.bikerental.app.ui.signup.SignUpViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the [SignUpViewModel] class.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockNavigator: Navigator
    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var mockUserRepository: UserRepository
    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var mockFirebaseUser: FirebaseUser

    private lateinit var viewModel: SignUpViewModel

    /**
     * Sets up the mocks and initializes the ViewModel before each test.
     */
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockNavigator = mockk(relaxed = true)
        mockAuthRepository = mockk(relaxed = true)
        mockUserRepository = mockk(relaxed = true)
        mockFirebaseAuth = mockk()
        mockFirebaseUser = mockk()

        every { mockFirebaseAuth.currentUser } returns mockFirebaseUser
        every { mockFirebaseUser.uid } returns "user123"

        viewModel = SignUpViewModel(
            navigator = mockNavigator,
            authRepository = mockAuthRepository,
            userRepository = mockUserRepository,
            auth = mockFirebaseAuth
        )
    }

    /**
     * Resets the dispatcher after each test.
     */
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Tests that name input updates the state and clears any error message.
     */
    @Test
    fun `onNameChange updates state and clears error`() = runTest {
        viewModel.onNameChange("Jane Doe")
        assertEquals("Jane Doe", viewModel.name.value)
        assertEquals("", viewModel.nameError.value)
    }

    /**
     * Tests that email input updates the state and clears any error message.
     */
    @Test
    fun `onEmailChange updates state and clears error`() = runTest {
        viewModel.onEmailChange("test@student.tudelft.nl")
        assertEquals("test@student.tudelft.nl", viewModel.email.value)
        assertEquals("", viewModel.emailError.value)
    }

    /**
     * Tests that password input updates the state and clears any error message.
     */
    @Test
    fun `onPasswordChange updates state and clears error`() = runTest {
        viewModel.onPasswordChange("securepass")
        assertEquals("securepass", viewModel.password.value)
        assertEquals("", viewModel.passwordError.value)
    }

    /**
     * Tests that signup fails and sets appropriate error messages when input is invalid.
     */
    @Test
    fun `basicSignUp fails with invalid input`() = runTest {
        viewModel.onEmailChange("invalid_email")
        viewModel.onPasswordChange("123")
        viewModel.basicSignUp()

        assertNotEquals("", viewModel.emailError.value)
        assertNotEquals("", viewModel.passwordError.value)
    }

    /**
     * Tests the full signup process including profile upload, user creation, and navigation.
     */
    @Test
    fun `basicSignUp uploads profile, creates user and navigates`() = runTest {
        val fakeUri = mockk<Uri>()
        val fakeDownloadUrl = "https://image.com/profile.jpg"

        // Set valid state
        viewModel.onNameChange("Jane Doe")
        viewModel.onEmailChange("jane@student.tudelft.nl")
        viewModel.onPasswordChange("secure123")
        viewModel.onProfileImageChange(fakeUri)

        // Mocks
        coEvery { mockAuthRepository.firebaseSignUp(any(), any()) } just Runs
        coEvery { mockUserRepository.addProfileImage(fakeUri) } returns Result.success(fakeDownloadUrl)
        coEvery {
            mockUserRepository.createUserDocument(
                uid = "user123",
                name = "Jane Doe",
                email = "jane@student.tudelft.nl",
                profileImageUrl = fakeDownloadUrl
            )
        } just Runs
        every { mockNavigator.navigateTo(any(), any()) } just Runs

        viewModel.basicSignUp()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockAuthRepository.firebaseSignUp("jane@student.tudelft.nl", "secure123") }
        coVerify { mockUserRepository.addProfileImage(fakeUri) }
        coVerify {
            mockUserRepository.createUserDocument(
                "user123",
                "Jane Doe",
                "jane@student.tudelft.nl",
                fakeDownloadUrl
            )
        }
        verify { mockNavigator.navigateTo(match { it == "home" }, true) }
        assertEquals("", viewModel.signUpError.value)
    }

    /**
     * Tests that signup fails with an appropriate error message if image upload fails.
     */
    @Test
    fun `basicSignUp fails if image upload fails`() = runTest {
        val fakeUri = mockk<Uri>()

        viewModel.onNameChange("John")
        viewModel.onEmailChange("john@student.tudelft.nl")
        viewModel.onPasswordChange("password123")
        viewModel.onProfileImageChange(fakeUri)

        coEvery { mockAuthRepository.firebaseSignUp(any(), any()) } just Runs
        coEvery { mockUserRepository.addProfileImage(fakeUri) } returns Result.failure(Exception("Upload failed"))

        viewModel.basicSignUp()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Profile image upload failed", viewModel.signUpError.value)
    }

    /**
     * Tests that switching to the login screen triggers navigation.
     */
    @Test
    fun `switchLogin triggers navigation`() = runTest {
        every { mockNavigator.navigateTo(any(), any()) } just Runs

        viewModel.switchLogin()
        verify { mockNavigator.navigateTo(match { it == "login" }, true) }
    }
}
