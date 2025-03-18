package com.bikerental.app.data.datasource

import com.bikerental.app.data.model.Bike
import com.bikerental.app.data.model.Transaction
import com.bikerental.app.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
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

    suspend fun updateUserDetails(user: User) {
        if(auth.currentUser?.uid != null) {
            db.collection("users").document(auth.currentUser!!.uid).set(user)
        }
    }

//    suspend fun getUserDetails(user: User): User {
//        if(auth.currentUser?.uid != null) {
//        val docRef = db.collection("users").document(auth.currentUser!!.uid)
//        return docRef.get().await().let {
//            snapshot ->
//                if (snapshot.exists()) {
//                    snapshot.toObject(User::class.java)?.copy(id = snapshot.id)
//                        ?: throw IllegalStateException("Invalid User Data")
//                } else {
//                    throw IllegalStateException("User not found")
//                }
//        }
//    }

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