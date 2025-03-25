package com.bikerental.app.ui.base

import android.os.Messenger
import androidx.lifecycle.ViewModel
import com.bikerental.app.ui.navigation.Navigator


abstract class BaseViewModel(
    private val navigator: Navigator
) : ViewModel() {

    companion object {
        const val TAG = "BaseViewModel"
    }
}