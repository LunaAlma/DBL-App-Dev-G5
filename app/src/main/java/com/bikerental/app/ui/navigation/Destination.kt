package com.bikerental.app.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument


object Destination {
    data object Splash : Screen("splash")
    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object SignUp : Screen("signup")
    data object Success : Screen("success")

    data object Home : Screen("home") {
        data object Map : Screen("home/map")
        data object AddBike : Screen("home/add-bike")
        data object Profile : Screen("home/profile")
        data object Search : Screen("home/search")
        data object Inbox : Screen("home/inbox")
        data object Chat : DynamicScreen("home/chat", "userId")
    }
}

abstract class Screen(baseRoute: String) {

    open val route = baseRoute
}

abstract class DynamicScreen(
    private val baseRoute: String,
    val routeArgName: String,
) : Screen(baseRoute) {

    val navArguments = listOf(navArgument(routeArgName) { type = NavType.StringType })

    override val route = "$baseRoute/{$routeArgName}"

//    fun dynamicRoute(param: String) = "$baseRoute/$param"
//
//    fun dynamicDeeplink(param: String) = "$BASE_DEEPLINK_URL/$baseRoute/${param}"
}