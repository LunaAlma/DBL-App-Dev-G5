package com.bikerental.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bikerental.app.ui.welcome.Welcome
import com.bikerental.app.ui.welcome.WelcomeViewModel

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = Destination.Welcome.route,
    navigator: Navigator,
    finish: () -> Unit = {},
) {
    NavHandler(
        navController = navController,
        navigator = navigator,
        finish = finish
    )

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        // Welcome
        composable(Destination.Welcome.route) {
            val viewModel: WelcomeViewModel = hiltViewModel(key = WelcomeViewModel.TAG)
            Welcome(modifier, viewModel)
        }
    }
}