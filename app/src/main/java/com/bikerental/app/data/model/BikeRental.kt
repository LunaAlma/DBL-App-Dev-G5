package com.bikerental.app.data.model

import com.google.firebase.Timestamp

data class BikeRental(
    val id: String = "",
    val bikeType: String = "",
    val description: String = "",
    val city: String = "",
    val rentalStartDate: String = "",
    val rentalEndDate: String = "",
    val imageUrl: String = "",
    val userId: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val status: String = "available" // available, rented, completed
) 