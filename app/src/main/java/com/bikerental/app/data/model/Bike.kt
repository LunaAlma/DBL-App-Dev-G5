package com.bikerental.app.data.model

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.Timestamp

/**
 * Data model representing a bike in the system.
 *
 * This class holds the details of a bike, including information such as its ID, owner,
 * price, image URL, location, and availability times.
 *
 * @property bikeId The unique identifier for the bike.
 * @property ownerId The ID of the user who owns the bike.
 * @property bikeName The name of the bike (e.g., "Mountain Bike Pro").
 * @property price The rental price of the bike per day.
 * @property imageUrl The URL of the bike's image.
 * @property location The geographical location of the bike, represented as latitude and longitude.
 * @property city The city where the bike is located.
 * @property startTime The start time of the bike's availability (when it can be rented).
 * @property endTime The end time of the bike's availability (when it is no longer available for rent).
 */
data class Bike(
    val bikeId: String = "",

    /** The ID of the bike owner. */
    val ownerId: String = "",

    /** The name of the bike. */
    val bikeName: String = "",

    /** The rental price of the bike per day. */
    val price: Double = 0.00,

    /** The URL of the bike's image. */
    val imageUrl: String = "",

    /** The geographical location of the bike. */
    val location: GeoPoint = GeoPoint(0.0 , 0.0),

    /** The city where the bike is located. */
    val city: String = "",

    /** The start time when the bike is available for rent. */
    val startTime: Timestamp = Timestamp.now(),

    /** The end time when the bike is no longer available for rent. */
    val endTime: Timestamp = Timestamp.now(),
)
