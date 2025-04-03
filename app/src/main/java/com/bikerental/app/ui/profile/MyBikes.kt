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

//             Dates
            Column {
                Text(
                    text = "Available from: ${formatTimestamp(bike.startTime)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text =  "Available until: ${formatTimestamp(bike.endTime)}",
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
fun formatTimestamp(timestamp: Timestamp): String {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return dateFormat.format(timestamp.toDate())
}
