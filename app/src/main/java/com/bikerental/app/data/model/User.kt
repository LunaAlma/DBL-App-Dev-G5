package com.bikerental.app.data.model

import com.google.firebase.firestore.GeoPoint;

data class User(
    val uid: String = "",

    val name: String = "",

    val email: String = "",
//
//    val currentLocation: GeoPoint = GeoPoint(0.0, 0.0),
//
    val profilePicture: String = "",

    val totalRating: Int = 0,

    val numberOfRatings: Int = 0,
)