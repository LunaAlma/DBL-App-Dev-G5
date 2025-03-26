package com.bikerental.app.ui.messaging

import androidx.lifecycle.SavedStateHandle
import com.bikerental.app.data.model.User
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.ui.base.BaseViewModel
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.bikerental.app.ui.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    navigator: Navigator,
    private val userRepository: UserRepository
) : BaseViewModel(navigator) {

    val userId: String = savedStateHandle.get<String>("userId") ?: ""

    companion object {
        const val TAG = "InboxViewModel"
    }

    val otherUser = MutableStateFlow<User?>(null)

    init {
        loadUserData()
    }

    private fun loadUserData() {
        launchFirebase {
            userRepository.getUsers().collect { users ->
                otherUser.value = users.firstOrNull { it.uid == userId }
            }
        }
    }
}