package com.bikerental.app

import com.bikerental.app.data.model.User
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.ui.navigation.Destination
import com.bikerental.app.ui.navigation.Navigator
import com.bikerental.app.ui.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the [ProfileViewModel] class.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private lateinit var viewModel: ProfileViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val mockNavigator: Navigator = mockk(relaxed = true)
    private val mockAuthRepository: AuthRepository = mockk()
    private val mockUserRepository: UserRepository = mockk()
    private val mockBikeRepository: BikeRepository = mockk()
    private val mockFirebaseAuth: FirebaseAuth = mockk()
    private val mockFirebaseUser: FirebaseUser = mockk()

    /**
     * Sets up the test environment and initializes the [ProfileViewModel] before each test.
     */
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { mockFirebaseAuth.currentUser } returns mockFirebaseUser
        every { mockAuthRepository.getCurrentUser } returns mockFirebaseUser
        every { mockFirebaseUser.uid } returns "user123"

        coEvery { mockUserRepository.getUserById("user123") } returns flowOf(
            User(uid = "user123", name = "John Doe", email = "john@example.com")
        )

        viewModel = ProfileViewModel(
            navigator = mockNavigator,
            authRepository = mockAuthRepository,
            userRepository = mockUserRepository,
            bikeRepository = mockBikeRepository,
            auth = mockFirebaseAuth
        )

        runTest { testDispatcher.scheduler.advanceUntilIdle() }
    }

    /**
     * Cleans up the dispatcher after each test.
     */
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Tests that the logout operation is performed and user is navigated to the login screen.
     */
    @Test
    fun `onLogout should logout and navigate to login`() = runTest {
        every { mockAuthRepository.logout() } just Runs

        viewModel.onLogout()

        verify { mockAuthRepository.logout() }
        verify { mockNavigator.navigateTo(Destination.Login.route, true) }
    }

    /**
     * Tests that navigation to the past rentals screen is triggered correctly.
     */
    @Test
    fun `navigateToPastRentals should trigger navigation`() = runTest {
        viewModel.navigateToPastRentals()
        verify { mockNavigator.navigateTo(Destination.Home.PastRentals.route) }
    }
}
