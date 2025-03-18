package com.example.agora.ui.components

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agora.model.util.ZippopotamClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class MapViewViewModel : ViewModel() {
    private val _locationData = MutableLiveData<LatLng>()
    val locationData: LiveData<LatLng> get() = _locationData
    fun fetchLocation(countryCode: String, postalCode: String) {
        viewModelScope.launch {
            try {
                val response = ZippopotamClient.instance.getLocation(countryCode, postalCode)
                if (response.places.isNotEmpty()) {
                    val latitude = response.places[0].latitude
                    val longitude = response.places[0].longitude
                    _locationData.value = LatLng(latitude.toDouble(), longitude.toDouble())
                } else {
                    _locationData.value = LatLng(-1.0, -1.0)
                }
            } catch (e: Exception) {
                _locationData.value = LatLng(-1.0, -1.0)
            }
        }
    }
}