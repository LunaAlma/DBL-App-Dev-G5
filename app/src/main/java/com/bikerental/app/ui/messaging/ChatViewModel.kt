package com.bikerental.app.ui.messaging

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.bikerental.app.data.model.Message
import com.bikerental.app.data.model.User
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.ui.base.BaseViewModel
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

/**
 * ViewModel class that handles the business logic for the chat interface.
 * It manages fetching user data, listening to real-time messages, and sending messages (text and images).
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    navigator: Navigator,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : BaseViewModel(navigator) {

    /**
     * The ID of the user to chat with, fetched from SavedStateHandle.
     */
    val userId: String = savedStateHandle.get<String>("userId") ?: ""

    /**
     * The ID of the currently authenticated user.
     */
    val currentUserId = auth.currentUser?.uid ?: ""

    /**
     * Mutable state to store the other user's data.
     */
    val otherUser = MutableStateFlow<User?>(null)

    /**
     * Mutable state to store the list of messages.
     */
    val messages = MutableStateFlow<List<Message>>(emptyList())

    /**
     * Firebase Storage instance used to upload images.
     */
    private val storage = FirebaseStorage.getInstance()

    /**
     * Initializes the ViewModel by loading user data and starting to listen for new messages.
     */
    init {
        loadUserData()
        // Optionally, subscribe to messages in real time
        listenToMessages()
    }

    /**
     * Loads data for the other user from the user repository.
     */
    fun loadUserData() {
        launchFirebase {
            userRepository.getUsers().collect { users ->
                otherUser.value = users.firstOrNull { it.uid == userId }
            }
        }
    }

    /**
     * Listens to real-time updates of messages between the current user and the other user.
     * This creates a chat ID by comparing user IDs to ensure the chat is bi-directional.
     */
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

    /**
     * Sends an image message by uploading the image to Firebase Storage and saving the message to Firestore.
     *
     * @param imageUri The URI of the image to be sent.
     */
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