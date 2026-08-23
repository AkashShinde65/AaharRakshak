package com.aaharrakshak.mobile.data

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

class ConsentLocationProvider(private val context: Context) {
    @SuppressLint("MissingPermission")
    suspend fun currentLocation(consentAccepted: Boolean): GpsLocationDto? {
        if (!consentAccepted) {
            return null
        }
        val client = LocationServices.getFusedLocationProviderClient(context)
        val location = runCatching { client.lastLocation.await() }.getOrNull()
        return GpsLocationDto(
            consentAccepted = true,
            latitude = location?.latitude,
            longitude = location?.longitude,
            address = null
        )
    }
}
