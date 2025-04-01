package com.bikerental.app.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp

data class MarkerData(
    val location: LatLng,

    val rating: Int,

    val bikeImgId: String, // Null option here? String? = null

    val bikePrice: Double,

    val city: String,

    val startTime: Timestamp?,

    val endTime: Timestamp?,

    val ownerId: String,

    val bikeId: String,

    val bikeName: String,
)
