package com.bikerental.app.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavHostController
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
    AppTheme {
        val navController = rememberNavController()
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (isLandscape) {
            // Landscape layout with NavigationRail
            Row(
                modifier = Modifier
                    .systemBarsPadding()
                    .imePadding()
            ) {
                HomeNavigation(navController = navController)
                NavContent(navController, navigator, finish)
            }
        } else {
            // Portrait layout using Scaffold with BottomBar
            Scaffold(
                modifier = Modifier
                    .systemBarsPadding()
                    .imePadding(),
                bottomBar = { HomeNavigation(navController = navController) },
                contentWindowInsets = WindowInsets.systemBars
            ) { innerPadding ->
                NavContent(navController, navigator, finish, Modifier.padding(innerPadding))
            }
        }
    }
}

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