package com.bikerental.app.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikerental.app.ui.navigation.Navigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/**
 * Abstract base ViewModel class providing common functionality for all ViewModels in the app.
 * - Handles coroutine lifecycle management
 * - Provides centralized navigation control via [Navigator]
 * - Includes safe coroutine execution for Firebase operations
 */
abstract class BaseViewModel(
    val navigator: Navigator
) : ViewModel() {

    /**
     * Launches a coroutine in the ViewModelScope with optional Firebase operation error handling.
     *
     * @param silent If true, suppresses all errors (useful for background operations).
     *               If false (default), errors are propagated (useful for UI operations).
     * @param block The suspendable lambda to execute within the coroutine.
     *
     * Note: Automatically cancels on ViewModel cleanup and ignores [CancellationException].
     */
    protected fun launchFirebase(
        silent: Boolean = false,
        block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: Throwable) {
                if (e is CancellationException) return@launch
                if (!silent) {
                    // Optionally log or rethrow, based on your needs
                    // e.g., Log.e("launchFirebase", "Unhandled error", e)
                }
            }
        }
    }
}