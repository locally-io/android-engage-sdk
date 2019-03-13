package io.locally.engagesdk.managers

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import io.locally.engagesdk.common.FAST_INTERVAL
import io.locally.engagesdk.common.INTERVAL
import io.locally.engagesdk.common.Utils

data class LocationManager(private val context: Context, var delegate: LocationDelegate? = null): LocationCallback() {

    private val REQUEST_CHECK_SETTINGS = 102
    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var callback: ((Location?) -> Unit)? = null

    init {
        requestPermissions()
    }

    fun currentLocation(callback: ((Location?) -> Unit)? = null){
        this.callback = callback

        if (hasPermission){
            locationRequest()
        } else {
            requestPermissions()
        }
    }

    fun startMonitoring(){
        if(hasPermission){
            locationUpdates()
        } else {
            requestPermissions()
        }
    }

    fun stopMonitoring(){
       fusedLocationClient.removeLocationUpdates(this)
    }

    @SuppressLint("MissingPermission")
    private fun locationUpdates(){
        val locationRequest = LocationRequest().apply {
            interval = INTERVAL
            fastestInterval = FAST_INTERVAL
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest).setAlwaysShow(true)

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            fusedLocationClient.requestLocationUpdates(locationRequest, this, null)
        }.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                try {
                    exception.startResolutionForResult(context as Activity, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {}
            }
        }
    }

    override fun onLocationResult(locations: LocationResult?) {
        locations?.lastLocation?.let { location ->
            io.locally.engagesdk.EventHandler.listener?.locationUpdate(location, Utils.logTime())
            delegate?.didLocationUpdated(location)
        }
    }

    @SuppressLint("MissingPermission")
    private fun locationRequest(){
        fusedLocationClient.lastLocation
                .addOnSuccessListener { callback?.invoke(it) }
                .addOnFailureListener { callback?.invoke(null) }
    }

    private val hasPermission: Boolean
        get() {
            val coarse = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            val fine = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)

            return coarse == PackageManager.PERMISSION_GRANTED && fine == PackageManager.PERMISSION_GRANTED
        }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CALL_PHONE), REQUEST_CHECK_SETTINGS)
    }

    interface LocationDelegate {
        fun didLocationUpdated(location: Location)
    }
}