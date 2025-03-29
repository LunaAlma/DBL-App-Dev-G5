package com.bikerental.app.ui.create

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.bikerental.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBike(
    modifier: Modifier = Modifier,
    viewModel: AddBikeViewModel = hiltViewModel(),
    onPublishClick: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedImageUri by viewModel.selectedImageUri.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setSelectedImage(it) }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AddBikeUiState.Success -> {
                // Show success message
                snackbarHostState.showSnackbar(
                    message = "Bike rental added successfully!",
                    duration = SnackbarDuration.Short
                )
                // Reset form
                viewModel.resetState()
            }
            is AddBikeUiState.Error -> {
                // Show error message
                snackbarHostState.showSnackbar(
                    message = (uiState as AddBikeUiState.Error).message,
                    duration = SnackbarDuration.Short
                )
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Add Bike") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFB2E59C)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image selection box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "Selected bike image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(text = "Upload or take pic", color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Text field for Bike Type.
            var bikeType by remember { mutableStateOf(TextFieldValue("")) }
            OutlinedTextField(
                value = bikeType,
                onValueChange = { bikeType = it },
                label = { Text("Bike type") },
                placeholder = { Text("e.g., Road Bike") },
                modifier = Modifier.fillMaxWidth(),
                isError = bikeType.text.isBlank() && uiState is AddBikeUiState.Error
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Text field for City
            var city by remember { mutableStateOf(TextFieldValue("")) }
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                placeholder = { Text("e.g., Istanbul") },
                modifier = Modifier.fillMaxWidth(),
                isError = city.text.isBlank() && uiState is AddBikeUiState.Error
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Text field for Rental Start Date
            var rentalStartDate by remember { mutableStateOf(TextFieldValue("")) }
            OutlinedTextField(
                value = rentalStartDate,
                onValueChange = { rentalStartDate = it },
                label = { Text("Rental Start Date") },
                placeholder = { Text("e.g., 2024-03-20") },
                modifier = Modifier.fillMaxWidth(),
                isError = rentalStartDate.text.isBlank() && uiState is AddBikeUiState.Error
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Text field for Rental End Date
            var rentalEndDate by remember { mutableStateOf(TextFieldValue("")) }
            OutlinedTextField(
                value = rentalEndDate,
                onValueChange = { rentalEndDate = it },
                label = { Text("Rental End Date") },
                placeholder = { Text("e.g., 2024-03-25") },
                modifier = Modifier.fillMaxWidth(),
                isError = rentalEndDate.text.isBlank() && uiState is AddBikeUiState.Error
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Text field for Bike Description.
            var bikeDescription by remember { mutableStateOf(TextFieldValue("")) }
            OutlinedTextField(
                value = bikeDescription,
                onValueChange = { bikeDescription = it },
                label = { Text("Description...") },
                placeholder = { Text("e.g., Comfortable, well-maintained bike") },
                modifier = Modifier.fillMaxWidth(),
                isError = bikeDescription.text.isBlank() && uiState is AddBikeUiState.Error
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.addBikeRental(
                        bikeType = bikeType.text,
                        description = bikeDescription.text,
                        city = city.text,
                        rentalStartDate = rentalStartDate.text,
                        rentalEndDate = rentalEndDate.text
                    )
                    onPublishClick(bikeType.text, bikeDescription.text, city.text, rentalStartDate.text, rentalEndDate.text)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB2E59C)
                ),
                enabled = uiState !is AddBikeUiState.Loading
            ) {
                if (uiState is AddBikeUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Publish bike rental")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddBikeScreenPreview() {
    PreviewAddBike()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewAddBike() {
    // Dummy data for preview
    var bikeType by remember { mutableStateOf(TextFieldValue("Mountain Bike")) }
    var bikeDescription by remember { mutableStateOf(TextFieldValue("Great for trails!")) }
    var city by remember { mutableStateOf(TextFieldValue("Istanbul")) }
    var rentalStartDate by remember { mutableStateOf(TextFieldValue("2024-03-20")) }
    var rentalEndDate by remember { mutableStateOf(TextFieldValue("2024-03-25")) }

    // Dummy onPublishClick for preview
    val dummyOnPublishClick: (String, String, String, String, String) -> Unit = { type, description, city, startDate, endDate ->
        println("Publish clicked with type: $type, description: $description, city: $city, start date: $startDate, end date: $endDate")
    }

    // Call AddBike with dummy data and callback
    AddBike(onPublishClick = dummyOnPublishClick)
}
