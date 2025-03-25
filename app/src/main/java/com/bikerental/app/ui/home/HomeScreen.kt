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
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.bikerental.app.data.model.User

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val bikes by viewModel.bikes
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    val users by viewModel.users

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        } else {
            BikeList(bikes = bikes)

            UserList(users = users)
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
fun UserList(users: List<User>) {
    LazyColumn {
        items(users) { user ->
            UserItem(user = user)
        }
    }
}

@Composable
fun BikeItem(bike: Bike) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = bike.bikeId, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = bike.price.toString())
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = bike.ownerId)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = bike.bikeId)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = bike.picture)
        }
    }
}
@Composable
fun UserItem(user: User) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = user.uid, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = user.firstName)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = user.lastName)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = user.username)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = user.email)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = user.avgRating.toString())
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = user.profilePicture)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = user.currentLocation.toString())
        }
    }
}