package com.bikerental.app.data.datasource

import com.bikerental.app.data.model.Bike
import com.bikerental.app.data.model.Transaction
import com.bikerental.app.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseDataSource @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
){

    fun getUsers(): Flow<List<User>> = callbackFlow {
        val subscription = db.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val users = snapshot?.toObjects(User::class.java) ?: emptyList()
                trySend(users)
            }

        awaitClose { subscription.remove() }
    }

    fun getUserDetails(): Flow<User> = callbackFlow {
        val subscription = db.collection("users").document(auth.currentUser!!.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val user = snapshot?.toObject(User::class.java)
                if(user != null) {
                    trySend(user)
                } else {
                    close(IllegalStateException("User document not found"))
                }

            }

        awaitClose { subscription.remove() }
    }

    suspend fun updateUserDetails(user: User) {
        if(auth.currentUser?.uid != null) {
            db.collection("users").document(auth.currentUser!!.uid).set(user)
        }
    }

    suspend fun createTransaction(transaction: Transaction) {
        db.collection("transactions").document(transaction.transactionId).set(transaction)

    }

    fun getAllUserTransactions(): Flow<List<Transaction>> = callbackFlow {
        val subscription = db.collection("transactions")
            .addSnapshotListener { snapshot, error ->
                if(error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val transactions = snapshot?.toObjects(Transaction::class.java) ?: emptyList()
                trySend(transactions)
            }

        awaitClose { subscription.remove() }
    }

    fun getBikes(): Flow<List<Bike>> = callbackFlow {
        val subscription = db.collection("bikes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val bikes = snapshot?.toObjects(Bike::class.java) ?: emptyList()
                trySend(bikes)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addBike(bike: Bike) {
        db.collection("bikes").document(bike.bikeId).set(bike)
    }

    suspend fun deleteBike(bike: Bike) {
        db.collection("bikes").document(bike.bikeId).delete()
    }



}