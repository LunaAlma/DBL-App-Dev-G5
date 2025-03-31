package com.bikerental.app.ui.create

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter

@Composable
fun AddBike(
    modifier: Modifier,
    viewModel: AddBikeViewModel
) {
    AddBikeView(
        modifier,
        bikeName = viewModel.bikeName.collectAsStateWithLifecycle().value,
        bikeCity = viewModel.city.collectAsStateWithLifecycle().value,
        bikeDescription = viewModel.description.collectAsStateWithLifecycle().value,
        bikeNameError = viewModel.bikeNameError.collectAsStateWithLifecycle().value,
        bikeDescriptionError = viewModel.bikeDescriptionError.collectAsStateWithLifecycle().value,
        bikeCityError = viewModel.bikeCityError.collectAsStateWithLifecycle().value,
        bikeImageUri = viewModel.bikeImageUri.collectAsStateWithLifecycle().value,
        onBikeImageChange = { viewModel.onBikeImageChange(it) },
        onBikeNameChange = { viewModel.onBikeNameChange(it) },
        onBikeDescriptionChange = { viewModel.onBikeDescriptionChange(it) },
        onBikeCityChange = { viewModel.onBikeCityChange(it) },
        firebaseError = viewModel.firebaseError.collectAsStateWithLifecycle().value,
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        addBike = { viewModel.addBike() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBikeView(
    modifier: Modifier = Modifier,
    bikeName: String,
    bikeDescription: String,
    bikeCity: String,
    bikeNameError: String,
    bikeDescriptionError: String,
    bikeCityError: String,
    firebaseError: String,
    isLoading: Boolean,
    bikeImageUri: Uri?,
    onBikeImageChange: (Uri) -> Unit,
    onBikeNameChange: (String) -> Unit = {},
    onBikeDescriptionChange: (String) -> Unit = {},
    onBikeCityChange: (String) -> Unit = {},
    addBike: () -> Unit = {},
    ) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(
                    color = MaterialTheme.colorScheme.onSecondary,
                    shape = RoundedCornerShape(8.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 32.dp,
                    bottom = 4.dp
                )
            ) {
                Text(
                    text = "Add Bike Listing",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Normal,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall // Uses Material Design typography
                )
            }
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 32.dp,
                    bottom = 4.dp
                )
            ) {
                // Add profile image selector
                BikeImageSelector(
                    bikeImageUri = bikeImageUri,
                    onImageSelected = onBikeImageChange,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 32.dp,
                    bottom = 4.dp
                )
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = bikeName,
                    onValueChange = onBikeNameChange,
                    label = { Text("Name") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Filled.PedalBike,
                            contentDescription = "Bike"
                        )
                    },
                    isError = bikeNameError.isNotEmpty(),
                    supportingText = {
                        Text(text = bikeNameError)
                    },
                )
            }
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 4.dp
                )
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = bikeDescription,
                    onValueChange = onBikeDescriptionChange,
                    label = { Text("Description") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Description,
                            contentDescription = "Description"
                        )
                    },
                    isError = bikeDescriptionError.isNotEmpty(),
                    supportingText = {
                        Text(text = bikeDescriptionError)
                    },
                )
            }
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 4.dp
                )
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = bikeCity,
                    onValueChange = onBikeCityChange,
                    label = { Text("City") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = "Location"
                        )
                    },
                    isError = bikeCityError.isNotEmpty(),
                    supportingText = {
                        Text(text = bikeCityError)
                    },
                )
            }
            if (firebaseError.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    )
                ) {
                    Text(
                        text = firebaseError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 48.dp
                )
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = addBike,
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = "Add Listing",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BikeImageSelector(
    bikeImageUri: Uri?,
    onImageSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }
    Box(
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
            .clickable { launcher.launch("image/*") }
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (bikeImageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(bikeImageUri),
                contentDescription = "Bike image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = "Select bike image",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}