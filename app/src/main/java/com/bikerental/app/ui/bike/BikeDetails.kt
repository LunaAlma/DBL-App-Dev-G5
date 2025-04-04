package com.bikerental.app.ui.bike

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.bikerental.app.R
import com.bikerental.app.data.model.Bike
import com.bikerental.app.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun BikeDetails(
    bikeId: String,
    viewModel: BikeDetailsViewModel = hiltViewModel()
) {
    val bike by viewModel.bikeDetails.collectAsState() // This is a nullable Bike (Bike?)
    val isLoading by viewModel.isLoading.collectAsState()
    val owner by viewModel.ownerDetails.collectAsState()

    val dateFormatter = remember {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bike Details") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            // Handle bike being null explicitly
            bike?.let { bikeData ->
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    item {
                        AsyncImage(
                            model = bikeData.imageUrl,
                            contentDescription = "Bike image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = bikeData.bikeName,
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Price and Rating
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "€${bikeData.price}/day",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                owner?.let {
                                    Text(
                                        text = "Owner: ${it.name}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            // Location
                            Divider(modifier = Modifier.padding(vertical = 16.dp))
                            Text(
                                text = "Location Details",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "City: ${bikeData.city}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Coordinates: ${"%.4f".format(bikeData.location.latitude)}, " +
                                        "${"%.4f".format(bikeData.location.longitude)}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            // Availability
                            Divider(modifier = Modifier.padding(vertical = 16.dp))
                            Text(
                                text = "Availability",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "From: ${dateFormatter.format(bikeData.startTime.toDate())}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "To: ${dateFormatter.format(bikeData.endTime.toDate())}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            // Rental Button
                            Button(
                                onClick = { /* Handle rental */ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                            ) {
                                Text("Rent This Bike")
                            }
                        }
                    }
                }
            } ?: Text(
                "Bike not found",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun BikeRentalCard(
    bike: Bike,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick) // Add clickable modifier
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = bike.imageUrl,
                contentDescription = "Bike Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = Color(0xFFFFC107)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "5.0", style = MaterialTheme.typography.bodyMedium)
                }
                Text(text = "€${bike.price}/day", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = bike.bikeName,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { /* Handle rent action */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB2E59C))
            ) {
                Text("Rent Now")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BikeRentalCardPreview() {
    AppTheme {
        BikeRentalCard(
            bike = Bike(
                bikeId = "1",
                bikeName = "Mountain Bike Pro",
                price = 29.99,
                imageUrl = "",
                ownerId = "owner123",
                city = "Amsterdam"
            ),
            onClick = {}
        )
    }
}
