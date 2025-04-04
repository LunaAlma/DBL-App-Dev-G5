package com.bikerental.app.ui.messaging

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.bikerental.app.data.model.Message
import com.bikerental.app.data.model.User
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.ui.base.BaseViewModel
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.bikerental.app.ui.navigation.Navigator
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    navigator: Navigator,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : BaseViewModel(navigator) {

//    val userId: String = savedStateHandle.get<String>("userId") ?: ""
//
//    val otherUser = MutableStateFlow<User?>(null)
//
//    init {
//        loadUserData()
//    }
//
//    private fun loadUserData() {
//        launchFirebase {
//            userRepository.getUsers().collect { users ->
//                otherUser.value = users.firstOrNull { it.uid == userId }
//            }
//        }
//    }

    val userId: String = savedStateHandle.get<String>("userId") ?: ""
    val currentUserId = auth.currentUser?.uid ?: ""

    val otherUser = MutableStateFlow<User?>(null)
    val messages = MutableStateFlow<List<Message>>(emptyList())

    private val storage = FirebaseStorage.getInstance()

    init {
        loadUserData()
        // Optionally, subscribe to messages in real time
        listenToMessages()
    }

    private fun loadUserData() {
        launchFirebase {
            userRepository.getUsers().collect { users ->
                otherUser.value = users.firstOrNull { it.uid == userId }
            }
        }
    }

    fun listenToMessages() {
        val chatId = if (currentUserId < userId) "$currentUserId-$userId" else "$userId-$currentUserId"
        Firebase.firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ChatVM", "Error listening to messages", error)
                    return@addSnapshotListener
                }

                snapshot?.let { snap ->
                    val msgs = snap.documents.mapNotNull { it.toObject(Message::class.java) }
                    messages.value = msgs
                }
            }
    }

    suspend fun sendImageMessage(imageUri: Uri) {
        // Upload image to Firebase Storage
        val storageRef = storage.reference.child("chat_images/${System.currentTimeMillis()}.jpg")
        val uploadTask = storageRef.putFile(imageUri).await()
        val imageUrl = storageRef.downloadUrl.await().toString()

        // Create a Message object
        val message = Message(
            id = currentUserId,
            toId = userId.removePrefix("{userId}"),
            imageUrl = imageUrl,
            timestamp = Timestamp.now()
        )

        // Generate chat ID
        val chatId = if (currentUserId < userId) "$currentUserId-$userId" else "$userId-$currentUserId"

        // Save the message to Firestore
        Firebase.firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(message)
            .addOnFailureListener { e ->
                Log.e("ChatVM", "Error sending image message", e)
            }
    }

}