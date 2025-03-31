package com.bikerental.app.ui.pastrentals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bikerental.app.data.model.PastRentalDisplay
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

fun formatTimestamp(timestamp: com.google.firebase.Timestamp): String {
    val date = timestamp.toDate()
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(date)
}

@Composable
fun RentalItem(rentalDisplay: PastRentalDisplay) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // First line: Bike Name - Bike City
            Text(
                text = "${rentalDisplay.bikeName} - ${rentalDisplay.bikeCity}",
                style = MaterialTheme.typography.titleMedium
            )
            // Second line: Rental Period
            Text(
                text = "From ${formatTimestamp(rentalDisplay.startTime)} till ${formatTimestamp(rentalDisplay.endTime)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
