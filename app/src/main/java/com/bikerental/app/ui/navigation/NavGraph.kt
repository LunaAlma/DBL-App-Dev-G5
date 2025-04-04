package com.bikerental.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.bikerental.app.ui.bike.BikeDetails
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.bikerental.app.ui.bike.BikeDetailsViewModel
import com.bikerental.app.ui.create.AddBike
import com.bikerental.app.ui.create.AddBikeViewModel
import com.bikerental.app.ui.currentrentals.CurrentRentals
import com.bikerental.app.ui.currentrentals.CurrentRentalsViewModel
import com.bikerental.app.ui.messaging.Inbox
import com.bikerental.app.ui.messaging.InboxViewModel
import com.bikerental.app.ui.login.Login
import com.bikerental.app.ui.login.LoginViewModel
import com.bikerental.app.ui.map.MapViewModel
import com.bikerental.app.ui.signup.SignUp
import com.bikerental.app.ui.signup.SignUpViewModel
import com.bikerental.app.ui.map.Map
import com.bikerental.app.ui.map.MapAddBike
import com.bikerental.app.ui.messaging.Chat
import com.bikerental.app.ui.messaging.ChatViewModel
import com.bikerental.app.ui.profile.Profile
import com.bikerental.app.ui.profile.ProfileViewModel
import com.bikerental.app.ui.search.SearchViewModel
import com.bikerental.app.ui.splash.Splash
import com.bikerental.app.ui.splash.SplashViewModel
import com.bikerental.app.ui.pastrentals.PastRentalsViewModel
import com.bikerental.app.ui.pastrentals.PastRentalsScreen
import com.bikerental.app.ui.profile.MyBikes
import com.bikerental.app.ui.profile.MyBikesViewModel
import com.bikerental.app.ui.profile.UserProfileDetails
import com.bikerental.app.ui.search.BikeListScreen
import com.bikerental.app.ui.search.Search

/**
 * Sets up the navigation graph for the app using Jetpack Compose's NavHost.
 * The navigation graph defines the structure of all screens and the routes used to navigate between them.
 *
 * @param modifier The modifier for the navigation graph.
 * @param navController The NavController that handles navigation.
 * @param startDestination The route to navigate to first (default is Splash screen).
 * @param navigator The navigator that manages the app's navigation.
 * @param finish A lambda that can be invoked to perform finishing tasks after navigation (optional).
 */
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
            val viewModel: SplashViewModel = hiltViewModel()
            Splash(modifier, viewModel)
        }

        // Login
        composable(Destination.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            Login(modifier, viewModel)
        }

        // Signup
        composable(Destination.SignUp.route) {
            val viewModel: SignUpViewModel = hiltViewModel()
            SignUp(modifier, viewModel)
        }

        // Home
        navigation(
            route = Destination.Home.route,
            startDestination = Destination.Home.Map.route
        ) {
            // Home.Map
            composable(Destination.Home.Map.route) {
                val viewModel: MapViewModel = hiltViewModel()
                Map(modifier, viewModel)
            }

            // Home.MapAddBike
            composable(
                route = Destination.Home.MapAddBike.route,
                arguments = Destination.Home.MapAddBike.navArguments) {
                val viewModel: MapViewModel = hiltViewModel()
                MapAddBike(modifier, viewModel)
            }

            // Home.Profile
            composable(Destination.Home.Profile.route) {
                val viewModel: ProfileViewModel = hiltViewModel()
                Profile(modifier, viewModel)
            }

            // Home.ProfileDetails
            composable(Destination.Home.ProfileDetails.route) {
                val viewModel: ProfileViewModel = hiltViewModel()
                UserProfileDetails(modifier, viewModel)
            }

            // Home.MyBikes
            composable(Destination.Home.MyBikes.route) {
                val viewModel: MyBikesViewModel = hiltViewModel()
                MyBikes(modifier, viewModel)
            }

            // Home.AddBike
            composable(Destination.Home.AddBike.route) {
                val viewModel: AddBikeViewModel = hiltViewModel()
                AddBike(modifier, viewModel)
            }

            // Home.Inbox
            composable(Destination.Home.Inbox.route) {
                val viewModel: InboxViewModel = hiltViewModel()
                Inbox(modifier, viewModel)
            }

            // Home.Chat
            composable(
                route = Destination.Home.Chat.route,
                arguments = Destination.Home.Chat.navArguments
            ) {
                val viewModel: ChatViewModel = hiltViewModel()
                Chat(modifier, viewModel)
            }

            // Home.Search
            composable(Destination.Home.Search.route) {
                //val navController = rememberNavController()
                val viewModel: SearchViewModel = hiltViewModel(key = SearchViewModel.TAG)
                BikeListScreen(navController, modifier, viewModel)
            }

            // Home.BikeDetails
            composable(
                route = Destination.Home.BikeDetails.route,
                arguments = Destination.Home.BikeDetails.navArguments
            ) { backStackEntry ->
                val bikeId = backStackEntry.arguments?.getString("bikeId") ?: "default_bike_id"
                BikeDetails(bikeId)
            }

            // Home.PastRental
            composable(Destination.Home.PastRentals.route) {
                val viewModel: PastRentalsViewModel = hiltViewModel()
                PastRentalsScreen(
                    navigator = viewModel.navigator,
                    viewModel = viewModel
                )
            }

            // Home.CurrentRentals
            composable(Destination.Home.CurrentRentals.route) {
                val viewModel: CurrentRentalsViewModel = hiltViewModel()
                CurrentRentals(
                    navigator = viewModel.navigator,
                    viewModel = viewModel
                )
            }

            // Another BikeDetails
            composable(
                route = Destination.Home.BikeDetails.route,
                arguments = Destination.Home.BikeDetails.navArguments
            ) { backStackEntry ->
                val bikeId = backStackEntry.arguments?.getString("bikeId") ?: ""
                val viewModel: BikeDetailsViewModel = hiltViewModel()
                BikeDetails(
                    bikeId = bikeId,
                    viewModel = viewModel
                )
            }

            // BikeDetails Dynamic Route
            composable(
                route = "bikeDetails/{bikeId}",
                arguments = listOf(navArgument("bikeId") { type = NavType.StringType })
            ) { backStackEntry ->
                BikeDetails(
                    bikeId = backStackEntry.arguments?.getString("bikeId") ?: ""
                )
            }
        }
    }
}
