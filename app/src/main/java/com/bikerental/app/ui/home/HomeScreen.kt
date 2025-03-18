package com.bikerental.app.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bikerental.app.data.model.Bike
import com.bikerental.app.ui.theme.BikeRentalTheme
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val bikes by viewModel.bikes
    val isLoading by viewModel.isLoading
    val error by viewModel.error

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        } else {
            BikeList(bikes = bikes)
        }

        error?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun BikeList(bikes: List<Bike>) {
    LazyColumn {
        items(bikes) { bike ->
            BikeItem(bike = bike)
        }
    }
}

@Composable
fun BikeItem(bike: Bike) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = bike.bikeId, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = bike.price)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = bike.ownerId)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = bike.price)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = bike.picture)
        }
    }
}