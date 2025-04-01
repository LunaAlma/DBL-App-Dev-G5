package com.bikerental.app.ui.create

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import java.security.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AddBike(
    modifier: Modifier,
    viewModel: AddBikeViewModel
) {
    AddBikeView(
        modifier,
        bikeName = viewModel.bikeName.collectAsStateWithLifecycle().value,
        bikeCity = viewModel.city.collectAsStateWithLifecycle().value,
        bikePrice = viewModel.bikePrice.collectAsStateWithLifecycle().value,
        bikeImageUri = viewModel.bikeImageUri.collectAsStateWithLifecycle().value,
        selectedStartDate = viewModel.selectedStartDate.collectAsStateWithLifecycle().value,
        selectedEndDate = viewModel.selectedEndDate.collectAsStateWithLifecycle().value,
        bikeNameError = viewModel.bikeNameError.collectAsStateWithLifecycle().value,
        bikePriceError = viewModel.bikePriceError.collectAsStateWithLifecycle().value,
        bikeCityError = viewModel.bikeCityError.collectAsStateWithLifecycle().value,
        bikeImageError = viewModel.bikeImageError.collectAsStateWithLifecycle().value,
        selectedStartDateError = viewModel.selectedStartDateError.collectAsStateWithLifecycle().value,
        onBikeImageChange = { viewModel.onBikeImageChange(it) },
        onBikeNameChange = { viewModel.onBikeNameChange(it) },
        onBikePriceChange = { viewModel.onBikePriceChange(it) },
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
    bikeCity: String,
    bikePrice: String,
    selectedStartDate: Timestamp?,
    selectedEndDate: Timestamp?,
    bikeNameError: String,
    bikeCityError: String,
    bikePriceError: String,
    bikeImageError: String,
    selectedStartDateError: String,
    firebaseError: String,
    isLoading: Boolean,
    bikeImageUri: Uri?,
    onBikeImageChange: (Uri) -> Unit,
    onBikeNameChange: (String) -> Unit = {},
    onBikeCityChange: (String) -> Unit = {},
    onBikePriceChange: (String) -> Unit = {},
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
                    modifier = Modifier.padding(16.dp),
                )
            }
            if (bikeImageError.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    )
                ) {
                    Text(
                        text = bikeImageError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
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
                    value = bikePrice,
                    onValueChange = onBikePriceChange,
                    label = { Text("Price") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Euro,
                            contentDescription = "Price"
                        )
                    },
                    isError = bikePriceError.isNotEmpty(),
                    supportingText = {
                        Text(text = bikePriceError)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    )
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
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    val cities = listOf("Eindhoven", "Amsterdam", "Utrecht", "Den Haag", "Rotterdam")
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        modifier = Modifier.fillMaxWidth(),
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            value = bikeCity,
                            onValueChange = {},
                            label = { Text("City") },
                            singleLine = true,
                            readOnly = true,  // Disables keyboard input
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.LocationOn,
                                    contentDescription = "Location"
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                    contentDescription = if (expanded) "Collapse" else "Expand"
                                )
                            },
                            isError = bikeCityError.isNotEmpty(),
                            supportingText = {
                                Text(text = bikeCityError)
                            },
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            cities.forEach { city ->
                                DropdownMenuItem(
                                    text = { Text(city) },
                                    onClick = {
                                        onBikeCityChange(city)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 4.dp
                )
            ) {
// Date picker field
//                DatePickerField(
//                    label = "Start Date",
//                    selectedDate = selectedStartDate,
//                    onDateSelected = { date ->
//                        selectedStartDate = date
//                        selectedStartDateError = ""
//                    },
//                )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    val showDatePicker = remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }

    if (showDatePicker.value) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val date = Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateSelected(date)
                        }
                        showDatePicker.value = false
                    }
                ) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    OutlinedTextField(
        value = selectedDate?.format(dateFormatter) ?: "",
        onValueChange = {},
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Select date"
            )
        },
        isError = isError,
        supportingText = {
            if (isError) {
                Text(text = errorMessage)
            }
        },
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            showDatePicker.value = true
                        }
                    }
                }
            }
    )
}