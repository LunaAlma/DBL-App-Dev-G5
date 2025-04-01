package com.bikerental.app.ui.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.bikerental.app.data.model.User
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UserProfileDetails(
    modifier: Modifier,
    viewModel: ProfileViewModel
) {
    UserProfileDetailsView(
        modifier = modifier,
        user = viewModel.user.collectAsState().value,
        onLogout = { viewModel.onLogout() },
        onDeleteAccount = { viewModel.onDeleteAccount() },
        onProfileImageChange = { uri -> viewModel.onProfileImageChange(uri) },
        onNameChanged = { newName -> viewModel.onNameChanged(newName) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileDetailsView(
    modifier: Modifier,
    user: User?,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    onProfileImageChange: (Uri) -> Unit,
    onNameChanged: (String) -> Unit
) {
    var isEditingName by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(user?.name ?: "") }
    var nameError by remember { mutableStateOf("") }

    // Name validation function
    fun validateName(): Boolean {
        return when {
            newName.isEmpty() -> {
                nameError = "Name cannot be empty"
                false
            }
            newName.length < 3 -> {
                nameError = "Name must be at least 3 characters"
                false
            }
            else -> {
                nameError = ""
                true
            }
        }
    }

    val isNameValid = nameError.isEmpty() && newName.isNotEmpty() && newName.length >= 3

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile Details") }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileImageSelector(
                    user = user,
                    onImageSelected = onProfileImageChange,
                    modifier = Modifier.size(260.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Editable Name Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isEditingName) {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = {
                                newName = it
                                validateName()
                             },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            isError = nameError.isNotEmpty(),
                            supportingText = {
                                if (nameError.isNotEmpty()) {
                                    Text(
                                        text = nameError,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )
                    } else {
                        Text(
                            text = user?.name ?: "err",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    IconButton(
                        onClick = {
                            if (isEditingName) {
                                if (validateName()) {
                                    // Here you would call your update function
                                    onNameChanged(newName)
                                    isEditingName = false
                                }
                            } else {
                                isEditingName = true
                            }
                        },
                        // Disable the button if validation fails and user is in edit mode
                        enabled = !isEditingName || isNameValid
                    ) {
                        Icon(
                            imageVector = if (isEditingName) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditingName) "Save Name" else "Edit Name",
                            // Apply alpha to visually indicate disabled state
                            tint = if (isEditingName && !isNameValid)
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // User Details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ListItem(
                        headlineContent = { Text("Email") },
                        supportingContent = { Text(user?.email ?: "error") }
                    )

                    ListItem(
                        headlineContent = { Text("User ID") },
                        supportingContent = { Text(user?.uid ?: "error") }
                    )

                    val averageRating = user?.numberOfRatings?.let {
                        if (it > 0) {
                            user.totalRating.toFloat() / user.numberOfRatings
                        } else 0f
                    }

                    ListItem(
                        headlineContent = { Text("Ratings") },
                        supportingContent = {
                            user?.numberOfRatings?.let {
                                Text(
                                    text = if (it > 0) {
                                        "%.1f average from %d ratings".format(averageRating, user.numberOfRatings)
                                    } else {
                                        "No ratings yet"
                                    }
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Action Buttons
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Log Out")
                    }

                    Button(
                        onClick = onDeleteAccount,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text("Delete Account")
                    }
                }
            }
        }
    )
}

@Composable
private fun ProfileImageSelector(
    user: User?,
    onImageSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val file = remember { createImageFile(context) }
    val uri = remember { FileProvider.getUriForFile(context, "${context.packageName}.provider", file) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            onImageSelected(uri)
        }
    }

    val cameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Clickable Profile Image
    IconButton(
        onClick = { },
        modifier = Modifier.size(260.dp)
    ) {
        AsyncImage(
            model = user?.profileImageUrl,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    val permissionCheckResult = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    )
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(uri)
                    } else {
                        cameraPermission.launch(Manifest.permission.CAMERA)
                    }
                }
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                ),
            contentScale = ContentScale.Crop
        )
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