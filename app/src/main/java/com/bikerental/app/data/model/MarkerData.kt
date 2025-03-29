package com.bikerental.app.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp

data class MarkerData(
    val location: LatLng,

    val ownerName: String,

    val rating: Int,

    val bikeImgId: String, // Null option here? String? = null

    val bikePrice: Int,

    val city: String,

    val startTime: Timestamp? = null,

    val endTime: Timestamp? = null,

    val ownerId: String,

    val bikeId: String
)