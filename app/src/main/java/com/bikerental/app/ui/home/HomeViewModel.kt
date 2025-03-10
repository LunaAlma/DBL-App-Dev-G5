package com.bikerental.app.ui.home

import com.bikerental.app.MainViewModel
import com.bikerental.app.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val authRepository: AuthRepository) : MainViewModel() {

}