package com.bikerental.app.ui.bike

import com.bikerental.app.R
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bikerental.app.ui.theme.AppTheme
import com.bikerental.app.ui.bike.BikeDetailsViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun BikeDetails(bikeId: String) {
    val bikeViewModel: BikeDetailsViewModel = viewModel()
    val bikeData = bikeViewModel.bike.value

    Log.d("BikeDetails", "bikeId: $bikeId")
    LaunchedEffect(bikeId) {
        bikeViewModel.getBikeDetails(bikeId)
    }

    // Handle image loading using imageUrl instead of imageRef
    var imageUrl by remember { mutableStateOf(bikeData?.imageUrl ?: "") }
    var imageLoaded by remember { mutableStateOf(false) }
    if (bikeData?.imageUrl?.isNotEmpty() == true && !imageLoaded) {
        val storageRef = Firebase.storage.reference.child(bikeData.imageUrl)
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            imageUrl = uri.toString()
            imageLoaded = true
        }.addOnFailureListener { exception ->
            Log.e("BikeRentalCard", "Failed to load image: ${exception.message}")
        }
    }

    // Safely derive painter in composable scope
    val painter: Painter = if (imageUrl.isNotEmpty()) {
        rememberAsyncImagePainter(model = imageUrl)
    } else {
        painterResource(id = R.drawable.bike)
    }

    // Format start and end time using SimpleDateFormat
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val startTimeText = bikeData?.startTime?.toDate()?.let { sdf.format(it) } ?: "Unknown"
    val endTimeText = bikeData?.endTime?.toDate()?.let { sdf.format(it) } ?: "Unknown"

    Surface(modifier = Modifier.fillMaxSize()) { }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painter,
                contentDescription = "Bike Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = Color(0xFFFFC107)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "5.0", style = MaterialTheme.typography.bodyMedium)
                }
                Text(text = "${bikeData?.price}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "$startTimeText - $endTimeText", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = bikeData?.bikeName ?: "No name found",
                style = MaterialTheme.typography.bodyMedium
            )
            // Replace ownerName with ownerId
            Text(
                text = bikeData?.ownerId ?: "no owner",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { /* TODO: Handle rent action */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB2E59C))
            ) {
                Text("Rent")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BikeRentalCardPreview() {
    AppTheme {
        BikeDetails("bike_1")
    }
}
