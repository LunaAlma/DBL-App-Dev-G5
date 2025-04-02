package com.bikerental.app.ui.bike

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.bikerental.app.R
import com.bikerental.app.data.model.Bike
import com.bikerental.app.ui.theme.AppTheme

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun BikeDetails(bikeId: String) {
    val viewModel: BikeDetailsViewModel = hiltViewModel()
    val bike by viewModel.bikeDetails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(bikeId) {
        viewModel.fetchBikeDetails(bikeId)
    }

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else {
        bike?.let { BikeRentalCard(bike = it) }
    }
}

@Composable
fun BikeRentalCard(bike: Bike) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
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
            )
        )
    }
}