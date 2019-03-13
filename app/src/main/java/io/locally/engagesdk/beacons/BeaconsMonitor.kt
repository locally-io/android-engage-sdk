package io.locally.engagesdk.beacons

import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import io.locally.engagesdk.campaigns.CampaignCoordinator
import io.locally.engagesdk.datamodels.impression.Beacon

class BeaconsMonitor(private val context: Context): BeaconDelegate {

    companion object {
        private const val REQUEST_CODE = 101
    }

    init { requestPermissions() }

    fun startMonitoring(){
        val identifier = java.util.UUID.randomUUID().toString() //random identifier (for now)
        BeaconScanner.init(context, identifier)

        if(hasPermission) BeaconScanner.scan(this)
        else requestPermissions()
    }

    fun stopMonitoring() = BeaconScanner.stopScan()

    override fun didBeaconsUpdated(beacons: List<Beacon>) = CampaignCoordinator.requestBeaconCampaigns(beacons)

    private val hasPermission: Boolean
        get() {
            val coarse = ContextCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION)
            val fine = ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION)
            val btAdmin = ContextCompat.checkSelfPermission(context, BLUETOOTH_ADMIN)
            val bluetooth = ContextCompat.checkSelfPermission(context, BLUETOOTH)

            return coarse == PERMISSION_GRANTED && fine == PERMISSION_GRANTED &&
                    btAdmin == PERMISSION_GRANTED && bluetooth == PERMISSION_GRANTED
        }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(context as Activity,
                arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION,
                        BLUETOOTH_ADMIN, BLUETOOTH), REQUEST_CODE)
    }
}