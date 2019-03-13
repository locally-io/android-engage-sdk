package io.locally.engagesdk.geofences

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.support.annotation.UiThread
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.android.gms.location.Geofence.*
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import io.locally.engagesdk.EventHandler
import io.locally.engagesdk.campaigns.CampaignCoordinator
import io.locally.engagesdk.common.TIMER_DELAY
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.datamodels.geofences.Geofence
import io.locally.engagesdk.datamodels.geofences.Geofence.CoverageType.*
import io.locally.engagesdk.datamodels.impression.Proximity
import io.locally.engagesdk.datamodels.impression.Proximity.ENTER
import io.locally.engagesdk.datamodels.impression.Proximity.EXIT
import io.locally.engagesdk.managers.LocationManager
import io.locally.engagesdk.managers.LocationManager.LocationDelegate
import io.locally.engagesdk.network.services.geofences.GeofenceServices
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import java.util.*
import kotlin.concurrent.schedule

class GeofenceMonitor(val context: Context) : LocationDelegate {
    private val TAG = "GeofenceMonitor"
    private lateinit var timer: Timer
    private val locationManager: LocationManager = LocationManager(context)
    private var geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)
    private var geofences: List<Geofence> = listOf()
    private val geofenceInside = arrayListOf<String>()
    private val geofencesMonitored = arrayListOf<com.google.android.gms.location.Geofence>()
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceTransitions::class.java)
        PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
    private val geofenceResponse = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val trigger = intent?.getStringExtra("proximity")
            val id = intent?.getStringExtra("geofenceId")
            trigger?.let {
                id?.let {
                    when(trigger) {
                        "ENTER" -> {
                            geofenceInside.contains(id).apply {
                                if(!this) {
                                    locationManager.currentLocation { current ->
                                        current?.let { EventHandler.listener?.impressionUpdate("Entered circle geofence - lat(${it.latitude} lng(${it.longitude})", Utils.logTime()) }
                                    }

                                    requestCampaign(ENTER, id)
                                }
                            }
                        }
                        "EXIT" -> {
                            geofenceInside.contains(id).apply {
                                if(this) {
                                    locationManager.currentLocation { current ->
                                        current?.let { EventHandler.listener?.impressionUpdate("Leaved circle geofence - lat(${it.latitude} lng(${it.longitude})", Utils.logTime()) }
                                    }

                                    requestCampaign(EXIT, id)
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun startMonitoring(radius: Int = 500, refresh: Long = 600000) {
        locationManager.startMonitoring()
        geofenceInside.clear()
        timer = Timer("GeofenceRefresh", true).apply {
            schedule(TIMER_DELAY, refresh) {
                locationManager.currentLocation { checkSurrounding(it, radius) }
            }
        }
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(geofenceResponse, IntentFilter("geofencing"))
    }

    @SuppressLint("MissingPermission")
    private fun updateMonitor() {
        geofencesMonitored.clear()
        val circular = geofences.filter { geofence -> geofence.type == RADIUS }
        circular.forEach { geofence ->
            geofencesMonitored.add(Builder()
                    .setRequestId(geofence.id.toString())
                    .setCircularRegion(geofence.center.lat, geofence.center.lng, (geofence.center.radius * 1609.344).toFloat())
                    .setExpirationDuration(NEVER_EXPIRE)
                    .setTransitionTypes(GEOFENCE_TRANSITION_ENTER or GEOFENCE_TRANSITION_EXIT)
                    .build())
        }

        Log.i(TAG, "Monitoring ${geofencesMonitored.size} geofences")

        geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent)?.run {
            addOnSuccessListener {
                Log.i(TAG, "Monitoring geofences")
            }
            addOnFailureListener {
                Log.e(TAG, "Error: ${it.localizedMessage}")
            }
        }

        locationManager.delegate = this
    }

    fun stopMonitoring() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(geofenceResponse)
        locationManager.delegate = null
        locationManager.stopMonitoring()
        timer.cancel()
        timer.purge()
        geofencingClient.removeGeofences(geofencePendingIntent)?.run {
            addOnSuccessListener {
                Log.i(TAG, "Stopped Geofence Monitor")
            }
            addOnFailureListener {
                Log.e(TAG, "Error: ${it.localizedMessage}")
            }
        }
    }

    private fun checkSurrounding(location: Location?, radius: Int) {
        location?.let {
            doAsync {
                GeofenceServices.inRange(it.latitude, it.longitude, radius)
                        .subscribe({ result ->
                            geofences = result.data

                            updateMonitor()
                        }, { error -> error.printStackTrace() })
            }
        }
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_DWELL or GeofencingRequest.INITIAL_TRIGGER_EXIT)
            addGeofences(geofencesMonitored)
        }.build()
    }

    override fun didLocationUpdated(location: Location) {
        geofences.filter { geofence -> geofence.type == POLYGON }
                .forEach { polygon ->
                    val points = polygon.points?.map { point -> LatLng(point[0], point[1]) }
                    PolyUtil.containsLocation(location.latitude, location.longitude, points, false).apply {
                        if(this) {
                            geofenceInside.contains(polygon.id.toString()).apply {
                                if(!this) {
                                    EventHandler.listener?.impressionUpdate("Entered polygon geofence - lat(${location.latitude} lng(${location.longitude})", Utils.logTime())

                                    requestCampaign(ENTER, polygon.id.toString())
                                }
                            }
                        } else {
                            geofenceInside.contains(polygon.id.toString()).apply {
                                if(this) {
                                    EventHandler.listener?.impressionUpdate("Leaved polygon geofence - lat(${location.latitude} lng(${location.longitude})", Utils.logTime())

                                    requestCampaign(EXIT, polygon.id.toString())
                                }
                            }
                        }
                    }
                }
    }

    private fun requestCampaign(proximity: Proximity, id: String){
        when(proximity) {
            ENTER -> {
                CampaignCoordinator.requestGeofenceCampaign(proximity)
                geofenceInside.add(id)
                Log.i(TAG, "Entered: $id")
            }
            EXIT -> {
                CampaignCoordinator.requestGeofenceCampaign(proximity)
                geofenceInside.remove(id)
                Log.i(TAG, "Leaved: $id")
            }
            else -> {}
        }
    }
}