package com.bikerental.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class SettingsViewModel : ViewModel() {
    var userName by mutableStateOf("Firstname Lastname")
    var userEmail by mutableStateOf("example@student.tue.nl")
    var userProfilePicture by mutableStateOf("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png")

    fun logout() {
        // TODO: Implement logout logic
    }

    fun deleteAccount() {
        // TODO: Implement account deletion logic
    }
}

