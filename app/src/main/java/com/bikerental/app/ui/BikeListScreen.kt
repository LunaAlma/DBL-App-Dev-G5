package com.bikerental.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bikerental.app.R

@Preview
@Composable
fun BikeListScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFA8E890))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Connect with bike\nowners and renters",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                SearchBar()
            }
        }

        // list
        Text(
            text = "Nearby listings",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        BikeList()
        Spacer(modifier = Modifier.weight(1f)) //push bottom navigation to the bottom
      //  BottomNavigationBar()
    }
}

@Composable
fun SearchBar() {
    TextField(
        value = "", onValueChange = {},
        placeholder = { Text("Enter location or bike type") },
        trailingIcon = { Icon(imageVector = Icons.Default.Menu, contentDescription = "Search") },
        modifier = Modifier.fillMaxWidth().background(Color.White)
    )
}

@Composable
fun BikeList() {
    val bikeList = listOf(
        Bike("100 meters away", R.drawable.bike1),
        Bike("150 meters away", R.drawable.bike2)
    )

    LazyRow(modifier = Modifier.padding(start = 16.dp)) {
        items(bikeList) { bike ->
            BikeCard(bike)
        }
    }
}

@Composable
fun BikeCard(bike: Bike) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(150.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = bike.imageRes),
                contentDescription = null,
                modifier = Modifier.height(100.dp)
            )
            Text(
                text = bike.name,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

//finalise once we agree on what we want in the nav bar
//@Composable
//fun BottomNavigationBar() {
//    NavigationBar(containerColor = Color.White) {
//        NavigationBarItem(
//            selected = false, onClick = {},
//            icon = { Icon(painterResource(id = R.drawable.ic_), contentDescription = "Add") }
//        )
//        NavigationBarItem(
//            selected = true, onClick = {},
//            icon = { Icon(painterResource(id = R.drawable.ic_), contentDescription = "Home") }
//        )
//        NavigationBarItem(
//            selected = false, onClick = {},
//            icon = { Icon(painterResource(id = R.drawable.ic_), contentDescription = "Search") }
//        )
//        NavigationBarItem(
//            selected = false, onClick = {},
//            icon = { Icon(painterResource(id = R.drawable.ic_), contentDescription = "Menu") }
//        )
//    }
//}

data class Bike(val name: String, val imageRes: Int)


