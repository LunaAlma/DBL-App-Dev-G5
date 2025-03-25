package com.bikerental.app.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument


object Destination {
    data object Welcome : Screen("welcome")
    data object SignIn : Screen("signin")
    data object SignUp : Screen("signup")
    data object Success : Screen("success")

    data object Home : Screen("home") {
        data object Feed : Screen("home/map")
        data object Mentors : Screen("home/profile")
//        data object MyBox : Screen("home/my_box")
//        data object Search : DynamicScreen("home/search", "searchMode")
//        data object Profile : DynamicScreen("home/profile", "profileTab")
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