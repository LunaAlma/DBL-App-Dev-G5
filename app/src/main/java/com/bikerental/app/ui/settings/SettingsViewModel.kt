package com.bikerental.app.ui.settings

import android.os.Messenger
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.bikerental.app.ui.MainViewModel
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    navigator: Navigator,
//    private val authRepository: AuthRepository,
//    private val bikeRepository: BikeRepository,
//    private val transactionRepository: TransactionRepository,
//    private val userRepository: UserRepository
) : BaseViewModel(navigator) {
//    private val _isLoading = mutableStateOf(false)
//    val isLoading: State<Boolean> = _isLoading
//
//    private val _error = mutableStateOf<String?>(null)
//    val error: State<String?> = _error
//
//    private val _user = mutableStateOf<User?>(null)
//    val user: State<User?> = _user
//
//    init {
//        loadUserDetails()
//    }

    var userName by mutableStateOf("Firstname Lastname")
    var userEmail by mutableStateOf("example@student.tue.nl")
    var userProfilePicture by mutableStateOf("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png")

//    private fun loadUserDetails() {
//        viewModelScope.launch {
//            userRepository.getUserDetails()
//                .onStart { _isLoading.value = true }
//                .catch { e ->
//                    _error.value = e.message
//                    _isLoading.value = false
//                }
//                .collect { user ->
//                    _user.value = user
//                    _isLoading.value = false
//                }
//        }
//    }

    fun logout() {
        // TODO: Implement logout logic
    }

    fun deleteAccount() {
        // TODO: Implement account deletion logic
    }
}

