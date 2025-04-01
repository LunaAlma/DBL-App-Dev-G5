package com.bikerental.app.ui.map

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.bikerental.app.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.LocationSource
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.bikerental.app.data.model.MarkerData
import com.google.firebase.Timestamp
import com.google.maps.android.compose.MapUiSettings
import com.google.type.Date
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale

// Note that rememberMultiplePermissions is using an experimental API
// Regularly check if it is working (this is easier code than alternative though)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Map(
    modifier: Modifier,
    viewModel: MapViewModel = hiltViewModel()) {

    val bikes by viewModel.bikes.collectAsState()
    val owner by viewModel.ownerDetails.collectAsState()

    fun onBikeSelected(ownerId: String) {
        viewModel.setOwnerId(ownerId)
    }

    // Assume you have a list of bike rentals available (you may collect it from your view model)
    // val bikeRentals by viewModel.bikeRentals.collectAsState(initial = emptyList())

// Filter bikes based on conditions
//    val nowMillis = System.currentTimeMillis()
//    val availableBikes = bikes.filter { bike ->
//        // Only show if the start time is in the future (or hasn't arrived yet)
//        val isNotStarted = bike.startTime?.toDate()?.time?.let { it > nowMillis } ?: false
//        // And the bike has not been rented yet (assuming BikeRental has a bikeId property)
//        val isNotRented = bikeRentals.none { rental -> rental.bikeId == bike.bikeId }
//        isNotStarted && isNotRented
//    }

    val startTime = Timestamp.now()  // Set this to your required start time
    // Get current time in milliseconds
    val currentTimeMillis = System.currentTimeMillis()

// Calculate two weeks from now (14 days * 24 hours * 60 minutes * 60 seconds * 1000 milliseconds)
    val twoWeeksFromNowMillis = currentTimeMillis + (14L * 24 * 60 * 60 * 1000)

// Create a Date object from the future time
    val twoWeeksFromNowDate = java.util.Date(twoWeeksFromNowMillis)

// Convert the Date to a Firebase Timestamp
    val endTime = Timestamp(twoWeeksFromNowDate)


    // Fetch available bikes
    LaunchedEffect(Unit) {
        viewModel.fetchAvailableBikes(startTime, endTime)
    }

    // Collect available bikes
    val availableBikes by viewModel.availableBikes.collectAsState()

    // CHANGE bikes to availableBikes once debugging
    val markersData = availableBikes.map { bike ->
        // bike.location is a GeoPoint from Firebase
        val lat = bike.location.latitude ?: 0.0
        val lng = bike.location.longitude ?: 0.0

        MarkerData(
            location = LatLng(lat, lng),
            rating = owner?.let {
                if (it.numberOfRatings > 0) it.totalRating / it.numberOfRatings else 0
            } ?: 0,
            bikeImgId = bike.imageUrl,  // uses Coil to load this image
            bikePrice = bike.price,
            city = bike.city,
            startTime = bike.startTime,
            endTime = bike.endTime,
            ownerId = bike.ownerId,
            bikeId = bike.bikeId,
            bikeName = bike.bikeName
        )
    }

    var currentLocation by remember { mutableStateOf<Location?>(null) }
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    val mapStyleResId = if (isDarkTheme) R.raw.map_style_night else R.raw.map_style
    // Use Eindhoven coordinates as default
    val defaultLocation = LatLng(51.4416, 5.4697)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }

    val locationsPermissions = rememberMultiplePermissionsState(
        listOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
    )
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(
        LocalContext.current
    )

    // Track which marker is currently selected
    var selectedMarker: MarkerData? by remember { mutableStateOf<MarkerData?>(null) }

    // Location activation code
    val myLocationSource = object : LocationSource {
        var listener: LocationSource.OnLocationChangedListener? = null

        override fun activate(p0: LocationSource.OnLocationChangedListener) {
            this.listener = p0
        }

        override fun deactivate() {
            this.listener = null
        }

        fun onLocation(userLocation: Location) {
            listener?.onLocationChanged(userLocation)
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult : LocationResult) {
            for (location in locationResult.locations) {
//                currentLocation = location
//                myLocationSource.onLocation(location)
//                coroutineScope.launch {
//                    cameraPositionState.animate(
//                        update = CameraUpdateFactory.newLatLngZoom(
//                            LatLng(location.latitude, location.longitude),
//                            cameraPositionState.position.zoom
//                        )
//                    )
//                }
                currentLocation = location
            }
        }
    }

    fun startListeningToLocations() {
        if ( // replaced "this" with "context"
            ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // If permissions are not granted, just return (or request permissions here)
            return
        }

        // Start listening for location updates
        fusedLocationClient.requestLocationUpdates(
            LocationRequest.Builder(1000L).build(),
            locationCallback,
            Looper.getMainLooper()
        )
    }

    LaunchedEffect(locationsPermissions.allPermissionsGranted) {
        if (locationsPermissions.allPermissionsGranted) {
            // Start listening for location updates
            startListeningToLocations()

            // Optionally, get the last known location and center the camera immediately
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(
                                LatLng(it.latitude, it.longitude),
                                cameraPositionState.position.zoom
                            )
                        )
                    }
                }
            }
        } else {
            // If permission is not granted, center on Eindhoven
            coroutineScope.launch {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(
                        LatLng(51.423, 5.46), // Eindhoven coordinates
                        10f // Default zoom level
                    )
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!locationsPermissions.allPermissionsGranted) {
            locationsPermissions.launchMultiplePermissionRequest()
        } else {
            startListeningToLocations()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = {
                    if (locationsPermissions.allPermissionsGranted) {
                        startListeningToLocations()
                        if (currentLocation != null) {
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(
                                        LatLng(currentLocation!!.latitude, currentLocation!!.longitude),
                                        cameraPositionState.position.zoom
                                    )
                                )
                            }
                        } else {
                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                location?.let {
                                    coroutineScope.launch {
                                        cameraPositionState.animate(
                                            update = CameraUpdateFactory.newLatLngZoom(
                                                LatLng(it.latitude, it.longitude),
                                                cameraPositionState.position.zoom
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        locationsPermissions.launchMultiplePermissionRequest()
                    }
                },
                modifier = Modifier
                    .padding(10.dp, 170.dp)
                    .offset(x = 23.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                containerColor = Color.White,
                contentColor = if (locationsPermissions.allPermissionsGranted) Color(0xFF1C73E8) else Color.Gray
            ) {
                Icon(
                    imageVector = Icons.Filled.Star, // Replace with your desired icon if needed
                    contentDescription = "Live Location",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) {
        // Gives error when empty
        val mapProperties = MapProperties(
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, mapStyleResId),
            maxZoomPreference = 18f,
            minZoomPreference = 3f,
//         Restrict map bounds to Europe?
//            latLngBoundsForCameraTarget = LatLngBounds(
//                LatLng(55.0,-3.0),
//                LatLng(57.0,-6.0),
//            ),
            isMyLocationEnabled = locationsPermissions.allPermissionsGranted
            // Map Type (without additional customization)
//        mapType = MapType.NORMAL
        )
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            // Customizable map properties
            properties = mapProperties,
//            locationSource = myLocationSource,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = false,
                compassEnabled = false,
            ),
            contentPadding = PaddingValues(bottom = 70.dp) // Adds padding to zoom in/out functionality
        ) {
            for (data in markersData) {
                Log.d("MarkersData", "Data: $data")
                val markerState = remember { MarkerState(position = data.location) }
                Marker(
                    state = markerState,
//                    title = data.ownerName,
//                    snippet = data.rating,
                    onClick = {
                        // Update the selected marker
                        onBikeSelected(data.ownerId)
                        selectedMarker = data
                        // Return true to consume click
                        true
                    }
                )
            }
        }

        // Show the bottom card if a marker is selected
        selectedMarker?.let { marker ->
            // Observe the owner details for the selected marker
            val ownerDetails by viewModel.ownerDetails.collectAsState()
            // Use ownerDetails to compute rating calc.
            val rating = ownerDetails?.let {
                if (it.numberOfRatings > 0) it.totalRating / it.numberOfRatings else 0
            } ?: 0

            BottomCard(
                markerData = marker.copy(rating = rating), // markerData = marker,
                onDismiss = { selectedMarker = null }
            )
        }
    }

}

/**
 * Bottom card pop up when pin clicked
 */
@Composable
fun BottomCard(
    markerData: MarkerData,
    onDismiss: () -> Unit
) {
    // Box that takes the entire screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            // If user taps outside the card, we dismiss it
            .clickable(
                onClick = { onDismiss() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        // The pop-up at the bottom
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 300.dp)
                // TODO: Clicking the card takes you to individual bike page
                .clickable(
                    onClick = { /*  TODO: ADD LOGIC HERE (LUKA pg) */ },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
                .padding(bottom = 10.dp, start = 10.dp, end = 10.dp ), // prev. 16
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            )
        ) {
            // Use a Row to arrange the image on the left and text on the right
            Box { // Added this outer box to add X button properties to close out
                Row(modifier = Modifier.padding(16.dp)) {
                    // Image on the left
                    Image(
                        painter = rememberAsyncImagePainter(model = markerData.bikeImgId),
                        contentDescription = "Bike image",
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.CenterVertically)
                    )
                    // Space between image and text
                    Spacer(modifier = Modifier.width(16.dp))
                    // Column for text on the right of image
                    Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                        Text(
                            text = markerData.bikeName.take(20) + if (markerData.bikeName.length > 20) "..." else "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.Black
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Star",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(16.dp).background(Color.Transparent)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${markerData.rating}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Text(text = "${markerData.bikePrice}€")
                        val dateFormatter = SimpleDateFormat("dd-MM HH:mm", Locale.getDefault())

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = "Calendar",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(16.dp).background(Color.Transparent)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${
                                    markerData.startTime?.toDate()
                                        ?.let { dateFormatter.format(it) } ?: "Unknown"
                                } to ${
                                    markerData.endTime?.toDate()
                                        ?.let { dateFormatter.format(it) } ?: "Unknown"
                                }"
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp) // Adjust padding if awkward
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Color.Black
                    )
                }
            } // Closure of box added to close out when clicking X button
        }
    }
}
