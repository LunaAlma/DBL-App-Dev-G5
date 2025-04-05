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

/**
 * A Composable function that represents the screen displaying the past rental history of the user.
 * It includes a list of past rental items and a top bar with a back button to navigate back.
 *
 * @param navigator The Navigator used to manage navigation actions.
 * @param viewModel The ViewModel containing the state and logic related to past rentals.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastRentalsScreen(
    navigator: Navigator,
    viewModel: PastRentalsViewModel
) {
    // Collect the state of past rental displays from the ViewModel
    val pastRentalDisplaysState = viewModel.pastRentalDisplays.collectAsState(initial = emptyList())
    val pastRentalDisplays = pastRentalDisplaysState.value

    // Scaffold for the screen layout
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Past Rentals") }, // Title for the top bar
                navigationIcon = {
                    // Back button to navigate to the previous screen
                    IconButton(onClick = { navigator.navigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack, // Icon for back button
                            contentDescription = "Back" // Description for accessibility
                        )
                    }
                }
            )
        }
    ) { padding ->
        // List of past rental items displayed using LazyColumn
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(pastRentalDisplays) { rentalDisplay ->
                // Render each rental display item
                RentalItem(rentalDisplay = rentalDisplay)
            }
        }
    }
}
