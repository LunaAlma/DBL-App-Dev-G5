package com.bikerental.app.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument


object Destination {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object SignUp : Screen("signup")

    data object Home : Screen("home") {
        data object PastRentals : Screen("home/past_rentals")
        data object CurrentRentals : Screen("home/current_rentals")
        data object Map : Screen("home/map")
        data object MapAddBike : DynamicScreen("home/map-add-bike", "bikeId")
        data object AddBike : Screen("home/add-bike")
        data object Profile : Screen("home/profile")
        data object Search : Screen("home/search")
        data object BikeDetails : DynamicScreen("home/bike-details", "bikeId")
        data object Inbox : Screen("home/inbox")
        data object Chat : DynamicScreen("home/chat", "userId")
        data object MyBikes : Screen("home/my-bikes")
        data object ProfileDetails : Screen("home/profile-details")

    }
}

abstract class Screen(baseRoute: String) {

    open val route = baseRoute
}

abstract class DynamicScreen(
    baseRoute: String,
    routeArgName: String,
) : Screen(baseRoute) {

    val navArguments = listOf(navArgument(routeArgName) { type = NavType.StringType })

    override val route = "$baseRoute/{$routeArgName}"

}