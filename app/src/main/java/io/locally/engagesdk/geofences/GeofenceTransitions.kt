package io.locally.engagesdk.geofences

import android.app.IntentService
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceTransitions: IntentService("geofence-transition-service") {
    private val TAG = javaClass.name
    override fun onHandleIntent(intent: Intent?) {
        val eventList = GeofencingEvent.fromIntent(intent)
        if(eventList.hasError()) {
            Log.e(javaClass.name, "Error: ${eventList.errorCode}")
            return
        }

        val geofenceTransition = eventList.geofenceTransition

        when(geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                eventList.triggeringGeofences.forEach { geofence ->
                    LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("geofencing").apply {
                        putExtra("proximity", "ENTER")
                        putExtra("geofenceId", geofence.requestId)
                    })
                }
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                eventList.triggeringGeofences.forEach { geofence ->
                    LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("geofencing").apply {
                        putExtra("proximity", "EXIT")
                        putExtra("geofenceId", geofence.requestId)
                    })
                }
            }

            else -> Log.i(TAG, "Invalid transition found: $geofenceTransition")
        }
    }
}