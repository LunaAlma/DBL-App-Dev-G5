package com.bikerental.app.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBikeScreen(
    onPublishClick: (String, String) -> Unit = { _, _ -> }
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Add Bike") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFB2E59C)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Placeholder for bike image upload or photo capture.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .clickable {
                        // TODO: Insert image selection or capture logic here.
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Upload or take pic", color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Text field for Bike Type.
            var bikeType by remember { mutableStateOf(TextFieldValue("")) }
            OutlinedTextField(
                value = bikeType,
                onValueChange = { bikeType = it },
                label = { Text("Bike type") },
                placeholder = { Text("e.g., Road Bike") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Text field for Bike Description.
            var bikeDescription by remember { mutableStateOf(TextFieldValue("")) }
            OutlinedTextField(
                value = bikeDescription,
                onValueChange = { bikeDescription = it },
                label = { Text("Description...") },
                placeholder = { Text("e.g., Comfortable, well-maintained bike") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onPublishClick(bikeType.text, bikeDescription.text) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB2E59C)
                )
            ) {
                Text("Publish bike rental")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddBikeScreenPreview() {
    AddBikeScreen()
}
