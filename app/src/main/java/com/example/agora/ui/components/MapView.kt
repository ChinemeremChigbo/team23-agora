package com.example.agora.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.agora.model.data.Address
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MapScreen(address: Address, viewModel: MapViewViewModel = viewModel()) {
    val context = LocalContext.current

    // Observe LiveData as Compose state
    val locationData by viewModel.locationData.observeAsState()

    // Fetch location data when the composable is first launched
    LaunchedEffect(Unit) {
        viewModel.fetchLocation(Address.getCountryCode(address.getCountry()) ?: "", address.getPostalCode().substringBefore(" "))
    }

    when(locationData) {
        null -> CircularProgressIndicator()
        LatLng(-1.0, -1.0) -> Toast.makeText(context, "No location data found!", Toast.LENGTH_SHORT).show()
        else -> {
            val markerState = rememberMarkerState(position = locationData!!)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(locationData!!, 15f)
            }
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = markerState,
                    title = "marker",
                    snippet = "Marker"
                )
            }
        }
    }



}