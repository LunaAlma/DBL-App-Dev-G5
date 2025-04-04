package com.bikerental.app.ui.navigation

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

/**
 * Navigator is responsible for managing navigation events within the app.
 * It exposes shared flows for navigation, back navigation, and ending navigation.
 * Components can subscribe to these flows and trigger navigation actions like
 * navigating to a new screen, navigating back, or finishing the navigation stack.
 */
@ActivityRetainedScoped
class Navigator @Inject constructor() {

    // Shared flow to emit navigation targets (routes and backstack behavior)
    private val _navigate =
        MutableSharedFlow<NavTarget>(extraBufferCapacity = 1)

    // Shared flow to emit back navigation events (and optional recreation behavior)
    private val _back =
        MutableSharedFlow<NavBack>(extraBufferCapacity = 1)

    // Shared flow to emit end navigation events
    private val _end =
        MutableSharedFlow<Boolean>(extraBufferCapacity = 1)

    // Exposed shared flows to listen to navigation events
    val navigate = _navigate.asSharedFlow()
    val back = _back.asSharedFlow()
    val end = _end.asSharedFlow()

    /**
     * Navigate to a new screen by its route. Optionally, the backstack can be popped
     * before navigating.
     *
     * @param route The target route to navigate to.
     * @param popBackstack Boolean flag to determine whether the backstack should be popped
     * before navigating.
     */
    fun navigateTo(route: String, popBackstack: Boolean = false) {
        _navigate.tryEmit(NavTarget(route, popBackstack))
    }

    /**
     * Trigger back navigation. Optionally, the previous screen can be recreated.
     *
     * @param recreate Boolean flag to determine whether the previous screen should be recreated.
     */
    fun navigateBack(recreate: Boolean = false) {
        _back.tryEmit(NavBack(recreate))
    }

    /**
     * Finish the navigation stack and signal that navigation should end.
     */
    fun finish() {
        _end.tryEmit(true)
    }

    /**
     * Data class to represent a navigation target.
     *
     * @param route The target route for navigation.
     * @param popBackstack Boolean flag to specify whether to pop the backstack before navigating.
     */
    data class NavTarget(val route: String, val popBackstack: Boolean = false)

    /**
     * Data class to represent back navigation.
     *
     * @param recreate Boolean flag to specify whether the previous screen should be recreated.
     */
    data class NavBack(val recreate: Boolean)
}
