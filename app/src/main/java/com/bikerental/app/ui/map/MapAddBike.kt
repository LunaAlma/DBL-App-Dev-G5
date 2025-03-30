package com.bikerental.app.ui.map

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Search
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

// Note that rememberMultiplePermissions is using an experimental API
// Regularly check if it is working (this is easier code than alternative though)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapAddBike(
    bikeId: String,
    modifier: Modifier,
    viewModel: MapViewModel = hiltViewModel()) {
    val bikes by viewModel.bikes.collectAsState()

    val owner by viewModel.ownerDetails.collectAsState()

    // Start with a default marker location (you might want to default to live location) (NEW)
    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    // State for showing confirmation dialog (NEW)
    var showConfirmationDialog by remember { mutableStateOf(false) }

    fun onBikeSelected(ownerId: String) {
        viewModel.setOwnerId(ownerId)
    }

    val markersData = bikes.map { bike ->
        // bike.location is a GeoPoint from Firebase
        val lat = bike.location.latitude
        val lng = bike.location.longitude

        MarkerDataAddPin(
            location = LatLng(lat, lng),
            ownerName = bike.bikeName,
            rating = owner?.let {
                if (it.numberOfRatings > 0) it.totalRating / it.numberOfRatings else 0
            } ?: 0,
            bikeImgId = bike.imageUrl,  // uses Coil to load this image
            bikePrice = bike.price,
            city = bike.city,
            startTime = bike.startTime,
            endTime = bike.endTime,
            ownerId = bike.ownerId,
        )
    }

    var currentLocation by remember { mutableStateOf<Location?>(null) }
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

    // If not granted, request permission immediately (NEW)
    LaunchedEffect(Unit) {
        if (!locationsPermissions.allPermissionsGranted) {
            locationsPermissions.launchMultiplePermissionRequest()
        }
    }

    // When we get a live location update, update the marker if not already set (NEW)
    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            // Update markerPosition if not already set (or you may always want to reset)
            if (markerPosition == null) {
                markerPosition = LatLng(it.latitude, it.longitude)
            }
        }
    }

    // Track which marker is currently selected
    var selectedMarker by remember { mutableStateOf<MarkerDataAddPin?>(null) }

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

    Scaffold( // Removed search bar for moment
        topBar = {
            // This ensures the search bar stays at the top
            SearchBarAddPin(
                text = "searchText",
                onTextChange = { "searchText = it" },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        viewModel.onSearchBarClick()
                    },
                onClose = {}
            )
        },
        modifier = Modifier.fillMaxSize().systemBarsPadding().padding(bottom = 60.dp),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (locationsPermissions.allPermissionsGranted) {
                        startListeningToLocations()
                        // Navigate to live location now
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
                // Icon here
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.target),
                    contentDescription = "Live Location"
                )
            }
        }
    ) {
        // Gives error when empty
        val mapProperties = MapProperties(
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, mapStyleResId),
            // Zoom limitations
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
            locationSource = myLocationSource,
            onMapClick = { latLng ->
                // Update marker location and show confirmation dialog
                markerPosition = latLng
                showConfirmationDialog = true
            }
        ) {
            markerPosition?.let { position ->
                Marker(
                    state = MarkerState(position = position),
                    onClick = {
                        // You might also update markerPosition here if you allow dragging
                        showConfirmationDialog = true
                        true
                    }
                )
            }
//            for (data in markersData) {
//                val markerState = remember { MarkerState(position = data.location) }
//                Marker(
//                    state = markerState,
////                    title = data.ownerName,
////                    snippet = data.rating,
//                    onClick = {
//                        // Update the selected marker
//                        onBikeSelected(data.ownerId)
//                        selectedMarker = data
//                        // Return true to consume click
//                        true
//                    }
//                )
//            }
        }

        if (showConfirmationDialog && selectedMarker != null) {
            BottomCardSetPin(
                markerData = selectedMarker!!,
                onDismiss = { showConfirmationDialog = false },
                onConfirm = {
                    viewModel.updateBikeLocation(bikeId, selectedMarker!!.location)
                    showConfirmationDialog = false
                }
            )
        }
    }

}

// Hardcoded data
//private val markersData = listOf(
//    MarkerData(
//        location = LatLng(51.423,5.462),
//        ownerName = "John",
//        rating = "5",
//        bikeImgId = R.drawable.target,
//        bikePrice = 5,
//    ),
//    MarkerData(
//        location = LatLng(51.508,5.398),
//        ownerName = "Tim",
//        rating = "4",
//        bikeImgId = R.drawable.target,
//        bikePrice = 6,
//    ),
//)

// Original MarkerData
data class MarkerDataAddPin(
    val location: LatLng,
    val ownerName: String,
    val rating: Int,
    val bikeImgId: String, // Null option here? String? = null
    val bikePrice: Int,
    val city: String,
    val startTime: Timestamp? = null,
    val endTime: Timestamp? = null,
    val ownerId: String,
)


/**
 * Bottom card pop up when pin clicked
 */
@Composable
fun BottomCardSetPin(
    markerData: MarkerDataAddPin,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    // Box that takes the entire screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
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
                .width(300.dp)
                .heightIn(min = 150.dp, max = 300.dp)
                // Clicking the card doesn't do anything
                .clickable(
                    onClick = { /* nothing */ },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
                .padding(bottom = 20.dp), // prev. 16
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Confirm Bike Location",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Do you want to set the bike's location to (${markerData.location.latitude}, ${markerData.location.longitude})?"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = Color.Black
                            )
                        }
                        IconButton(onClick = onConfirm) {
                            Text(text = "Confirm", color = Color.Blue)
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

@Composable
fun SearchBarAddPin(
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(start = 16.dp),
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )

            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 8.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text(
                            text = "Search...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                    innerTextField()
                }
            )

            if (text.isNotEmpty()) {
                IconButton(onClick = { onTextChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search"
                    )
                }
            }
        }
    }
}