package com.bikerental.app.ui.messaging

import com.bikerental.app.data.model.User
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Destination
import com.bikerental.app.ui.navigation.Navigator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel for the Inbox screen. This ViewModel is responsible for fetching the list of users
 * and managing the loading state. It also handles navigation to individual chat screens.
 *
 * @param navigator The Navigator object used to navigate to different screens.
 * @param userRepository The repository that provides access to user data.
 * @param auth FirebaseAuth instance for handling authentication and getting the current user.
 */
@HiltViewModel
class InboxViewModel @Inject constructor(
    navigator: Navigator,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : BaseViewModel(navigator) {

    // MutableStateFlow to hold the list of users
    private val _users = MutableStateFlow<List<User>>(emptyList())
    // Exposing _users as a read-only StateFlow
    val users = _users.asStateFlow()

    // MutableStateFlow to track the loading state
    private val _isLoading = MutableStateFlow(false)
    // Exposing _isLoading as a read-only StateFlow
    val isLoading = _isLoading.asStateFlow()

    init {
        // Calling loadUsers to fetch users when the ViewModel is created
        loadUsers()
    }

    /**
     * Function to load users from the repository. It filters out the current user from the list.
     * This function also manages the loading state.
     */
    private fun loadUsers() {
        // Starting the Firebase operation on a background thread
        launchFirebase {
            // Set loading state to true when starting the data load
            _isLoading.value = true
            try {
                // Collect users from the repository
                userRepository.getUsers().collect { users ->
                    // Filter out the current user from the list
                    _users.value = users.filter { it.uid != auth.currentUser?.uid }
                    // Set loading state to false after data is loaded
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                // Handle any error and set loading state to false
                _isLoading.value = false
                // In a real app, you might want to show an error message or handle the error more gracefully
            }
        }
    }

    /**
     * Function to navigate to the chat screen for a specific user.
     * It appends the user's ID to the route for navigating to the chat.
     *
     * @param uid The user ID to navigate to the chat screen for.
     */
    fun goToChat(uid: String) {
        // Navigating to the chat screen by concatenating the userId to the route
        navigator.navigateTo(Destination.Home.Chat.route + uid)
    }
}
