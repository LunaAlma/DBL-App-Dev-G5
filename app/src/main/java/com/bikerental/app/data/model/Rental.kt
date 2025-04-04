package com.bikerental.app.data.model
import com.google.firebase.Timestamp

/**
 * Data class representing a rental transaction in the bike rental application.
 *
 * This class holds the details of a rental, including the unique rental ID, the associated bike ID,
 * the renter's ID, the owner's ID, the rental status, and the start and end times of the rental period.
 *
 * @property id The unique identifier for the rental.
 * @property bikeId The ID of the bike being rented.
 * @property renterId The ID of the user renting the bike.
 * @property ownerId The ID of the bike's owner.
 * @property status The current status of the rental (e.g., "In Progress", "Completed", "Cancelled").
 * @property startTime The timestamp indicating the start time of the rental.
 * @property endTime The timestamp indicating the end time of the rental.
 */
data class Rental(
    val id: String = "",

    val bikeId: String = "",

    val renterId: String = "",

    val ownerId: String = "",

    val status: String = "",

    val startTime: Timestamp = Timestamp.now(),

    val endTime: Timestamp = Timestamp.now(),
)
