package com.bikerental.app.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp

/**
 * Data class representing the information related to a bike marker on a map.
 *
 * This class is used to hold the data that is necessary to display a marker on the map,
 * including the bike's location, rating, price, availability times, and other relevant details.
 *
 * @property location The geographical location of the bike marker on the map.
 * @property rating The rating of the bike (usually a value between 1 and 5).
 * @property bikeImgId The unique identifier for the bike's image. Can be null if no image is available.
 * @property bikePrice The price per day for renting the bike.
 * @property city The city where the bike is located.
 * @property startTime The start time for the bike's availability.
 * @property endTime The end time for the bike's availability.
 * @property ownerId The unique identifier of the bike's owner.
 * @property bikeId The unique identifier for the bike.
 * @property bikeName The name of the bike.
 */
data class MarkerData(
    val location: LatLng,

    val rating: Int,

    val bikeImgId: String,

    val bikePrice: Double,

    val city: String,

    val startTime: Timestamp?,

    val endTime: Timestamp?,

    val ownerId: String,

    val bikeId: String,

    val bikeName: String,
)
