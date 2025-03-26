package com.bikerental.app.ui.home

import android.os.Messenger
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.data.repositories.TransactionRepository
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.data.model.Bike
import com.bikerental.app.data.model.User
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bikeRepository: BikeRepository,
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    navigator: Navigator
) : BaseViewModel(navigator) {

    private val _bikes = mutableStateOf(emptyList<Bike>())
    val bikes: State<List<Bike>> = _bikes

    private val _users = mutableStateOf(emptyList<User>())
    val users: State<List<User>> = _users

    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    init {
        loadUserDetails()
        loadBikes()
        loadUsers()

    }

    private fun loadUserDetails() {
        viewModelScope.launch {
            userRepository.getUserDetails()
                .onStart { _isLoading.value = true }
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { user ->
                    _user.value = user
                    _isLoading.value = false
                }
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            userRepository.getUsers()
                .onStart { _isLoading.value = true }
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { users ->
                    _users.value = users
                    _isLoading.value = false
                }
        }
    }


    private fun loadBikes() {
        viewModelScope.launch {
            bikeRepository.getBikes()
                .onStart { _isLoading.value = true }
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { bikes ->
                    _bikes.value = bikes
                    _isLoading.value = false
                }
        }
    }
}