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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentRentals(
    navigator: Navigator,
    viewModel: CurrentRentalsViewModel
) {
    val pastRentalDisplaysState = viewModel.currentRentalDisplays.collectAsState(initial = emptyList())
    val pastRentalDisplays = pastRentalDisplaysState.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Current Rentals") },
                navigationIcon = {
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
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(pastRentalDisplays) { rentalDisplay ->
                RentalItem(rentalDisplay = rentalDisplay)
            }
        }
    }
}