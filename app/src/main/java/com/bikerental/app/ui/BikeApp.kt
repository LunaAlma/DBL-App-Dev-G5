package com.bikerental.app.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bikerental.app.ui.home.HomeNavigation
import com.bikerental.app.ui.navigation.NavGraph
import com.bikerental.app.ui.navigation.Navigator
import com.bikerental.app.ui.theme.AppTheme

/**
 * Entry point composable for the Bike Rental app.
 * It sets up the app's theme and manages the layout based on device orientation (portrait or landscape).
 * In landscape mode, it displays a side navigation with a navigation rail, while in portrait mode,
 * it uses a scaffold with a bottom navigation bar.
 *
 * @param navigator The Navigator that helps with navigating between screens.
 * @param finish A lambda function to handle the app finishing, used for cleanup or closing the app.
 */
@Composable
fun BikeApp(
    navigator: Navigator,
    finish: () -> Unit
) {
    AppTheme {
        val navController = rememberNavController()
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                if (isLandscape) {
                    // Landscape layout with NavigationRail
                    Row(
                        modifier = Modifier
                            .imePadding()
                    ) {
                        HomeNavigation(navController = navController)
                        NavContent(navController, navigator, finish)
                    }
                } else {
                    // Portrait layout using Scaffold with BottomBar
                    Scaffold(
                        modifier = Modifier
                            .imePadding(),
                        bottomBar = { HomeNavigation(navController = navController) },
                        contentWindowInsets = WindowInsets.systemBars
                    ) { innerPadding ->
                        NavContent(navController, navigator, finish, Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

/**
 * Composable that sets up the navigation graph for the app, based on the current navigation controller.
 *
 * @param navController The NavController that manages app navigation.
 * @param navigator The Navigator instance used to navigate between screens.
 * @param finish A lambda function that handles the app finish event.
 * @param modifier Modifier to be applied to the NavGraph.
 */
@Composable
private fun NavContent(
    navController: NavHostController,
    navigator: Navigator,
    finish: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavGraph(
        navController = navController,
        modifier = modifier,
        navigator = navigator,
        finish = finish
    )
}
