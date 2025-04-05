package com.bikerental.app.data.model

import com.google.firebase.Timestamp

/**
 * Data class representing the display information for a past rental in the bike rental application.
 *
 * This class holds the details of a completed or past rental, including the bike name, rental city,
 * the start and end times of the rental period, and the current status of the rental.
 *
 * @property bikeName The name of the bike that was rented.
 * @property bikeCity The city where the bike was rented.
 * @property startTime The timestamp indicating the start time of the rental.
 * @property endTime The timestamp indicating the end time of the rental.
 * @property status The current status of the rental (e.g., "Completed", "Cancelled").
 */
data class PastRentalDisplay(
    val bikeName: String,
    val bikeCity: String,
    val startTime: Timestamp,
    val endTime: Timestamp,
    val status: String
)
