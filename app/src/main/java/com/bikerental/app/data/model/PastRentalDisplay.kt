package com.bikerental.app.data.model

import com.google.firebase.Timestamp

data class PastRentalDisplay(
    val bikeName: String,
    val bikeCity: String,
    val startTime: Timestamp,
    val endTime: Timestamp,
    val status: String
)
