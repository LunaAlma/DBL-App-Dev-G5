package com.bikerental.app.data.model

import com.google.firebase.Timestamp

data class Message(
    val id: String = "",

    val toId: String = "",

    val imageUrl: String? = null,

    val timestamp: Timestamp = Timestamp.now(),
)