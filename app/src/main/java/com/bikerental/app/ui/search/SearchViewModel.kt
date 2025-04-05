package com.bikerental.app.ui.search

import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel responsible for managing the data and business logic for the bike rental search functionality.
 * This ViewModel is annotated with @HiltViewModel to allow dependency injection.
 * It extends from the BaseViewModel and communicates with the Navigator to handle navigation.
 *
 * @param navigator The Navigator that helps with navigating to different screens.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    navigator: Navigator,
) : BaseViewModel(navigator) {

    companion object {
        const val TAG = "SearchViewModel"
    }

}
