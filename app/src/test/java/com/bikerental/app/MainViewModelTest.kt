package com.bikerental.app.ui

import androidx.lifecycle.viewModelScope
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Destination
import com.bikerental.app.ui.navigation.Navigator
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import kotlinx.coroutines.test.StandardTestDispatcher


/**
 * Unit tests for [MainViewModel] functionality.
 *
 * Verifies core behavior of the main application ViewModel including:
 * - Proper initialization sequence
 * - Navigation dependency integration
 * - Base ViewModel functionality inheritance
 *
 * @see MainViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val mockNavigator: Navigator = mockk(relaxed = true)

    /**
     * Configures test environment before each test case:
     * - Sets main coroutine dispatcher for test execution
     * - Initializes fresh ViewModel instance
     */
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel(mockNavigator)
    }

    /**
     * Cleans up test environment after each test case:
     * - Resets main coroutine dispatcher to original state
     */
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Verifies successful ViewModel initialization and dependency injection.
     *
     * Test Scenario:
     * 1. Initialize ViewModel with mocked dependencies
     * 2. Verify proper dependency assignment
     * 3. Confirm coroutine scope availability
     */
    @Test
    fun `should initialize with injected dependencies`() {
        assertNotNull("ViewModel should be initialized", viewModel)
        assertTrue("Should have access to coroutine scope",
            viewModel.viewModelScope.isActive)
    }

    /**
     * Verifies proper inheritance from BaseViewModel.
     *
     * Test Scenario:
     * 1. Check inheritance hierarchy
     * 2. Verify navigator assignment in base class
     */
    @Test
    fun `should properly extend BaseViewModel`() {
        assertTrue("Should inherit from BaseViewModel",
            viewModel is BaseViewModel
        )

        // Verify base class functionality
        viewModel.navigator.navigateTo(Destination.Home.route)
        verify { mockNavigator.navigateTo(Destination.Home.route) }
    }

}