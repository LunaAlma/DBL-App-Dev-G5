package com.bikerental.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.bikerental.app.ui.bike.BikeDetailsViewModel
import com.bikerental.app.ui.create.AddBike
import com.bikerental.app.ui.create.AddBikeViewModel
import com.bikerental.app.ui.messaging.Inbox
import com.bikerental.app.ui.messaging.InboxViewModel
import com.bikerental.app.ui.login.Login
import com.bikerental.app.ui.login.LoginViewModel
import com.bikerental.app.ui.maps.MapViewModel
import com.bikerental.app.ui.signup.SignUp
import com.bikerental.app.ui.signup.SignUpViewModel
import com.bikerental.app.ui.welcome.Welcome
import com.bikerental.app.ui.welcome.WelcomeViewModel
import com.bikerental.app.ui.maps.Map
import com.bikerental.app.ui.messaging.Chat
import com.bikerental.app.ui.messaging.ChatViewModel
import com.bikerental.app.ui.navigation.Destination.Home.BikeDetails
import com.bikerental.app.ui.profile.Profile
import com.bikerental.app.ui.profile.ProfileViewModel
import com.bikerental.app.ui.search.SearchViewModel
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
            startDestination = Destination.Home.Map.route
        ) {
            // Home.Map
            composable(Destination.Home.Map.route) {
                val viewModel: MapViewModel = hiltViewModel(key = MapViewModel.TAG)
                Map(modifier, viewModel)
            }

            // Home.Profile
            composable(Destination.Home.Profile.route) {
                val viewModel: ProfileViewModel = hiltViewModel(key = ProfileViewModel.TAG)
                Profile(modifier, viewModel)
            }

            // Home.AddBike
            composable(Destination.Home.AddBike.route) {
                val viewModel: AddBikeViewModel = hiltViewModel(key = AddBikeViewModel.TAG)
                AddBike(modifier, viewModel)
            }

            // Home.Inbox
            composable(Destination.Home.Inbox.route) {
                val viewModel: InboxViewModel = hiltViewModel(key = InboxViewModel.TAG)
                Inbox(modifier, viewModel)
            }

//            // Home.BikeDetails
//            composable(
//                route = Destination.Home.BikeDetails.route,
//                arguments = Destination.Home.BikeDetails.navArguments
//            ) {
//                val viewModel: BikeDetailsViewModel = hiltViewModel(key = BikeDetailsViewModel.TAG)
//                BikeDetails(modifier, viewModel)
//            }

            // Home.Chat
            composable(
                route = Destination.Home.Chat.route,
                arguments = Destination.Home.Chat.navArguments
            ) {
                val viewModel: ChatViewModel = hiltViewModel(key = ChatViewModel.TAG)
                Chat(modifier, viewModel)
            }

//            // Home.Search
//            composable(Destination.Home.Search.route) {
//                val viewModel: SearchViewModel = hiltViewModel(key = SearchViewModel.TAG)
//                Search(modifier, viewModel)
//            }
        }
    }
}