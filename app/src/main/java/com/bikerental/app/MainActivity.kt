package com.bikerental.app

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.bikerental.app.ui.theme.BikeRentalTheme
import kotlinx.coroutines.launch
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bikerental.app.data.datasource.AuthRemoteDataSource
import com.bikerental.app.data.model.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import com.bikerental.app.ui.signup.SignUpScreen
import com.bikerental.app.ui.home.HomeScreen
import com.bikerental.app.ui.signin.SignInScreen
import com.google.firebase.auth.FirebaseAuth

const val SIGN_UP_ROUTE = "signup"
const val SIGN_IN_ROUTE = "signin"
const val HOME_ROUTE = "home"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setSoftInputMode()
        val currentUser = FirebaseAuth.getInstance().currentUser
        var startDestination = SIGN_UP_ROUTE

        if (currentUser != null) {
            startDestination = HOME_ROUTE
        }

        setContent {
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }
            val navController = rememberNavController()

            BikeRentalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.padding(innerPadding)
                        ) {
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
                                    openHomeScreen = {
                                        navController.navigate(HOME_ROUTE) {
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
