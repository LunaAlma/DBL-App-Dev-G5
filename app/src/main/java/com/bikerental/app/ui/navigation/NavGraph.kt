package com.bikerental.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.bikerental.app.ui.create.AddBike
import com.bikerental.app.ui.create.AddBikeViewModel
import com.bikerental.app.ui.login.Login
import com.bikerental.app.ui.login.LoginViewModel
import com.bikerental.app.ui.maps.MapViewModel
import com.bikerental.app.ui.signup.SignUp
import com.bikerental.app.ui.signup.SignUpViewModel
import com.bikerental.app.ui.welcome.Welcome
import com.bikerental.app.ui.welcome.WelcomeViewModel
import com.bikerental.app.ui.maps.Map
import com.bikerental.app.ui.search.SearchViewModel
import com.bikerental.app.ui.settings.Settings
import com.bikerental.app.ui.settings.SettingsViewModel
import com.bikerental.app.ui.splash.Splash
import com.bikerental.app.ui.splash.SplashViewModel

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = Destination.Splash.route,
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
        // Splash
        composable(Destination.Splash.route) {
            val viewModel: SplashViewModel = hiltViewModel(key = SplashViewModel.TAG)
            Splash(modifier, viewModel)
        }

        // Welcome
        composable(Destination.Welcome.route) {
            val viewModel: WelcomeViewModel = hiltViewModel(key = WelcomeViewModel.TAG)
            Welcome(modifier, viewModel)
        }

        // Login
        composable(Destination.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel(key = LoginViewModel.TAG)
            Login(modifier, viewModel)
        }

        // Signup
        composable(Destination.SignUp.route) {
            val viewModel: SignUpViewModel = hiltViewModel(key = SignUpViewModel.TAG)
            SignUp(modifier, viewModel)
        }

        // Home
        navigation(
            route = Destination.Home.route,
            startDestination = Destination.Home.Profile.route
        ) {
            // Home.Map
            composable(Destination.Home.Map.route) {
                val viewModel: MapViewModel = hiltViewModel(key = MapViewModel.TAG)
                Map(modifier, viewModel)
            }

            // Home.Profile
            composable(Destination.Home.Profile.route) {
                val viewModel: SettingsViewModel = hiltViewModel(key = SettingsViewModel.TAG)
                Settings(modifier, viewModel)
            }

            // Home.AddBike
            composable(Destination.Home.AddBike.route) {
                val viewModel: AddBikeViewModel = hiltViewModel(key = AddBikeViewModel.TAG)
                AddBike(modifier, viewModel)
            }

//            // Home.Search
//            composable(Destination.Home.Search.route) {
//                val viewModel: SearchViewModel = hiltViewModel(key = SearchViewModel.TAG)
//                Search(modifier, viewModel)
//            }
        }
    }
}