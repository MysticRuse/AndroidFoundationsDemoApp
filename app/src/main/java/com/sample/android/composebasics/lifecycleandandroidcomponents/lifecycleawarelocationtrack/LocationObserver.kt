package com.sample.android.composebasics.lifecycleandandroidcomponents.lifecycleawarelocationtrack

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.*

/**
 * A lifecycle-aware component that tracks location using FusedLocationProviderClient.
 * It starts tracking when the lifecycle reaches the STARTED state (onStart)
 * and stops when it reaches the STOPPED state (onStop).
 */
class LocationObserver(
    private val context: Context,
    private val onLocationUpdate: (Location) -> Unit
) : DefaultLifecycleObserver {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                onLocationUpdate(location)
                Log.d("LocationObserver", "Location updated: ${location.latitude}, ${location.longitude}")
            }
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        startTracking()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        stopTracking()
    }

    @SuppressLint("MissingPermission")
    fun startTracking() {
        Log.d("LocationObserver", "startTracking: Requesting location updates")
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(2000)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopTracking() {
        Log.d("LocationObserver", "stopTracking: Removing location updates")
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
