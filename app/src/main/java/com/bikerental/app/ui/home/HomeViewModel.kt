package com.bikerental.app.ui.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.bikerental.app.MainViewModel
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.data.repositories.TransactionRepository
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.data.model.Bike
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
    private val userRepository: UserRepository
) : MainViewModel() {

    private val _bikes = mutableStateOf(emptyList<Bike>())
    val bikes: State<List<Bike>> = _bikes

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    init {
        loadBikes()
    }


    private fun loadBikes() {
        viewModelScope.launch {
            bikeRepository.getBikes()
                .onStart { _isLoading.value = true }
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { posts ->
                    _bikes.value = posts
                    _isLoading.value = false
                }
        }
    }
}