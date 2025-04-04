package com.bikerental.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Handles navigation actions within the app.
 * This composable listens to navigation events from the [navigator] and performs
 * navigation actions such as navigating to a new screen, popping the back stack,
 * or navigating up. It also handles ending the navigation by calling the [finish] function.
 *
 * @param navController The [NavController] responsible for managing the app's navigation stack.
 * @param navigator The [Navigator] that exposes navigation events and actions.
 * @param finish A callback function that is executed when navigation should be ended.
 */
@Composable
internal fun NavHandler(
    navController: NavController,
    navigator: Navigator,
    finish: () -> Unit
) {
    LaunchedEffect("navigation") {
        // Listen for navigation events and handle navigation accordingly
        navigator.navigate.onEach {
            // If popBackstack is true, pop the back stack and navigate to the target screen
            if (it.popBackstack) navController.popBackStack()
            // Navigate to the target route
            navController.navigate(it.route)
        }.launchIn(this)

        // Listen for back navigation events
        navigator.back.onEach {
            // If recreate is true, navigate to the previous screen and clear the stack up to it
            if (it.recreate) {
                navController.previousBackStackEntry?.destination?.route?.let { route ->
                    navController.navigate(route) {
                        popUpTo(route) { inclusive = true }
                    }
                }
            } else {
                // If recreate is false, simply navigate up
                navController.navigateUp()
            }
        }.launchIn(this)

        // Listen for the end event and execute the finish callback
        navigator.end.onEach {
            finish()
        }.launchIn(this)
    }
}
