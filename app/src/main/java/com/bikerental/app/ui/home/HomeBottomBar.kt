package com.bikerental.app.ui.home

import android.content.res.Configuration
import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bikerental.app.ui.navigation.Destination
import com.bikerental.app.ui.theme.AppTheme
import com.bikerental.app.R

/**
 * Enum representing the tabs available in the Home screen.
 *
 * @param title The resource ID of the title string for the tab.
 * @param unselectedIcon The icon to display when the tab is not selected.
 * @param selectedIcon The icon to display when the tab is selected.
 * @param route The route associated with this tab for navigation.
 * @param destRoute The destination route for this tab.
 */
@Keep
enum class HomeTab(
    @StringRes val title: Int,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector,
    val route: String,
    val destRoute: String,
) {
    MAP(
        R.string.menu_map,
        Icons.Outlined.Map,
        Icons.Filled.Map,
        Destination.Home.Map.route,
        Destination.Home.Map.route,
    ),
    ADD_BIKE(
        R.string.menu_add_bike,
        Icons.Outlined.Add,
        Icons.Filled.Add,
        Destination.Home.AddBike.route,
        Destination.Home.AddBike.route,
    ),
    SEARCH(
        R.string.menu_search,
        Icons.Outlined.Search,
        Icons.Filled.Search,
        Destination.Home.Search.route,
        Destination.Home.Search.route,
    ),
    MESSAGES(
        R.string.menu_profile,
        Icons.AutoMirrored.Outlined.Message,
        Icons.AutoMirrored.Filled.Message,
        Destination.Home.Inbox.route,
        Destination.Home.Inbox.route,
    ),
    PROFILE(
        R.string.menu_profile,
        Icons.Outlined.Person,
        Icons.Filled.Person,
        Destination.Home.Profile.route,
        Destination.Home.Profile.route,
    )
}

/**
 * Composable function to handle the Home navigation UI.
 *
 * This function decides whether to show the HomeBottomBarView or HomeNavigationRailView
 * depending on the screen orientation (portrait or landscape).
 *
 * @param navController The navigation controller to handle screen navigation.
 */
@Composable
fun HomeNavigation(navController: NavController) {
    val tabs = remember { HomeTab.entries }
    val routes = remember { HomeTab.entries.map { it.route } }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Destination.Home.Profile.route

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val tabClick = { tab: HomeTab ->
        if (tab.route != currentRoute) {
            navController.navigate(tab.destRoute) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    if (currentRoute in routes || currentRoute.startsWith(Destination.Home.Chat.route)) {
        if (isLandscape) {
            HomeNavigationRailView(
                tabs = tabs,
                currentRoute = currentRoute,
                tabClick = tabClick
            )
        } else {
            HomeBottomBarView(
                tabs = tabs,
                currentRoute = currentRoute,
                tabClick = tabClick
            )
        }
    }
}

/**
 * Composable function to show the Home Bottom Navigation Bar.
 *
 * Displays a bottom bar with navigation items for each tab in `tabs`. The selected tab
 * will have its icon highlighted.
 *
 * @param tabs The list of `HomeTab` items that represent the tabs in the navigation.
 * @param currentRoute The current route to determine which tab is selected.
 * @param tabClick A lambda function to handle tab selection.
 */
@Composable
private fun HomeBottomBarView(
    tabs: List<HomeTab>,
    currentRoute: String,
    tabClick: (HomeTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier.height(60.dp)
    ) {  // This was the missing opening brace
        tabs.forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                },
                selected = selected,
                onClick = { tabClick(tab) },
                alwaysShowLabel = false,
            )
        }
    }
}

/**
 * Composable function to show the Home Navigation Rail.
 *
 * Displays a vertical navigation rail with items for each tab in `tabs`. The selected
 * tab will have its icon highlighted.
 *
 * @param tabs The list of `HomeTab` items that represent the tabs in the navigation.
 * @param currentRoute The current route to determine which tab is selected.
 * @param tabClick A lambda function to handle tab selection.
 */
@Composable
private fun HomeNavigationRailView(
    tabs: List<HomeTab>,
    currentRoute: String,
    tabClick: (HomeTab) -> Unit
) {
    NavigationRail(
        modifier = Modifier.fillMaxHeight().width(65.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            tabs.forEach { tab ->
                val selected = currentRoute == tab.route
                NavigationRailItem(
                    icon = {
                        Icon(
                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    },
                    selected = selected,
                    onClick = { tabClick(tab) },
                    alwaysShowLabel = false,
                )
            }
        }
    }
}

/**
 * Preview of the Home Bottom Bar in Light mode.
 */
@Preview("Light")
@Composable
private fun HomeBottomBarLightPreview() {
    AppTheme {
        HomeBottomBarView(
            tabs = HomeTab.entries,
            currentRoute = HomeTab.PROFILE.route,
            tabClick = {}
        )
    }
}

/**
 * Preview of the Home Bottom Bar in Dark mode.
 */
@Preview("Dark")
@Composable
private fun HomeBottomBarDarkPreview() {
    AppTheme {
        HomeBottomBarView(
            tabs = HomeTab.entries,
            currentRoute = HomeTab.PROFILE.route,
            tabClick = {}
        )
    }
}

/**
 * Preview of the Home Navigation Rail in Light mode.
 */
@Preview("Rail Light")
@Composable
private fun HomeRailLightPreview() {
    AppTheme {
        HomeNavigationRailView(
            tabs = HomeTab.entries,
            currentRoute = HomeTab.PROFILE.route,
            tabClick = {}
        )
    }
}