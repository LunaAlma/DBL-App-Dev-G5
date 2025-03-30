package com.bikerental.app.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.bikerental.app.data.model.Bike
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun BikeListScreen(
    navController: NavController,
    modifier: Modifier,
    viewModel: SearchViewModel
) {
    var bikeList by remember { mutableStateOf(listOf<Bike>()) }
    var selectedCity by remember { mutableStateOf("Select City") }
    var selectedStartDate by remember { mutableStateOf<Timestamp?>(null) }
    var selectedEndDate by remember { mutableStateOf<Timestamp?>(null) }

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(selectedCity, selectedStartDate, selectedEndDate) {
        try {
            val activeRentals = db.collection("rentals")
                .whereEqualTo("status", "active")
                .get().await()
                .documents.mapNotNull { it.getString("bikeId") }

            val bikes = db.collection("bikes")
                .whereEqualTo("city", selectedCity)
                .get().await()
                .documents.mapNotNull { doc ->
                    val bike = doc.toObject(Bike::class.java)
                    if (bike != null && !activeRentals.contains(bike.bikeId)) {
                        if (selectedStartDate != null && selectedEndDate != null) {
                            val isAvailable = (bike.startTime >= selectedStartDate!!) &&
                                    (bike.endTime <= selectedEndDate!!)
                            if (isAvailable) bike else null
                        } else {
                            bike
                        }
                    } else null
                }
            bikeList = bikes
        } catch (e: Exception) {
            Log.e("Firebase", "Error fetching bikes", e)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        RentalPeriodSelector(
            selectedCity = selectedCity,
            onCitySelected = { selectedCity = it },
            selectedStartDate = selectedStartDate,
            selectedEndDate = selectedEndDate,
            onDateSelected = { start, end ->
                selectedStartDate = start
                selectedEndDate = end
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Nearby listings",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(16.dp)
        )

        BikeList(bikeList, navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentalPeriodSelector(
    selectedCity: String,
    onCitySelected: (String) -> Unit,
    selectedStartDate: Timestamp?,
    selectedEndDate: Timestamp?,
    onDateSelected: (Timestamp, Timestamp) -> Unit
) {
    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("MMM dd", Locale.getDefault())
    var startDateText by remember { mutableStateOf("Select Start") }
    var endDateText by remember { mutableStateOf("Select End") }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    if (showStartDatePicker) {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                val date = selectedCalendar.time
                startDateText = dateFormatter.format(date)
                onDateSelected(Timestamp(date), selectedEndDate ?: Timestamp(date))
                showStartDatePicker = false
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (showEndDatePicker) {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                val date = selectedCalendar.time
                endDateText = dateFormatter.format(date)
                onDateSelected(selectedStartDate ?: Timestamp(date), Timestamp(date))
                showEndDatePicker = false
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp, start = 16.dp, end = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // SearchBar as a Dropdown
            Column(modifier = Modifier.weight(1f)) {
                OutlinedButton(onClick = { expanded = !expanded }) {
                    Text(selectedCity)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    listOf("Amsterdam", "Eindhoven", "Utrecht", "Den Haag", "Rotterdam").forEach { city ->
                        DropdownMenuItem(
                            text = { Text(city) },
                            onClick = {
                                onCitySelected(city)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Date Buttons
            Button(onClick = { showStartDatePicker = true }, modifier = Modifier.weight(1f)) {
                Text(startDateText, maxLines = 1)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { showEndDatePicker = true }, modifier = Modifier.weight(1f)) {
                Text(endDateText, maxLines = 1)
            }
        }
    }
}

@Composable
fun BikeList(bikeList: List<Bike>, navController: NavController) {
    LazyRow(modifier = Modifier.padding(start = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(bikeList) { bike ->
            BikeCard(bike) { navController.navigate("bikeDetails/${bike.bikeId}") }
        }
    }
}

@Composable
fun BikeCard(bike: Bike, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(150.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(bike.imageUrl),
                contentDescription = null,
                modifier = Modifier.height(100.dp).fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = bike.bikeName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = "${bike.price} €/hr",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}