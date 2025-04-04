package com.bikerental.app.data.model

/**
 * Data class representing a user in the bike rental application.
 *
 * This class holds the details of a user, including their unique user ID, name, email, profile image URL,
 * and rating information such as total rating and the number of ratings the user has received.
 *
 * @property uid The unique identifier for the user.
 * @property name The name of the user.
 * @property email The email address of the user.
 * @property profileImageUrl The URL of the user's profile image.
 * @property totalRating The total rating score the user has received from other users.
 * @property numberOfRatings The number of ratings the user has received from other users.
 */
data class User(
    val uid: String = "",

    val name: String = "",

    val email: String = "",

    val profileImageUrl: String = "",

    val totalRating: Int = 0,

    val numberOfRatings: Int = 0,
)