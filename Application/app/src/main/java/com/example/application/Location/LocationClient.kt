package com.example.application.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class LocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) {

    lateinit var setInterval: (interval: Long) -> Unit

    @SuppressLint("MissingPermission")
    fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            canMakeLocationRequest()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location ->
                        launch { send(location) }
                    }
                }
            }

            initLocationUpdates(interval, locationCallback)

            this@LocationClient.setInterval =
                { interval: Long -> initLocationUpdates(interval, locationCallback) }

            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initLocationUpdates(interval: Long, locationCallback: LocationCallback) {
        val newRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval * 1000)
            .setMaxUpdateAgeMillis(interval * 1000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        client.requestLocationUpdates(
            newRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    @SuppressLint("MissingPermission")
    fun makeLocationRequest(): Flow<Location> {
        return callbackFlow {
            canMakeLocationRequest()

            client.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        launch { send(location) }
                    }
                }

            awaitClose {}
        }
    }


    private fun canMakeLocationRequest() {
        if (!hasLocationPermission()) {
            throw Exception("Missing location permission")
        }

        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!isGpsEnabled && !isNetworkEnabled) {
            throw Exception("GPS is disabled")
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    }
}