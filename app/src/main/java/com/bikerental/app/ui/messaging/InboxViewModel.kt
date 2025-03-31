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

@HiltViewModel
class InboxViewModel @Inject constructor(
    navigator: Navigator,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : BaseViewModel(navigator) {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        launchFirebase {
            _isLoading.value = true
            try {
                userRepository.getUsers().collect { users ->
                    _users.value = users.filter { it.uid != auth.currentUser?.uid }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
                // Handle error
            }
        }
    }

        fun goToChat(uid: String) {
            navigator.navigateTo(Destination.Home.Chat.route + uid)
        }
}