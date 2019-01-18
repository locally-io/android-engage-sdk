package io.locally.engagesdk.beacons

import android.content.Context
import io.locally.engagesdk.common.UUID
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.datamodels.impression.Beacon
import io.locally.engagesdk.datamodels.impression.ImpressionType.BEACON
import io.locally.engagesdk.datamodels.impression.Proximity
import com.kontakt.sdk.android.ble.configuration.ScanMode
import com.kontakt.sdk.android.ble.configuration.ScanPeriod
import com.kontakt.sdk.android.ble.device.BeaconRegion
import com.kontakt.sdk.android.ble.manager.ProximityManager
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener
import com.kontakt.sdk.android.common.KontaktSDK
import com.kontakt.sdk.android.common.Proximity.*
import com.kontakt.sdk.android.common.profile.IBeaconDevice
import com.kontakt.sdk.android.common.profile.IBeaconRegion
import java.util.UUID.fromString

object BeaconScanner: IBeaconListener {
    private lateinit var identifier: String
    private lateinit var proximityManager: ProximityManager
    var delegate: BeaconDelegate? = null

    fun init(context: Context, identifier: String) {
        BeaconScanner.identifier = identifier
        KontaktSDK.initialize(Utils.kontaktKey)

        proximityManager = ProximityManagerFactory.create(context)
        proximityManager.setIBeaconListener(this)
        proximityManager.configuration()
                .scanMode(ScanMode.LOW_POWER)
                .scanPeriod(ScanPeriod.MONITORING)
    }

    fun scan(delegate: BeaconDelegate) {
        BeaconScanner.delegate = delegate
        val region = BeaconRegion.builder()
                .identifier(identifier)
                .proximity(fromString(UUID))
                .major(BeaconRegion.ANY_MAJOR)
                .minor(BeaconRegion.ANY_MINOR)
                .build()

        proximityManager.spaces().iBeaconRegion(region)
        proximityManager.connect { proximityManager.startScanning() }
    }

    override fun onIBeaconDiscovered(iBeacon: IBeaconDevice?, region: IBeaconRegion?) {}

    fun stopScan(){
        proximityManager.stopScanning()
        proximityManager.disconnect()
    }

    override fun onIBeaconsUpdated(iBeacons: MutableList<IBeaconDevice>?, region: IBeaconRegion?) {
        iBeacons?.let {
            val beaconsUpdated = it.map { iBeacon ->
                val proximity: Proximity =
                        when(iBeacon.proximity) {
                            IMMEDIATE -> Proximity.TOUCH
                            NEAR -> Proximity.NEAR
                            FAR -> Proximity.FAR
                            UNKNOWN -> Proximity.FAR
                        }

                Beacon(BEACON, proximity, iBeacon.major, iBeacon.minor)
            }

            delegate?.didBeaconsUpdated(beaconsUpdated)
        }
    }

    override fun onIBeaconLost(iBeacon: IBeaconDevice?, region: IBeaconRegion?) {}
}

interface BeaconDelegate {
    fun didBeaconsUpdated(beacons: List<Beacon>){}
}