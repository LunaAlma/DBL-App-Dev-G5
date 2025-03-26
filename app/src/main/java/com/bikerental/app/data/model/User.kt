package com.bikerental.app.data.model

import com.google.firebase.firestore.GeoPoint;

data class User(
    val uid: String = "",

    val firstName: String = "",

    val lastName: String = "",

    val username: String = "",

    val email: String = "",

    val profilePicture: String = "",

    val totalRating: Int = 0,

    val numberOfRatings: Int = 0,
)