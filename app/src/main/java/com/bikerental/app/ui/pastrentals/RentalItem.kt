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

/**
 * Formats a Firebase timestamp into a human-readable string.
 *
 * This function converts a Firebase Timestamp object into a formatted string representing
 * the date and time in the format: "dd MMM yyyy, HH:mm".
 *
 * @param timestamp The Firebase timestamp to format.
 * @return A string representation of the formatted date and time.
 */
fun formatTimestamp(timestamp: com.google.firebase.Timestamp): String {
    val date = timestamp.toDate()
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(date)
}

/**
 * Composable function to display a rental item in a card.
 *
 * This composable function displays the details of a past rental, including the bike name,
 * bike city, and the rental period (from start time to end time). The rental information
 * is displayed inside a MaterialCard for a structured and styled UI presentation.
 *
 * @param rentalDisplay The rental data to display. It includes bike details and rental period.
 */
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
