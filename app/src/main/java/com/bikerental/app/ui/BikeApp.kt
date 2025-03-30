package com.bikerental.app.ui

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.bikerental.app.ui.home.HomeNavigation
import com.bikerental.app.ui.navigation.NavGraph
import com.bikerental.app.ui.navigation.Navigator
import com.bikerental.app.ui.theme.AppTheme

@Composable
fun BikeApp(
    navigator: Navigator,
    finish: () -> Unit
) {
    // Wrap the entire application in our custom Material Theme.
    AppTheme {

        // Create the navigation Controller to navigate between different screens.
        val navController = rememberNavController()

        // Use Material3 Scaffold for the layout.
        // Apply imePadding to adjust for keyboard visibility.
        // Apply systemBarsPadding to adjust for status bar and navigation bar.
        Scaffold(
            modifier = Modifier
                .imePadding()
                .systemBarsPadding(),
            bottomBar = { HomeNavigation(navController = navController) },
        ) { innerPaddingModifier ->

            // Receive navController.
            // Apply Scaffold padding.
            // Pass through the navigator.
            // Pass through finish callbacks.
            NavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPaddingModifier),
                navigator = navigator,
                finish = finish
            )
        }
    }
}