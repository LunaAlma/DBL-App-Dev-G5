package com.bikerental.app.data.model

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.Timestamp

data class Bike(
    val bikeId: String = "",

    val ownerId: String = "",

    val bikeName: String = "",

    val price: Int = 0,

    val picture: String = "",

    val location: GeoPoint? = null,

    val city: String = "",

    val startTime: Timestamp? = null,

    val endTime: Timestamp? = null
)