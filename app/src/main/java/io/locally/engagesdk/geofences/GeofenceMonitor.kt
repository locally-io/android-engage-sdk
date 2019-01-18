package io.locally.engagesdk.geofences

import android.content.Context
import android.location.Location
import io.locally.engagesdk.managers.LocationManager
import io.locally.engagesdk.managers.LocationManager.LocationDelegate

class GeofenceMonitor(context: Context): LocationDelegate {

    private val subscribers = arrayListOf<LocationListener>()
    private var locationManager: LocationManager = LocationManager(context)

    fun startMonitoring(){
        locationManager.startMonitoring(this)
    }

    fun stopMonitoring(){
        locationManager.stopMonitoring()
        subscribers.clear()
    }

    fun subscribe(subscriber: LocationListener){
        subscribers.add(subscriber)
    }

    override fun didLocationUpdated(location: Location) {
        subscribers.forEach { it.didLocationUpdated(location) }
    }

    interface LocationListener {
        fun didLocationUpdated(location: Location) {}
    }
}