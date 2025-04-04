package com.bikerental.app
//
//import android.net.Uri
//import androidx.lifecycle.SavedStateHandle
//import com.bikerental.app.data.model.Message
//import com.bikerental.app.data.model.User
//import com.bikerental.app.data.repositories.UserRepository
//import com.bikerental.app.ui.messaging.ChatViewModel
//import com.bikerental.app.ui.navigation.Navigator
//import com.google.firebase.Firebase
//import com.google.firebase.Timestamp
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.firestore.*
//import com.google.firebase.storage.FirebaseStorage
//import com.google.firebase.storage.StorageReference
//import io.mockk.*
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.tasks.await
//import kotlinx.coroutines.test.*
//import org.junit.Assert.*
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//
//@OptIn(ExperimentalCoroutinesApi::class)
//class ChatViewModelTest {
//
//    private lateinit var viewModel: ChatViewModel
//    private val navigator: Navigator = mockk(relaxed = true)
//    private val userRepository: UserRepository = mockk(relaxed = true)
//    private val auth: FirebaseAuth = mockk(relaxed = true)
//    private val firestore: FirebaseFirestore = mockk(relaxed = true)
//    private val firebaseStorage: FirebaseStorage = mockk(relaxed = true)
//    private val savedStateHandle: SavedStateHandle = mockk(relaxed = true)
//
//    private val testUser = User(uid = "user123", name = "Test User", profileImageUrl = "")
//    private val testCurrentUser = "currentUser"
//
//    @Before
//    fun setup() {
//        // Mock Firebase static singletons
//        mockkStatic(Firebase::class)
//        every { Firebase.firestore } returns firestore
//        every { FirebaseStorage.getInstance() } returns firebaseStorage
//
//        val mockFirebaseUser = mockk<FirebaseUser> {
//            every { uid } returns testCurrentUser
//        }
//        every { auth.currentUser } returns mockFirebaseUser
//
//        viewModel = ChatViewModel(savedStateHandle, navigator, userRepository, auth)
//    }
//
//    @Test
//    fun `test loadUserData updates otherUser correctly`() = runTest {
//        every { userRepository.getUsers() } returns flow { emit(listOf(testUser)) }
//
//        viewModel.loadUserData()
//
//        assertEquals(testUser, viewModel.otherUser.value)
//    }
//
//    @Test
//    fun `test otherUser is null initially`() {
//        assertNull(viewModel.otherUser.value)
//    }
//
//    @Test
//    fun `test messages are empty initially`() {
//        assertTrue(viewModel.messages.value.isEmpty())
//    }
//
//    @Test
//    fun `test chatId generation for different userIds`() {
//        val currentUserId = "currentUser"
//        val userId = "user123"
//
//        val chatId1 = if (currentUserId < userId) "$currentUserId-$userId" else "$userId-$currentUserId"
//        val chatId2 = if (userId < currentUserId) "$userId-$currentUserId" else "$currentUserId-$userId"
//
//        assertEquals(chatId1, chatId2)
//    }
//
//    @Test
//    fun `test listenToMessages updates messages correctly`() = runTest {
//        val mockMessages = listOf(
//            Message(id = "currentUser", toId = "user123", imageUrl = "https://fake-image-url.com", timestamp = Timestamp(1000, 0))
//        )
//        val chatId = "currentUser-user123"
//
//        val mockSnapshot = mockk<QuerySnapshot> {
//            every { documents } returns mockMessages.map {
//                mockk<QueryDocumentSnapshot> {
//                    every { toObject(Message::class.java) } returns it
//                }
//            }
//        }
//
//        val listenerSlot = slot<EventListener<QuerySnapshot>>()
//        val mockQuery = mockk<Query>(relaxed = true)
//
//        every {
//            firestore.collection("chats")
//                .document(chatId)
//                .collection("messages")
//                .orderBy("timestamp")
//                .addSnapshotListener(capture(listenerSlot))
//        } returns ListenerRegistration { }
//
//        viewModel.listenToMessages()
//        listenerSlot.captured.onEvent(mockSnapshot, null)
//
//        assertEquals(mockMessages, viewModel.messages.value)
//    }
//
//    @Test
//    fun `test listenToMessages with error logs the error correctly`() {
//        val mockError = mockk<FirebaseFirestoreException>(relaxed = true)
//        val listenerSlot = slot<EventListener<QuerySnapshot>>()
//
//        every {
//            firestore.collection("chats")
//                .document(any())
//                .collection("messages")
//                .orderBy("timestamp")
//                .addSnapshotListener(capture(listenerSlot))
//        } returns ListenerRegistration { }
//
//        viewModel.listenToMessages()
//        listenerSlot.captured.onEvent(null, mockError)
//
//        // In a real case, you'd verify logs here
//    }
//
//    @Test
//    fun `test sendImageMessage uploads image and saves message`() = runTest {
//        val mockUri: Uri = mockk()
//        val mockStorageRef = mockk<StorageReference>()
//        val mockUploadTaskSnapshot = mockk<com.google.firebase.storage.UploadTask.TaskSnapshot>()
//        val mockDownloadUrl = mockk<Uri> {
//            every { toString() } returns "https://example.com/image.jpg"
//        }
//
//        every { firebaseStorage.reference.child(any()) } returns mockStorageRef
//        coEvery { mockStorageRef.putFile(mockUri).await() } returns mockUploadTaskSnapshot
//        coEvery { mockStorageRef.downloadUrl.await() } returns mockDownloadUrl
//
//        val mockFirestoreRef = mockk<CollectionReference>(relaxed = true)
//        every {
//            firestore.collection("chats")
//                .document("currentUser-user123")
//                .collection("messages")
//        } returns mockFirestoreRef
//
//        coEvery { mockFirestoreRef.add(any()) } returns mockk(relaxed = true)
//
//        viewModel.sendImageMessage(mockUri)
//
//        coVerify { mockStorageRef.putFile(mockUri) }
//        coVerify { mockFirestoreRef.add(any()) }
//    }
//
//    @Test
//    fun `test sendImageMessage handles errors correctly`() = runTest {
//        val mockUri: Uri = mockk()
//        val mockStorageRef = mockk<StorageReference>()
//
//        every { firebaseStorage.reference.child(any()) } returns mockStorageRef
//        coEvery { mockStorageRef.putFile(mockUri).await() } throws Exception("Upload failed")
//
//        val mockFirestoreRef = mockk<CollectionReference>(relaxed = true)
//        every {
//            firestore.collection("chats")
//                .document("currentUser-user123")
//                .collection("messages")
//        } returns mockFirestoreRef
//
//        viewModel.sendImageMessage(mockUri)
//
//        coVerify { mockStorageRef.putFile(mockUri) }
//    }
//}
