package com.bikerental.app.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import com.bikerental.app.ui.theme.AppTheme
import kotlin.getValue


/**
 * Main entry point of the Bike Rental Android application.
 * This Activity sets up the UI using Jetpack Compose and handles navigation.
 */

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Hilt viewModel instance, used for managing UI-related data and navigation
    val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Enables edge-to-edge mode giving them a transparent look
        enableEdgeToEdge()

        // Adjusts the soft input mode to prevent layout resizing when keyboard appears
        setSoftInputMode()

        // Sets the Compose UI as the content view
        setContent {
            // Applies the custom theme to all composable functions inside
            AppTheme {
                // Main Composable function that sets up the app's UI structure
                BikeApp(
                    // Provides navigation control from the ViewModel
                    navigator = viewModel.navigator,
                    // Callback to finish / close the activity
                    finish = { finish() }
                )
            }
        }
    }

    /**
     * Configures the window's soft input mode to prevent layout resizing
     * when the keyboard is shown. Instead, the keyboard will overlay the content.
     */
    private fun setSoftInputMode() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

}
