package com.bikerental.app.ui.pastrentals

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastRentalsScreen(
    navigator: Navigator,
    viewModel: PastRentalsViewModel
) {
    val pastRentalDisplaysState = viewModel.pastRentalDisplays.collectAsState(initial = emptyList())
    val pastRentalDisplays = pastRentalDisplaysState.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Past Rentals") },
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
