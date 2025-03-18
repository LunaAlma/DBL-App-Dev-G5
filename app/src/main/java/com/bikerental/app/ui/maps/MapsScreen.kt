package com.bikerental.app.ui.maps

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.bikerental.app.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
//import com.google.android.gms.maps.model.CameraPosition
//import com.google.android.gms.maps.model.LatLng
//import com.google.maps.android.compose.GoogleMap
//import com.google.maps.android.compose.rememberCameraPositionState
//import com.lucianocoletti.composemapstutorial.ui.theme.ComposeMapsTutorialTheme
//import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.rememberCameraPositionState

import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.maps.android.compose.Circle
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Text
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
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.launch



// Note that rememberMultiplePermissions is using an experimental API
// Regularly check if it is working (this is easier code than alternative though)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapsScreen() {
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    val mapStyleResId = if (isDarkTheme) R.raw.map_style_night else R.raw.map_style
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(51.423, 5.46), 10f)
    }
    val locations = listOf(
        LatLng(51.423,5.462),
        LatLng(51.508,5.398)
    )
    val locationsPermissions = rememberMultiplePermissionsState(
        listOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
    )
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(
        LocalContext.current
    )

    // Track which marker is currently selected
    var selectedMarker by remember { mutableStateOf<MarkerData?>(null) }






    //here
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


    // Here is where mapProperties used to be


    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                modifier = Modifier.offset(x = (13).dp, y = (-65).dp),
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
            // Zoom limitations?
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
            locationSource = myLocationSource
        ) {
            // Add markers, etc.
//            for (loc in locations) {
//                Marker(
//                    state = MarkerState(loc)
//                )
//            }
            for (data in markersData) {
//                Marker(
//                    state = MarkerState(position = data.location),
//                    title = data.ownerName,
//                    snippet = data.rating,
//                )

//                MarkerInfoWindowContent(
//                    state = MarkerState(position = data.location),
//                    title = data.ownerName,
//                    snippet = data.rating,
//                ) {
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(32.dp)
//                    ) {
//                        Text(
//                            modifier = Modifier.padding(top = 6.dp),
//                            text = data.ownerName,
//                            fontWeight = FontWeight.Bold,
//                            color = Color.Black
//                        )
//                        data.rating?.let { desc -> Text(desc) }
//                        data.bikeImgId?.let { res ->
//                            Image(
//                                modifier = Modifier
//                                    .padding(top = 6.dp)
//                                    .size(240.dp),
//                                painter = painterResource(id = res),
//                                contentDescription = data.bikePrice.toString()
//                            )
//                        }
//                    }
//                }
                Marker(
                    state = MarkerState(position = data.location),
                    title = data.ownerName,
                    snippet = data.rating,
                    onClick = {
                        // Update the selected marker
                        selectedMarker = data
                        // Return true to consume the click
                        true
                    }
                )




            }
        }

        // Show the bottom card if a marker is selected
        selectedMarker?.let { marker ->
            BottomCard(
                markerData = marker,
                onDismiss = { selectedMarker = null }
            )
        }
    }



    // Here is where GoogleMap used to be

}

// Hardcoded data
private val markersData = listOf(
    MarkerData(
        location = LatLng(51.423,5.462),
        ownerName = "John",
        rating = "5",
        bikeImgId = R.drawable.target,
        bikePrice = 5,
    ),
    MarkerData(
        location = LatLng(51.508,5.398),
        ownerName = "Tim",
        rating = "4",
        bikeImgId = R.drawable.target,
        bikePrice = 6,
    ),
)

// Original MarkerData
data class MarkerData(
    val location: LatLng,
    val ownerName: String,
    val rating: String,
    val bikeImgId: Int? = null,
    val bikePrice: Int,
)

// Firebase spec.
//data class MarkerData(
//    val location: LatLng = LatLng(0.0, 0.0),  // Provide defaults so Firestore can map them
//    val ownerName: String = "",
//    val bikeImgId: String? = null,           // We'll load from this URL
//    val bikePrice: Int = 0,
//    val rating: String = ""                   // If you want a rating, keep it or remove it
//)


/**
 * Simple bottom card composable that appears at the bottom of the screen.
 * You can style it however you like (rounded corners, images, etc.).
 */
@Composable
fun BottomCard(
    markerData: MarkerData,
    onDismiss: () -> Unit
) {
    // Box overlay that takes the entire screen
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
        // The card itself at the bottom
        Card(
            modifier = Modifier
                //.fillMaxWidth()
                .width(300.dp)
                .heightIn(min = 150.dp, max = 300.dp)
                // Make sure clicks on the card do NOT dismiss it
                .clickable(
                    onClick = { /* do nothing */ },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
                .padding(bottom = 20.dp), // prev. 16
            shape = androidx.compose.foundation.shape.RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp
            )
        ) {
//            Column(
//                modifier = Modifier.padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = markerData.ownerName,
//                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
//                    color = Color.Black
//                )
//                Text(
//                    text = "Rating: ${markerData.rating}",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//                markerData.bikeImgId?.let { res ->
//                    Image(
//                        painter = painterResource(id = res),
//                        contentDescription = "Bike image",
//                        modifier = Modifier
//                            .padding(top = 8.dp)
//                            .size(100.dp)
//                    )
//                }
//                Text(text = "Price: ${markerData.bikePrice}€")
//            }
            // Use a Row to arrange the image on the left and text on the right
            Row(modifier = Modifier.padding(16.dp)) {
                // Image on the left
                markerData.bikeImgId?.let { res ->
                    Image(
                        painter = painterResource(id = res),
//                        painter = rememberAsyncImagePainter(model = res),
                                contentDescription = "Bike image",
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
                // A spacer between image and text
                Spacer(modifier = Modifier.width(16.dp))
                // Column for text on the right
                Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                    Text(
                        text = markerData.ownerName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
//                    Text(
//                        text = "Rating: ${markerData.rating}",
//                        style = MaterialTheme.typography.bodyMedium
//                    )
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
                            text = markerData.rating,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Text(text = "${markerData.bikePrice}€/hour")
                }
            }

        }
    }
}