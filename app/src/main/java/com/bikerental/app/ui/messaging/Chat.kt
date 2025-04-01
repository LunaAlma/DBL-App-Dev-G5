package com.bikerental.app.ui.messaging

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat(
    modifier: Modifier,
    viewModel: ChatViewModel
) {

    ChatView(
        userId = viewModel.userId,
    )

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatView(
    userId: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(userId) },
            )
        }
    ) {

    }
}