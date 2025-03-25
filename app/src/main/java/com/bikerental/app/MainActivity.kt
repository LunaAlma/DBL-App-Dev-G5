package com.bikerental.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.bikerental.app.data.model.ErrorMessage
import com.bikerental.app.ui.create.AddBikeScreen
import com.bikerental.app.ui.search.BikeListScreen
import dagger.hilt.android.AndroidEntryPoint
import com.google.firebase.auth.FirebaseAuth
import com.bikerental.app.ui.home.HomeScreen
import com.bikerental.app.ui.maps.MapsScreen
import com.bikerental.app.ui.settings.SettingsScreen
import com.bikerental.app.ui.signin.SignInScreen
import com.bikerental.app.ui.signup.SignUpScreen
import com.bikerental.app.ui.success.SuccessScreen
import com.bikerental.app.ui.theme.AppTheme
import com.bikerental.app.ui.welcome.WelcomeScreen

const val WELCOME_ROUTE = "welcome"
const val SIGN_UP_ROUTE = "signup"
const val SIGN_IN_ROUTE = "signin"
const val HOME_ROUTE = "home"
const val MAPS_ROUTE = "maps"
const val SUCCESS_ROUTE = "success"
const val ADD_BIKE_ROUTE = "add-bike"
const val SEARCH_ROUTE = "search"
const val SETTINGS_ROUTE = "settings"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setSoftInputMode()
        val currentUser = FirebaseAuth.getInstance().currentUser
        var startDestination = WELCOME_ROUTE

        if (currentUser != null) {
            startDestination = MAPS_ROUTE
        }

        setContent {
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }
            val navController = rememberNavController()

            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                        bottomBar = {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination?.route

                            if (currentDestination in listOf(HOME_ROUTE, MAPS_ROUTE, ADD_BIKE_ROUTE, SEARCH_ROUTE, SETTINGS_ROUTE)) {
                                NavigationBar {
                                    NavigationBarItem(
                                        selected = currentDestination == HOME_ROUTE,
                                        onClick = {
                                            navController.navigate(HOME_ROUTE) {
                                                popUpTo(HOME_ROUTE) { saveState = true }

                                            }
                                        },
                                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                                        label = { Text("Home") }
                                    )
                                    NavigationBarItem(
                                        selected = currentDestination == MAPS_ROUTE,
                                        onClick = {
                                            navController.navigate(MAPS_ROUTE) {
                                                popUpTo(MAPS_ROUTE) { saveState = true }

                                            }
                                        },
                                        icon = { Icon(Icons.Filled.Map, contentDescription = "Map") },
                                        label = { Text("Map") }
                                    )
                                    NavigationBarItem(
                                        selected = currentDestination == ADD_BIKE_ROUTE,
                                        onClick = {
                                            navController.navigate(ADD_BIKE_ROUTE) {
                                                popUpTo(ADD_BIKE_ROUTE) { saveState = true }

                                            }
                                        },
                                        icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                                        label = { Text("") }
                                    )
                                    NavigationBarItem(
                                        selected = currentDestination == SEARCH_ROUTE,
                                        onClick = {
                                            navController.navigate(SEARCH_ROUTE) {
                                                popUpTo(SEARCH_ROUTE) { saveState = true }

                                            }
                                        },
                                        icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                                        label = { Text("Search") }
                                    )
                                    NavigationBarItem(
                                        selected = currentDestination == SETTINGS_ROUTE,
                                        onClick = {
                                            navController.navigate(SETTINGS_ROUTE) {
                                                popUpTo(SETTINGS_ROUTE) { saveState = true }

                                            }
                                        },
                                        icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                                        label = { Text("Settings") }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(WELCOME_ROUTE) {
                                WelcomeScreen(
                                    openSignUpScreen = {
                                        navController.navigate(SIGN_UP_ROUTE) {
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                            composable(SIGN_IN_ROUTE) {
                                SignInScreen(
                                    openHomeScreen = {
                                        navController.navigate(HOME_ROUTE) {
                                            launchSingleTop = true
                                        }
                                    },

                                    openSignUpScreen = {
                                        navController.navigate(SIGN_UP_ROUTE) {
                                            launchSingleTop = true
                                        }
                                    },
                                    showErrorSnackbar = { errorMessage ->
                                        val message = getErrorMessage(errorMessage)
                                        scope.launch { snackbarHostState.showSnackbar(message) }
                                    }
                                )
                            }
                            composable(SIGN_UP_ROUTE) {
                                SignUpScreen(
                                    openSuccessScreen = {
                                        navController.navigate(SUCCESS_ROUTE) {
                                            launchSingleTop = true
                                        }
                                    },
                                    showErrorSnackbar = { errorMessage ->
                                        val message = getErrorMessage(errorMessage)
                                        scope.launch { snackbarHostState.showSnackbar(message) }
                                    }
                                )
                            }
                            composable(SUCCESS_ROUTE) {
                                SuccessScreen(
                                    openHomeScreen = {
                                        navController.navigate(MAPS_ROUTE) {
                                            // Clear the back stack up to MAPS_ROUTE
                                            popUpTo(MAPS_ROUTE) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    },
                                    showErrorSnackbar = { errorMessage ->
                                        val message = getErrorMessage(errorMessage)
                                        scope.launch { snackbarHostState.showSnackbar(message) }
                                    }
                                )

                            }
                            composable(HOME_ROUTE) {
                                HomeScreen()
                            }
                            composable(MAPS_ROUTE) {
                                MapsScreen()
                            }
                            composable(ADD_BIKE_ROUTE) {
                                AddBikeScreen()
                            }
                            composable(SEARCH_ROUTE) {
                                BikeListScreen()
                            }
                            composable(SETTINGS_ROUTE) {
                                SettingsScreen()
                            }
                        }
                    }
                }
            }
        }
    }
    private fun setSoftInputMode() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    private fun getErrorMessage(error: ErrorMessage): String {
        return when (error) {
            is ErrorMessage.StringError -> error.message
            is ErrorMessage.IdError -> this@MainActivity.getString(error.message)
        }
    }
}
