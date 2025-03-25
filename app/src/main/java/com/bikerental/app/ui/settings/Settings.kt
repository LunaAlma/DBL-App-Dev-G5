package com.bikerental.app.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import com.bikerental.app.ui.welcome.WelcomeViewModel

@Composable
fun Settings(modifier: Modifier,
             viewModel: WelcomeViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFB0DCA4) // Your green color
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Picture
//                Image(
//                    painter = rememberAsyncImagePainter(),
//                    contentDescription = "Profile Picture",
//                    modifier = Modifier
//                        .size(80.dp)
//                        .clip(CircleShape),
//                    contentScale = ContentScale.Crop
//                )

                Spacer(modifier = Modifier.width(16.dp))

                // User Info
                Column {
                    Text(
                        text = "test",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "test",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Settings Items
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                SettingsItem("Past Rentals")

                SettingsItem("Payments & Invoices")

                SettingsItem("Account Details")

                SettingsItem("App Settings")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Log Out Button
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB0DCA4),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Log Out", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Delete Account Button
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Delete Account", fontSize = 16.sp)
        }
    }
}

@Composable
fun SettingsItem(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable { /* TODO: Navigate */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            modifier = Modifier.padding(end = 16.dp)
        )
    }
}

// Preview
@Preview(name = "Settings Screen", showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    MaterialTheme {
        Settings(
            modifier = TODO(),
            viewModel = TODO()
        )
    }
}