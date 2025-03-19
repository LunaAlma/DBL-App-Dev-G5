package com.bikerental.app.ui.settings

import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.bikerental.app.MainViewModel
import com.bikerental.app.data.model.User
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.data.repositories.TransactionRepository
import com.bikerental.app.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
//    private val authRepository: AuthRepository,
//    private val bikeRepository: BikeRepository,
//    private val transactionRepository: TransactionRepository,
//    private val userRepository: UserRepository
) : MainViewModel(

) {
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

