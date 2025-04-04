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

//    @Test
//    fun `loadUserDetails should emit user`() = runTest {
//        viewModel.user.test {
//            val user = awaitItem()
//            assertNotNull(user)
//            assertEquals("John Doe", user?.name)
//            cancelAndIgnoreRemainingEvents()
//        }
//    }

//    @Test
//    fun `onNameChanged should update user name`() = runTest {
//        val mockFirebaseUser: FirebaseUser = mockk()
//        every { mockFirebaseAuth.currentUser } returns mockFirebaseUser
//        every { mockFirebaseUser.uid } returns "user123"
//        coEvery { mockUserRepository.updateUserName("user123", "New Name") } just Runs
//
//        viewModel.onNameChanged("New Name")
//
//        coVerify { mockUserRepository.updateUserName("user123", "New Name") }
//    }
//
//    @Test
//    fun `onProfileImageChange uploads and sets image`() = runTest {
//        val fakeUri: Uri = mockk()
//        val fakeUrl = "https://example.com/profile.jpg"
//        every { mockAuthRepository.getCurrentUser?.uid } returns "user123"
//        coEvery { mockUserRepository.addProfileImage(fakeUri) } returns Result.success(fakeUrl)
//        coEvery { mockUserRepository.updateUserImage("user123", fakeUrl) } just Runs
//
//        viewModel.onProfileImageChange(fakeUri)
//
//        coVerifySequence {
//            mockUserRepository.addProfileImage(fakeUri)
//            mockUserRepository.updateUserImage("user123", fakeUrl)
//        }
//    }

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

//    @Test
//    fun `onDeleteAccount should delete user and navigate`() = runTest {
//        coEvery { mockFirebaseUser.uid } returns "user123"
//        coEvery { mockBikeRepository.deleteUsersBikes("user123") } just Runs
//        coEvery { mockUserRepository.deleteUserDetails("user123") } just Runs
//        coEvery { mockFirebaseUser.delete() } returns mockk()
//        coEvery { mockFirebaseUser.delete().await() } returns Unit
//
//        viewModel.onDeleteAccount()
//
//        coVerifySequence {
//            mockBikeRepository.deleteUsersBikes("user123")
//            mockUserRepository.deleteUserDetails("user123")
//            mockFirebaseUser.delete()
//            mockNavigator.navigateTo(Destination.SignUp.route, true)
//        }
//    }
}
