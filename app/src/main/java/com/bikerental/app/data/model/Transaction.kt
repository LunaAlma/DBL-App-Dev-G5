package com.bikerental.app.data.model

data class Transaction(
    val transactionId: String = "",

    val fromId: String = "",

    val toId: String = "",

    val timeStamp: String = "",

    val status: String = "",

    val fee: Double = 0.00,

    val ownerAmount: Double = 0.00,

    val totalAmount: Double = 0.00
)
