package com.bikerental.app

import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.ui.navigation.Destination
import com.bikerental.app.ui.navigation.Navigator
import com.bikerental.app.ui.splash.SplashViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [SplashViewModel] functionality.
 *
 * Verifies navigation behavior based on user authentication state after a 1000ms delay.
 *
 * @see SplashViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private lateinit var viewModel: SplashViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val mockNavigator: Navigator = mockk(relaxed = true)
    private val mockAuthRepository: AuthRepository = mockk()

    /**
     * Sets up the test environment before each test case.
     * Configures the main coroutine dispatcher for test execution.
     */
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    /**
     * Cleans up the test environment after each test case.
     * Resets the main coroutine dispatcher to its original state.
     */
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Verifies navigation to the Home screen when an authenticated user exists.
     *
     * Test Scenario:
     * 1. Mock an authenticated user
     * 2. Initialize the ViewModel
     * 3. Advance time to simulate delay
     * 4. Verify Home navigation with back stack clearance
     */
    @Test
    fun `should navigate to home when user is authenticated`() = runTest {
        // Arrange
        every { mockAuthRepository.getCurrentUser } returns mockk()

        // Act
        viewModel = SplashViewModel(mockNavigator, mockAuthRepository)
        testDispatcher.scheduler.advanceTimeBy(1000)
        testDispatcher.scheduler.runCurrent()

        // Assert
        verify {
            mockNavigator.navigateTo(
                route = Destination.Home.route,
                true
            )
        }
        confirmVerified(mockNavigator)
    }

    /**
     * Verifies navigation to the SignUp screen when no authenticated user exists.
     *
     * Test Scenario:
     * 1. Mock no authenticated user
     * 2. Initialize the ViewModel
     * 3. Advance time to simulate delay
     * 4. Verify SignUp navigation with back stack clearance
     */
    @Test
    fun `should navigate to signup when user is not authenticated`() = runTest {
        // Arrange
        every { mockAuthRepository.getCurrentUser } returns null

        // Act
        viewModel = SplashViewModel(mockNavigator, mockAuthRepository)
        testDispatcher.scheduler.advanceTimeBy(1000)
        testDispatcher.scheduler.runCurrent()

        // Assert
        verify {
            mockNavigator.navigateTo(
                route = Destination.SignUp.route,
                true
            )
        }
        confirmVerified(mockNavigator)
    }

    /**
     * Verifies the 1000ms delay is respected before navigation occurs.
     *
     * Test Scenario:
     * 1. Mock an authenticated user
     * 2. Initialize the ViewModel
     * 3. Verify no immediate navigation
     * 4. Advance time past delay threshold
     * 5. Verify navigation occurs after delay
     */
    @Test
    fun `should wait 1000ms before navigating`() = runTest {
        // Arrange
        every { mockAuthRepository.getCurrentUser } returns mockk()

        // Act
        viewModel = SplashViewModel(mockNavigator, mockAuthRepository)

        // Verify no immediate navigation
        verify(exactly = 0) { mockNavigator.navigateTo(any(), any()) }

        // Advance past delay
        testDispatcher.scheduler.advanceTimeBy(1000)
        testDispatcher.scheduler.runCurrent()

        // Verify navigation occurred after delay
        verify(exactly = 1) { mockNavigator.navigateTo(any(), any()) }
    }
}