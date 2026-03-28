package com.example.windwidget.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

object LocationHelper {
    suspend fun getCurrentLocation(context: Context): Location? {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        val client = LocationServices.getFusedLocationProviderClient(context)

        // Try lastLocation first — it's fast, doesn't need GPS, and works
        // reliably from widget/background contexts where getCurrentLocation
        // often times out.
        return try {
            val last = client.lastLocation.await()
            if (last != null) return last
            client.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                CancellationTokenSource().token
            ).await()
        } catch (_: Exception) {
            try {
                client.lastLocation.await()
            } catch (_: Exception) {
                null
            }
        }
    }
}
