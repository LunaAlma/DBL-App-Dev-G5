package com.bikerental.app.data.repositories

import com.bikerental.app.data.datasource.FirebaseDataSource
import com.bikerental.app.data.model.Transaction
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class TransactionRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {
    fun getAllUserTransactions(): Flow<List<Transaction>> =
        firebaseDataSource.getAllUserTransactions()

    suspend fun createTransaction(transaction: Transaction) =
        firebaseDataSource.createTransaction(transaction)
}