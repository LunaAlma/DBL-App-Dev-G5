package com.bikerental.app.ui.create

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import java.io.File
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


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
        firebaseError = viewModel.firebaseError.collectAsStateWithLifecycle().value,
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        addBike = { viewModel.addBike() },
        onBikeImageChange = { viewModel.onBikeImageChange(it) },
        onBikeNameChange = { viewModel.onBikeNameChange(it) },
        onBikePriceChange = { viewModel.onBikePriceChange(it) },
        onBikeCityChange = { viewModel.onBikeCityChange(it) },
        selectedEndDateError = viewModel.selectedEndDateError.collectAsStateWithLifecycle().value,
        onStartDateChange = { viewModel.onStartDateSelected(it) },
        onEndDateChange = { viewModel.onEndDateSelected(it) }

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBikeView(
    modifier: Modifier = Modifier,
    bikeName: String,
    bikeCity: String,
    bikePrice: String,
    selectedStartDate: Timestamp?, // or whatever type you are using
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
    selectedEndDateError: String,
    onStartDateChange: (LocalDate) -> Unit,
    onEndDateChange: (LocalDate) -> Unit,
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
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
                        .padding(vertical = 4.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            // Bike Image Selector
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 32.dp,
                    bottom = 4.dp
                )
            ) {
                BikeImageSelector(
                    bikeImageUri = bikeImageUri,
                    onImageSelected = onBikeImageChange,
                    modifier = Modifier.padding(4.dp),
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

            // Bike Name
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

            // Bike Price
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

            // Bike City
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

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        value = bikeCity,
                        onValueChange = {},
                        label = { Text("City") },
                        singleLine = true,
                        readOnly = true,
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

            Row(
                modifier = Modifier.padding(8.dp, 4.dp)
            ) {
                DatePickerField(
                    label = "Available From",
                    selectedDate = selectedStartDate?.toDate()?.toInstant()
                        ?.atZone(ZoneId.systemDefault())?.toLocalDate(),
                    onDateSelected = onStartDateChange,
                    isError = selectedStartDateError.isNotEmpty(),
                    errorMessage = selectedStartDateError
                )
            }

            // End Date
            Row(
                modifier = Modifier.padding(8.dp, 4.dp)
            ) {
                DatePickerField(
                    label = "Available Until",
                    selectedDate = selectedEndDate?.toDate()?.toInstant()
                        ?.atZone(ZoneId.systemDefault())?.toLocalDate(),
                    onDateSelected = onEndDateChange,
                    isError = selectedEndDateError.isNotEmpty(),
                    errorMessage = selectedEndDateError
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

            // Add Bike Button
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
    val context = LocalContext.current

    // State to store camera photo URI
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher for picking an image from gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    // Launcher for taking a photo with the camera
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraUri?.let { onImageSelected(it) }
        } else {
            Toast.makeText(context, "Camera capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    // Request camera permission
    val cameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraUri = createImageFile(context).toUri(context)
            cameraUri?.let { cameraLauncher.launch(it) }
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = modifier
            .size(150.dp)
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
            .clip(CircleShape)
            .clickable {
                // You can show a dialog or menu here to let the user choose
                // between picking from the gallery or taking a photo with the camera.
                // For now, let's just open the gallery.
                galleryLauncher.launch("image/*")
            }
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

private fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
}

/**
 * Helper extension to get a content:// Uri from a File using FileProvider.
 * Make sure you define a <provider> in your AndroidManifest with the same authority
 * you use here, e.g., "com.bikerental.app.fileprovider".
 */
private fun File.toUri(context: Context): Uri {
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, this)
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
            initialSelectedDateMillis = selectedDate
                ?.atStartOfDay(ZoneId.systemDefault())
                ?.toInstant()
                ?.toEpochMilli()
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
    fun Timestamp.toDate(): Date = this.toDate()

    fun Date.toInstant(): Instant = this.toInstant()
}
