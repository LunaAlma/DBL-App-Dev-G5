package com.bikerental.app.data.model

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.Timestamp

data class Bike(
    val bikeId: String = "",

    val ownerId: String = "",

    val bikeName: String = "",

    val description: String = "",

    val price: Int = 0,

    val imageUrl: String = "",

    val location: GeoPoint = GeoPoint(0.0, 0.0),

    val city: String = "",

    val status: String = "available",

    val createdAt: Timestamp = Timestamp.now(),

    val startTime: Timestamp = Timestamp.now(),

    val endTime: Timestamp = Timestamp.now(),
)