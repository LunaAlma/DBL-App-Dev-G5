package com.bikerental.app.ui.create

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.bikerental.app.R

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
    onBikeNameChange: (String) -> Unit = {},
    onBikeDescriptionChange: (String) -> Unit = {},
    onBikeCityChange: (String) -> Unit = {},
    addBike: () -> Unit = {},
    ) {

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(.9f)
                .fillMaxHeight(.9f)
                .defaultMinSize()
                .align(Alignment.Center)
                .background(
                    color = MaterialTheme.colorScheme.onSecondary,
                    shape = RoundedCornerShape(8.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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

//    val selectedImageUri by viewModel.selectedImageUri.collectAsStateWithLifecycle()
//    val context = LocalContext.current
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    val imagePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let { viewModel.setSelectedImage(it) }
//    }

//    LaunchedEffect(uiState) {
//        when (uiState) {
//            is AddBikeUiState.Success -> {
//                // Show success message
//                snackbarHostState.showSnackbar(
//                    message = "Bike rental added successfully!",
//                    duration = SnackbarDuration.Short
//                )
//                // Reset form
//                viewModel.resetState()
//            }
//            is AddBikeUiState.Error -> {
//                // Show error message
//                snackbarHostState.showSnackbar(
//                    message = (uiState as AddBikeUiState.Error).message,
//                    duration = SnackbarDuration.Short
//                )
//            }
//            else -> {}
//        }
//    }

//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = { Text(text = "Add Bike") },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color(0xFFB2E59C)
//                )
//            )
//        },
//        snackbarHost = { SnackbarHost(snackbarHostState) }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .padding(horizontal = 24.dp, vertical = 16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Image selection box
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(150.dp)
//                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
//                    .clickable { imagePickerLauncher.launch("image/*") },
//                contentAlignment = Alignment.Center
//            ) {
//                if (selectedImageUri != null) {
//                    Image(
//                        painter = rememberAsyncImagePainter(selectedImageUri),
//                        contentDescription = "Selected bike image",
//                        modifier = Modifier.fillMaxSize(),
//                        contentScale = ContentScale.Crop
//                    )
//                } else {
//                    Text(text = "Upload or take pic", color = Color.Gray)
//                }
//            }
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Text field for Bike Type.
//            var bikeType by remember { mutableStateOf(TextFieldValue("")) }
//            OutlinedTextField(
//                value = bikeType,
//                onValueChange = { bikeType = it },
//                label = { Text("Bike type") },
//                placeholder = { Text("e.g., Road Bike") },
//                modifier = Modifier.fillMaxWidth(),
//                isError = bikeType.text.isBlank() && uiState is AddBikeUiState.Error
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Text field for City
//            var city by remember { mutableStateOf(TextFieldValue("")) }
//            OutlinedTextField(
//                value = city,
//                onValueChange = { city = it },
//                label = { Text("City") },
//                placeholder = { Text("e.g., Istanbul") },
//                modifier = Modifier.fillMaxWidth(),
//                isError = city.text.isBlank() && uiState is AddBikeUiState.Error
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Text field for Rental Start Date
//            var rentalStartDate by remember { mutableStateOf(TextFieldValue("")) }
//            OutlinedTextField(
//                value = rentalStartDate,
//                onValueChange = { rentalStartDate = it },
//                label = { Text("Rental Start Date") },
//                placeholder = { Text("e.g., 2024-03-20") },
//                modifier = Modifier.fillMaxWidth(),
//                isError = rentalStartDate.text.isBlank() && uiState is AddBikeUiState.Error
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Text field for Rental End Date
//            var rentalEndDate by remember { mutableStateOf(TextFieldValue("")) }
//            OutlinedTextField(
//                value = rentalEndDate,
//                onValueChange = { rentalEndDate = it },
//                label = { Text("Rental End Date") },
//                placeholder = { Text("e.g., 2024-03-25") },
//                modifier = Modifier.fillMaxWidth(),
//                isError = rentalEndDate.text.isBlank() && uiState is AddBikeUiState.Error
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Text field for Bike Description.
//            var bikeDescription by remember { mutableStateOf(TextFieldValue("")) }
//            OutlinedTextField(
//                value = bikeDescription,
//                onValueChange = { bikeDescription = it },
//                label = { Text("Description...") },
//                placeholder = { Text("e.g., Comfortable, well-maintained bike") },
//                modifier = Modifier.fillMaxWidth(),
//                isError = bikeDescription.text.isBlank() && uiState is AddBikeUiState.Error
//            )
//            Spacer(modifier = Modifier.height(32.dp))
//
//            Button(
//                onClick = {
//                    viewModel.addBikeRental(
//                        bikeType = bikeType.text,
//                        description = bikeDescription.text,
//                        city = city.text,
//                        rentalStartDate = rentalStartDate.text,
//                        rentalEndDate = rentalEndDate.text
//                    )
//                    onPublishClick(bikeType.text, bikeDescription.text, city.text, rentalStartDate.text, rentalEndDate.text)
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFFB2E59C)
//                ),
//                enabled = uiState !is AddBikeUiState.Loading
//            ) {
//                if (uiState is AddBikeUiState.Loading) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(24.dp),
//                        color = Color.White
//                    )
//                } else {
//                    Text("Publish bike rental")
//                }
//            }
//        }
//    }
}