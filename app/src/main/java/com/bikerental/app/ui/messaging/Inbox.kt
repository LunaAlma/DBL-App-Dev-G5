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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Inbox(
    modifier: Modifier,
    viewModel: InboxViewModel
) {
    InboxView(
        users = viewModel.users.collectAsState().value,
        isLoading = viewModel.isLoading.collectAsState().value,
        goToChat = { uid -> viewModel.goToChat(uid) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxView(
    users: List<User>,
    isLoading: Boolean,
    goToChat: (String) -> Unit
) {
    Surface(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceDim)){
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Messages") })
            }
        ) { padding ->
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(
                        items = users,
                        key = { it.uid }
                    ) {
                        UserItem(
                            user = it,
                            goToChat = goToChat
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(
    user: User,
    goToChat: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = { goToChat(user.uid) }
            )
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = user.profileImageUrl,
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = user.name, style = MaterialTheme.typography.bodyLarge)
        }
    }
}