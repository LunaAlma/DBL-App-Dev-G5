package com.bikerental.app.ui.home

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bikerental.app.ui.navigation.Destination
import com.bikerental.app.ui.theme.AppTheme
import kotlin.collections.asList
import kotlin.collections.map
import kotlin.collections.toTypedArray
import com.bikerental.app.R

@Keep
enum class HomeTab(
    @StringRes val title: Int,
    @DrawableRes val unselectedIcon: Int,
    @DrawableRes val selectedIcon: Int,
    val route: String,
    val destRoute: String,
) {
    MAP(
        R.string.menu_map,
        R.drawable.ic_search_unselected,
        R.drawable.ic_search,
        Destination.Home.Map.route,
        Destination.Home.Map.route,
    ),
    PROFILE(
        R.string.menu_profile,
        R.drawable.ic_mentor_unselected,
        R.drawable.ic_mentor,
        Destination.Home.Profile.route,
        Destination.Home.Profile.route,
    )
}


@Composable
fun HomeBottomBar(navController: NavController) {

    val tabs = remember { HomeTab.entries.toTypedArray().asList() }
    val routes = remember { HomeTab.entries.map { it.route } }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
        ?: Destination.Home.Profile.route

    HomeBottomBarView(
        tabs = tabs,
        routes = routes,
        currentRoute = currentRoute,
        tabClick = {
            if (it.route != currentRoute) {
                navController.navigate(it.destRoute) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    )
}

@Composable
private fun HomeBottomBarView(
    tabs: List<HomeTab>,
    routes: List<String>,
    currentRoute: String,
    tabClick: (HomeTab) -> Unit
) {

    if (currentRoute in routes) {
        NavigationBar(
            Modifier
                .windowInsetsBottomHeight(
                    WindowInsets.navigationBars.add(WindowInsets(bottom = 60.dp))
                )
        ) {
            tabs.forEach { tab ->
                val selected = currentRoute == tab.route
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(if (selected) tab.selectedIcon else tab.unselectedIcon),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    },
                    selected = selected,
                    onClick = { tabClick(tab) },
                    alwaysShowLabel = false,
                    modifier = Modifier.navigationBarsPadding(),
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
            tabs = HomeTab.entries.toTypedArray().asList(),
            routes = HomeTab.entries.map { it.route },
            currentRoute = HomeTab.PROFILE.route,
        ) {}
    }
}

@Preview("Dark")
@Composable
private fun HomeBottomBarDarkPreview() {
    AppTheme {
        HomeBottomBarView(
            tabs = HomeTab.entries.toTypedArray().asList(),
            routes = HomeTab.entries.map { it.route },
            currentRoute = HomeTab.PROFILE.route,
        ) {}
    }
}