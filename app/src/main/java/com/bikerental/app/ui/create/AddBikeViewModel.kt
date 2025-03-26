package com.bikerental.app.ui.create

import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddBikeViewModel @Inject constructor(
     navigator: Navigator,
) : BaseViewModel(navigator) {
    companion object {
        const val TAG = "LoginViewModel"
    }
}