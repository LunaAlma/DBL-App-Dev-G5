package com.bikerental.app.ui.profile

import com.bikerental.app.data.model.Bike
import com.bikerental.app.data.model.User
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Navigator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MyBikesViewModel @Inject constructor(
    navigator: Navigator,
    private val bikeRepository: BikeRepository,
    private val db: FirebaseFirestore,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : BaseViewModel(navigator) {

//    private val _users = MutableStateFlow<List<User>>(emptyList())
//    val users = _users.asStateFlow()
//
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading = _isLoading.asStateFlow()
//
//    init {
//        loadUsers()
//    }
//
//    private fun loadUsers() {
//        launchFirebase {
//            _isLoading.value = true
//            try {
//                userRepository.getUsers().collect { users ->
//                    _users.value = users.filter { it.uid == auth.currentUser?.uid }
//                    _isLoading.value = false
//                }
//            } catch (e: Exception) {
//                _isLoading.value = false
//                // Handle error
//            }
//        }
//    }


    init {
        loadBikes()
    }

    private val _bikes = MutableStateFlow<List<Bike>>(emptyList())
    val bikes = _bikes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private fun loadBikes() {
        launchFirebase {
            _isLoading.value = true
            try {
                bikeRepository.getBikes().collect { bikes ->
                    _bikes.value = bikes.filter { true }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
                // Handle error
            }
        }
    }
}