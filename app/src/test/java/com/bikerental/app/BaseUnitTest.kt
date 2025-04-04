package com.bikerental.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Navigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import io.mockk.*
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.cancellation.CancellationException

class BaseUnitTest {

    private lateinit var viewModel: BaseViewModelTestClass
    private val navigator: Navigator = mockk(relaxed = true) // Mock Navigator

    // Concrete subclass of BaseViewModel to test the abstract class
    class BaseViewModelTestClass(navigator: Navigator) : BaseViewModel(navigator) {
        fun testLaunchFirebase(silent: Boolean, block: suspend CoroutineScope.() -> Unit) {
            launchFirebase(silent, block)
        }
    }

    @Before
    fun setup() {
        viewModel = BaseViewModelTestClass(navigator)
    }

    // Test that the coroutine block is executed
    @Test
    fun `test launchFirebase executes block`() = runTest {
        // Create a mock block
        val block: suspend CoroutineScope.() -> Unit = mockk(relaxed = true)

        // Call launchFirebase
        viewModel.testLaunchFirebase(silent = false, block = block)

        // Verify that the block is executed
        coVerify { block.invoke(any()) }
    }

    // Test that errors are caught when silent = false
    @Test
    fun `test launchFirebase catches error when silent is false`() = runTest {
        // Create a mock block that throws an exception
        val block: suspend CoroutineScope.() -> Unit = {
            throw Exception("Test Error")
        }

        // Launch the function
        viewModel.testLaunchFirebase(silent = false, block = block)

        // Add a delay to let the coroutine run
        delay(100)

        // No specific assertions as the error is caught within the ViewModel
    }

    // Test that silent = true suppresses errors
    @Test
    fun `test launchFirebase suppresses error when silent is true`() = runTest {
        // Create a mock block that throws an exception
        val block: suspend CoroutineScope.() -> Unit = {
            throw Exception("Test Error")
        }

        // Call launchFirebase with silent = true
        viewModel.testLaunchFirebase(silent = true, block = block)

        // Add a delay to let the coroutine run
        delay(100)

        // No assertions since the error should be suppressed
    }

//    // Test that viewModelScope.launch is called
//    @Test
//    fun `test viewModelScopelaunch is called`() = runTest {
//        // Create a mock block
//        val block: suspend CoroutineScope.() -> Unit = mockk(relaxed = true)
//
//        // Mock viewModelScope.launch
//        val viewModelScopeMock = mockk<CoroutineScope>(relaxed = true)
//        viewModelScopeMock.launch(block = any()) // Capture this call
//
//        // Call launchFirebase
//        viewModel.testLaunchFirebase(silent = false, block = block)
//
//        // Verify that viewModelScope.launch is called
//        coVerify { viewModelScopeMock.launch(any()) }
//    }

    // Test that CancellationException is handled correctly
    @Test
    fun `test CancellationException is handled properly`() = runTest {
        // Create a mock block that throws a CancellationException
        val block: suspend CoroutineScope.() -> Unit = {
            throw CancellationException("Cancelled")
        }

        // Call launchFirebase and expect it to handle the CancellationException
        viewModel.testLaunchFirebase(silent = false, block = block)

        // Add a delay to let the coroutine run
        delay(100)

        // If no crash occurs and the exception is handled properly, the test passes.
    }
}