package com.agora.agora.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.agora.agora.model.data.Address
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapScreen(address: Address) {
    val context = LocalContext.current

    // Compute location data only once per address change
    val locationData by remember(address) { mutableStateOf(address.getLatLng()) }

    // Show toast only once when locationData is initially set
    LaunchedEffect(locationData) {
        if (locationData == LatLng(-1.0, -1.0)) {
            Toast.makeText(context, "No location data found!", Toast.LENGTH_SHORT).show()
        }
    }

    if (locationData != LatLng(-1.0, -1.0)) {
        val markerState = rememberMarkerState(position = locationData)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(locationData, 15f)
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = markerState,
                title = "Marker",
                snippet = "Marker"
            )
        }
    }
}
