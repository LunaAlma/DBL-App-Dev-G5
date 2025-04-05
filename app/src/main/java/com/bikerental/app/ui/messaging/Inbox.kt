package com.bikerental.app.ui.messaging

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bikerental.app.data.model.User

/**
 * Composable function that represents the Inbox screen of the app.
 * It observes the users list and loading state from the ViewModel.
 *
 * @param modifier Modifier to customize the UI's layout.
 * @param viewModel ViewModel that provides the users data and loading state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Inbox(
    modifier: Modifier,
    viewModel: InboxViewModel
) {
    // Passes the observed users data and loading state to the InboxView composable.
    InboxView(
        users = viewModel.users.collectAsState().value,
        isLoading = viewModel.isLoading.collectAsState().value,
        goToChat = { uid -> viewModel.goToChat(uid) } // Navigate to the chat screen for a specific user.
    )
}

/**
 * Composable function that displays the list of users and handles loading state.
 * It shows a list of users in a LazyColumn, or a loading indicator if data is loading.
 *
 * @param users List of users to display.
 * @param isLoading Boolean flag indicating if the data is being loaded.
 * @param goToChat Function to navigate to the chat screen for the selected user.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxView(
    users: List<User>,
    isLoading: Boolean,
    goToChat: (String) -> Unit
) {
    // Outer surface to give a background to the entire view.
    Surface(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceDim)) {
        Scaffold(
            topBar = {
                // Top app bar displaying the title "Messages".
                TopAppBar(title = { Text("Messages") })
            }
        ) { padding ->
            // Show a loading spinner while data is being fetched.
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            } else {
                // LazyColumn to efficiently display the list of users.
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Iterate over the list of users and display each one in a UserItem.
                    items(
                        items = users,
                        key = { it.uid }
                    ) { user ->
                        // UserItem composable to represent a single user.
                        UserItem(
                            user = user,
                            goToChat = goToChat // Pass the function to navigate to the chat screen.
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composable function that represents a single user in the Inbox.
 * It shows the user's profile image and name, and handles clicks to go to chat.
 *
 * @param user The user to display.
 * @param goToChat Function to navigate to the chat screen for the selected user.
 */
@Composable
fun UserItem(
    user: User,
    goToChat: (String) -> Unit
) {
    // Card to display the user item, with clickable behavior.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = { goToChat(user.uid) } // Trigger navigation when the user item is clicked.
            )
            .padding(8.dp) // Add padding around the card.
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, // Align the elements vertically centered.
            modifier = Modifier.padding(16.dp) // Add padding inside the Row.
        ) {
            // Display the user's profile image inside a circular shape.
            AsyncImage(
                model = user.profileImageUrl,
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(48.dp) // Set the size of the profile image.
                    .clip(CircleShape) // Make the image circular.
            )
            Spacer(modifier = Modifier.width(16.dp)) // Space between the image and the user's name.
            // Display the user's name.
            Text(text = user.name, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
