package com.bikerental.app.ui.home

import android.content.res.Configuration
import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
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

@Composable
private fun HomeBottomBarView(
    tabs: List<HomeTab>,
    currentRoute: String,
    tabClick: (HomeTab) -> Unit
) {
    NavigationBar {  // This was the missing opening brace
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

@Composable
private fun HomeNavigationRailView(
    tabs: List<HomeTab>,
    currentRoute: String,
    tabClick: (HomeTab) -> Unit
) {
    NavigationRail(
        modifier = Modifier.fillMaxHeight(),
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