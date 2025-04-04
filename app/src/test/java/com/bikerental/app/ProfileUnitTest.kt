package com.bikerental.app

import android.net.Uri
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
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

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

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `onLogout should logout and navigate to login`() = runTest {
        every { mockAuthRepository.logout() } just Runs

        viewModel.onLogout()

        verify { mockAuthRepository.logout() }
        verify { mockNavigator.navigateTo(Destination.Login.route, true) }
    }

    @Test
    fun `navigateToPastRentals should trigger navigation`() = runTest {
        viewModel.navigateToPastRentals()
        verify { mockNavigator.navigateTo(Destination.Home.PastRentals.route) }
    }

}
