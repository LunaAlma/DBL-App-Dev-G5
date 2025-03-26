package com.bikerental.app.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.bikerental.app.ui.home.HomeBottomBar
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
        Scaffold(
            modifier = Modifier.imePadding(),
            snackbarHost = { SnackbarHostState() },
            // it will render bottom bar only in the home route
            bottomBar = { HomeBottomBar(navController = navController) },
        ) { innerPaddingModifier ->
            NavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPaddingModifier),
                navigator = navigator,
                finish = finish
            )
        }
    }
}