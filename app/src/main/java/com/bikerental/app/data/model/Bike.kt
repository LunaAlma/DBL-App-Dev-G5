package com.bikerental.app.data.model

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.Timestamp

data class Bike(
    val bikeId: String,

    val ownerId: String,

    val bikeName: String,

    val price: Int,

    val picture: String,

    val location: GeoPoint,

    val city: String,

    val startTime: Timestamp,

    val endTime: Timestamp
)