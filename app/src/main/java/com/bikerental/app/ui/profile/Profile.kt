package com.bikerental.app.ui.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.ui.layout.ContentScale
import com.bikerental.app.data.model.User

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel
) {
    ProfileView(
        modifier = modifier,
        user = viewModel.user.collectAsState().value,
        navigateToMyBikes = { viewModel.navigateToMyBikes() },
        navigateToPastRentals = { viewModel.navigateToPastRentals() },
        navigateToDetails = { viewModel.navigateToDetails() }
    )
}

@Composable
private fun ProfileView(
    modifier: Modifier,
    user: User?,
    navigateToDetails: () -> Unit,
    navigateToMyBikes: () -> Unit,
    navigateToPastRentals: () -> Unit
) {
//    val userState by viewModel.user.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ProfileCard(navigateToDetails, user)

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(
                navigateToMyBikes = navigateToMyBikes,
                navigateToPastRentals = navigateToPastRentals
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileCard(
    navigateToDetails: () -> Unit = {},
    user: User?
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { navigateToDetails() },
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image column
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = user?.profileImageUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )
            }

            // User info column
            Column(
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    text = user?.name ?: "err",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = user?.email ?: "err",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    navigateToMyBikes: () -> Unit = {},
    navigateToPastRentals: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column {
            ListItem(
                headlineContent = { Text("My Bikes") },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { navigateToMyBikes() }
            )

            ListItem(
                headlineContent = { Text("Past Rentals") },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { /* Handle click */ }
            )
        }
    }
}

@Composable
private fun ActionButtons(onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Log Out")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { /* Handle account deletion */ },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text("Delete Account")
        }
    }
}