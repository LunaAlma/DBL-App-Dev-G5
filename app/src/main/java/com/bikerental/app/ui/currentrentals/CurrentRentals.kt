package com.bikerental.app.ui.currentrentals


import com.bikerental.app.ui.pastrentals.RentalItem

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bikerental.app.ui.navigation.Navigator
import androidx.compose.runtime.collectAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.bikerental.app.ui.currentrentals.CurrentRentalsViewModel

/**
 * Composable function that displays the current rentals screen.
 *
 * This screen shows a list of current rentals, and allows the user to navigate back
 * to the previous screen. It uses a `LazyColumn` to display the rental items.
 *
 * @param navigator The navigator used to handle navigation actions.
 * @param viewModel The ViewModel that holds the current rental data and business logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentRentals(
    navigator: Navigator,
    viewModel: CurrentRentalsViewModel
) {
    // Collects the current rental displays as state from the ViewModel
    val pastRentalDisplaysState = viewModel.currentRentalDisplays.collectAsState(initial = emptyList())
    val pastRentalDisplays = pastRentalDisplaysState.value

    // Scaffold layout for the current rentals screen with a top app bar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Current Rentals") },
                navigationIcon = {
                    // Navigation icon that allows the user to go back
                    IconButton(onClick = { navigator.navigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        // LazyColumn to display each rental item
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(pastRentalDisplays) { rentalDisplay ->
                RentalItem(rentalDisplay = rentalDisplay)
            }
        }
    }
}