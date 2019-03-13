package io.locally.engagesdk.network.services.beacons

import android.location.Location
import com.google.gson.GsonBuilder
import io.locally.engagesdk.EventHandler
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.datamodels.campaign.BeaconCampaign
import io.locally.engagesdk.datamodels.impression.Beacon
import io.locally.engagesdk.datamodels.impression.ImpressionBeacon
import io.reactivex.Observable

class BeaconServices {

    companion object {
        private val imp = GsonBuilder().setPrettyPrinting().create()

        fun getCampaign(bluetoothEnabled: Boolean, location: Location, beacon: Beacon): Observable<BeaconCampaign> {
            val impression =
                    ImpressionBeacon(
                            bluetooth  = bluetoothEnabled,
                            impressionType = beacon.type,
                            impressionProximity = beacon.proximity,
                            major = beacon.major,
                            minorDec = beacon.minorDec,
                            location = location)

            EventHandler.listener?.impressionUpdate("Impression Beacon - ${imp.toJson(impression)}", Utils.logTime())
            return BeaconAPI.instance.getCampaign(impressionBeacon = impression)
        }
    }
}