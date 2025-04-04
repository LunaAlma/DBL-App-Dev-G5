package com.bikerental.app.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bikerental.app.data.model.Bike
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Composable function to display the user's bikes.
 *
 * This function serves as the entry point to render a list of bikes owned by the user.
 * It also handles the loading state while the bikes are being fetched and allows the user to delete bikes.
 *
 * @param modifier The modifier to be applied to the component.
 * @param viewModel The view model that holds the state of the bikes and the loading indicator.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBikes(
    modifier: Modifier = Modifier,
    viewModel: MyBikesViewModel
) {
    MyBikesView(
        modifier = modifier,
        bikes = viewModel.bikes.collectAsState().value,
        isLoading = viewModel.isLoading.collectAsState().value,
        onDeleteBike = { bike -> viewModel.deleteBike(bike) }
    )
}

/**
 * Composable function to render a list of bikes.
 *
 * This function renders a list of bikes, showing a loading indicator while the bikes are being fetched.
 * Each bike is displayed in a card, and the user can delete bikes from the list.
 *
 * @param modifier The modifier to be applied to the component.
 * @param bikes A list of bikes to display.
 * @param isLoading A boolean indicating whether the data is loading.
 * @param onDeleteBike A function that gets called when the user deletes a bike.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBikesView(
    modifier: Modifier,
    bikes: List<Bike>,
    isLoading: Boolean,
    onDeleteBike: (Bike) -> Unit
) {
    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else {
        LazyColumn(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = bikes,
                key = { it.bikeId }
            ) {
                MyBikeCard(
                    bike = it,
                    onDelete = { onDeleteBike(it) }
                )
            }
        }
    }
}

/**
 * Composable function to render a single bike in a card.
 *
 * This function displays detailed information about a bike, including the bike's name, image, pricing,
 * location, and availability. The user can delete the bike by clicking the delete icon.
 *
 * @param bike The bike to display.
 * @param onDelete A function to call when the user deletes the bike.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBikeCard(
    bike: Bike,
    onDelete: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = bike.bikeName,
                    style = MaterialTheme.typography.headlineSmall
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete bike"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bike Image
            AsyncImage(
                model = bike.imageUrl,
                contentDescription = "Bike image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pricing and Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "€${bike.price}/hour",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = bike.city,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dates
            Column {
                Text(
                    text = "Available from: ${formatTimestamp(bike.startTime)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Available until: ${formatTimestamp(bike.endTime)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Coordinates
            Text(
                text = "Location: %.4f, %.4f".format(
                    bike.location.latitude,
                    bike.location.longitude
                ),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * Formats a Firebase timestamp into a human-readable string.
 *
 * This function converts a Firebase Timestamp object into a formatted string representing
 * the date in the format: "dd MMM yyyy".
 *
 * @param timestamp The Firebase timestamp to format.
 * @return A string representation of the formatted date.
 */
fun formatTimestamp(timestamp: Timestamp): String {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return dateFormat.format(timestamp.toDate())
}
