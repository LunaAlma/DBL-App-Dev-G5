package com.bikerental.app.ui.map

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
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
import com.google.firebase.Timestamp
import com.google.maps.android.compose.MapUiSettings

/**
 * Composable function for adding a bike on the map.
 * It sets up the necessary state, handles permissions, and interacts with the viewModel.
 *
 * @param modifier Modifier for layout styling.
 * @param viewModel The ViewModel containing bike and owner data.
 */
@Composable
fun MapAddBike(
    modifier: Modifier,
    viewModel: MapViewModel
) {
    MapAddBikeView(
        bikeId = viewModel.bikeId.removePrefix("{bikeId}"), // Removes "{bikeId}" from bikeId passed in
        modifier = modifier,
        viewModel = viewModel
    )
}

/**
 * The main composable that sets up the map view and handles permissions, location updates, and interactions.
 *
 * @param bikeId The ID of the bike being added to the map.
 * @param modifier Modifier for layout styling.
 * @param viewModel The ViewModel for managing bike and owner data.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapAddBikeView(
    bikeId: String,
    modifier: Modifier,
    viewModel: MapViewModel = hiltViewModel()) {

    val bikes by viewModel.bikes.collectAsState()

    val owner by viewModel.ownerDetails.collectAsState()

    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    fun onBikeSelected(ownerId: String) {
        viewModel.setOwnerId(ownerId)
    }

    val markersData = bikes.map { bike ->
        val lat = bike.location.latitude
        val lng = bike.location.longitude

        MarkerDataAddPin(
            location = LatLng(lat, lng),
            bikeName = bike.bikeName,
            rating = owner?.let {
                if (it.numberOfRatings > 0) it.totalRating / it.numberOfRatings else 0
            } ?: 0,
            bikeImgId = bike.imageUrl,
            bikePrice = bike.price,
            city = bike.city,
            startTime = bike.startTime,
            endTime = bike.endTime,
            ownerId = bike.ownerId,
        )
    }

    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var isPinManuallyMoved by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    val mapStyleResId = if (isDarkTheme) R.raw.map_style_night else R.raw.map_style
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(51.423, 5.46), 10f)
    }

    val locationsPermissions = rememberMultiplePermissionsState(
        listOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
    )
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(
        LocalContext.current
    )

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(locationsPermissions.allPermissionsGranted) {
        if (locationsPermissions.allPermissionsGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    markerPosition = LatLng(it.latitude, it.longitude)
                    showConfirmationDialog = true
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

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        currentLocation = location
                    }
                }
            }

            if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                fusedLocationClient.requestLocationUpdates(
                    LocationRequest.Builder(1000L).build(),
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            locationsPermissions.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect(currentLocation) {
        if (!isPinManuallyMoved) {
            currentLocation?.let {
                markerPosition = LatLng(it.latitude, it.longitude)
                showConfirmationDialog = true
            }
        }
    }

    var selectedMarker by remember { mutableStateOf<MarkerDataAddPin?>(null) }

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

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult : LocationResult) {
            for (location in locationResult.locations) {
                currentLocation = location
                myLocationSource.onLocation(location)
                coroutineScope.launch {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(
                            LatLng(location.latitude, location.longitude),
                            cameraPositionState.position.zoom
                        )
                    )
                }
            }
        }
    }

    fun startListeningToLocations() {
        if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.requestLocationUpdates(
            LocationRequest.Builder(1000L).build(),
            locationCallback,
            Looper.getMainLooper()
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().systemBarsPadding().padding(bottom = 60.dp),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (locationsPermissions.allPermissionsGranted) {
                        startListeningToLocations()
                        currentLocation?.let { location ->
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(
                                        LatLng(location.latitude, location.longitude),
                                        cameraPositionState.position.zoom
                                    )
                                )
                            }
                        }
                    } else {
                        locationsPermissions.launchMultiplePermissionRequest()
                    }
                },
                modifier = Modifier.offset(x = (13).dp, y = (-85).dp),
                shape = CircleShape,
                containerColor = Color.White,
                contentColor =
                if (locationsPermissions.allPermissionsGranted) {
                    Color(0xFF1C73E8)
                } else {
                    Color.Gray
                }
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.target),
                    contentDescription = "Live Location"
                )
            }
        }
    ) {
        val mapProperties = MapProperties(
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, mapStyleResId),
            maxZoomPreference = 18f,
            minZoomPreference = 3f,
            isMyLocationEnabled = locationsPermissions.allPermissionsGranted
        )
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            locationSource = myLocationSource,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = false,
                compassEnabled = false,
            ),
            onMapClick = { latLng ->
                markerPosition = latLng
                isPinManuallyMoved = true
                showConfirmationDialog = true
                selectedMarker = MarkerDataAddPin(
                    location = latLng,
                    bikeName = "New Location",
                    rating = 0,
                    bikeImgId = "",
                    bikePrice = 0.0,
                    city = "",
                    ownerId = ""
                )
            }
        ) {
            markerPosition?.let { position ->
                Marker(
                    state = MarkerState(position = position),
                    onClick = {
                        val existingMarkerData = markersData.find { it.location == position }

                        if (existingMarkerData != null) {
                            selectedMarker = existingMarkerData.copy(location = position)
                            showConfirmationDialog = true
                        } else {
                            selectedMarker = MarkerDataAddPin(
                                location = position,
                                bikeName = "New Location",
                                rating = 0,
                                bikeImgId = "",
                                bikePrice = 0.0,
                                city = "",
                                ownerId = ""
                            )
                        }
                        true
                    }
                )
            }
        }

        if (showConfirmationDialog && markerPosition != null) {
            BottomCardSetPin(
                markerData = selectedMarker ?: MarkerDataAddPin(
                    location = markerPosition!!,
                    bikeName = "New Location",
                    rating = 0,
                    bikeImgId = "",
                    bikePrice = 0.0,
                    city = "",
                    ownerId = ""
                ),
                onDismiss = {
                    showConfirmationDialog = false
                    selectedMarker = null
                },
                onConfirm = {
                    if (bikeId.isNotBlank()) {
                        Log.e("MapViewModel", bikeId)
                    }
                    if (!isPinManuallyMoved) {
                        currentLocation?.let {
                            markerPosition = LatLng(it.latitude, it.longitude)
                        }
                        viewModel.updateBikeLocation(bikeId, markerPosition!!)
                    }
                    selectedMarker?.let {
                        viewModel.updateBikeLocation(bikeId, it.location)
                    }
                    showConfirmationDialog = false
                    selectedMarker = null
                    viewModel.goToMap()
                }
            )
        }
    }
}

/**
 * Data class representing the marker information for adding a bike pin.
 */
data class MarkerDataAddPin(
    val location: LatLng,
    val bikeName: String,
    val rating: Int,
    val bikeImgId: String,
    val bikePrice: Double,
    val city: String,
    val startTime: Timestamp? = null,
    val endTime: Timestamp? = null,
    val ownerId: String,
)

/**
 * Composable function for showing the bottom card popup when a pin is clicked.
 *
 * @param markerData The marker data containing information to show in the popup.
 * @param onDismiss Callback to dismiss the popup.
 * @param onConfirm Callback to confirm the action in the popup.
 */
@Composable
fun BottomCardSetPin(
    markerData: MarkerDataAddPin,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                onClick = { onDismiss() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 250.dp)
                .padding(bottom = 10.dp, start = 10.dp, end = 10.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Confirm Location",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = "Location",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${"%.2f".format(markerData.location.latitude)}, ${"%.2f".format(markerData.location.longitude)}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier.width(150.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Confirm")
                        }
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}