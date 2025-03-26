package com.bikerental.app.data.model

data class Message(
    val fromId: String,

    val toId: String,

    val content: String,

    val timestamp: String,
)