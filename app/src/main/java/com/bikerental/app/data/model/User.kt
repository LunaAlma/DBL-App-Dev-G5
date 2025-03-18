package com.bikerental.app.data.model

import com.google.firebase.firestore.GeoPoint;

data class User(
    val uid: String = "",

    val firstName: String = "",

    val lastName: String = "",

    val username: String = "",

    val email: String = "",

    val avgRating: Int = 0,

    val currentLocation: GeoPoint = GeoPoint(0.0, 0.0),

    val profilePicture: String = ""
)