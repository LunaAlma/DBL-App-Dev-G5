package com.bikerental.app.data.model
import com.google.firebase.Timestamp

data class Rental(
    val id: String = "",

    val bikeId: String = "",

    val renterId: String = "",

    val ownerId: String = "",

    val status: String = "",

    val startTime: Timestamp = Timestamp.now(),

    val endTime: Timestamp = Timestamp.now(),
)
