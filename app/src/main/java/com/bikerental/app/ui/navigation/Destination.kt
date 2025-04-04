package com.bikerental.app.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * Object that holds all the destination routes for the app's navigation.
 * Contains both static and dynamic destinations for different screens in the app.
 */
object Destination {
    /**
     * Static screen representing the splash screen.
     */
    data object Splash : Screen("splash")

    /**
     * Static screen representing the login screen.
     */
    data object Login : Screen("login")

    /**
     * Static screen representing the sign-up screen.
     */
    data object SignUp : Screen("signup")

    /**
     * Static screen representing the home section.
     * Contains several child screens related to the home functionality.
     */
    data object Home : Screen("home") {

        /**
         * Static screen representing the past rentals section.
         */
        data object PastRentals : Screen("home/past_rentals")

        /**
         * Static screen representing the current rentals section.
         */
        data object CurrentRentals : Screen("home/current_rentals")

        /**
         * Static screen representing the map section.
         */
        data object Map : Screen("home/map")

        /**
         * Dynamic screen representing the "Add Bike" section on the map.
         * Requires a `bikeId` argument.
         */
        data object MapAddBike : DynamicScreen("home/map-add-bike", "bikeId")

        /**
         * Static screen representing the screen to add a new bike.
         */
        data object AddBike : Screen("home/add-bike")

        /**
         * Static screen representing the user's profile screen.
         */
        data object Profile : Screen("home/profile")

        /**
         * Static screen representing the search screen.
         */
        data object Search : Screen("home/search")

        /**
         * Dynamic screen representing the bike details section.
         * Requires a `bikeId` argument.
         */
        data object BikeDetails : DynamicScreen("home/bike-details", "bikeId")

        /**
         * Static screen representing the inbox where messages can be seen.
         */
        data object Inbox : Screen("home/inbox")

        /**
         * Dynamic screen representing a chat screen.
         * Requires a `userId` argument.
         */
        data object Chat : DynamicScreen("home/chat", "userId")

        /**
         * Static screen representing the screen where users can view their own bikes.
         */
        data object MyBikes : Screen("home/my-bikes")

        /**
         * Static screen representing the user's profile details screen.
         */
        data object ProfileDetails : Screen("home/profile-details")
    }
}

/**
 * Abstract class representing a screen with a base route.
 * All screens must define their route using this base class.
 *
 * @param baseRoute The base route for the screen.
 */
abstract class Screen(baseRoute: String) {

    /**
     * The route for the screen. It can be overridden by subclasses to define custom routes.
     */
    open val route = baseRoute
}

/**
 * Abstract class representing a dynamic screen with arguments.
 * This class is used for screens that require arguments in their routes.
 *
 * @param baseRoute The base route for the screen.
 * @param routeArgName The name of the argument that is passed in the route.
 */
abstract class DynamicScreen(
    baseRoute: String,
    routeArgName: String,
) : Screen(baseRoute) {

    /**
     * A list of navigation arguments for the dynamic screen.
     * This defines the type and name of the argument passed in the route.
     */
    val navArguments = listOf(navArgument(routeArgName) { type = NavType.StringType })

    /**
     * The full route for the dynamic screen, including the argument.
     */
    override val route = "$baseRoute/{$routeArgName}"
}
