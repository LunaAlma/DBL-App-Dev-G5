package com.bikerental.app.data.model

import com.google.firebase.Timestamp

/**
 * Data class representing a message in the bike rental application.
 *
 * This class holds the details of a message, such as the unique identifiers for the sender and receiver,
 * an optional image associated with the message, and the timestamp when the message was created.
 *
 * @property id The unique identifier for the message.
 * @property toId The unique identifier for the recipient of the message.
 * @property imageUrl The optional URL to an image associated with the message. Can be null if no image is provided.
 * @property timestamp The timestamp indicating when the message was created or sent.
 */
data class Message(
    val id: String = "",

    val toId: String = "",

    val imageUrl: String? = null,

    val timestamp: Timestamp = Timestamp.now(),
)