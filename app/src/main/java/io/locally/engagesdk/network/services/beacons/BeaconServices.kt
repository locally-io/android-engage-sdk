package io.locally.engagesdk.network.services.beacons

import android.location.Location
import io.locally.engagesdk.datamodels.campaign.BeaconCampaign
import io.locally.engagesdk.datamodels.impression.Beacon
import io.locally.engagesdk.datamodels.impression.ImpressionBeacon
import io.reactivex.Observable

class BeaconServices {

    companion object {

        fun getCampaign(bluetoothEnabled: Boolean, location: Location, beacon: Beacon): Observable<BeaconCampaign> {
            val impression =
                    ImpressionBeacon(
                            bluetooth  = bluetoothEnabled,
                            impressionType = beacon.type,
                            impressionProximity = beacon.proximity,
                            major = beacon.major,
                            minorDec = beacon.minorDec,
                            location = location)

            return BeaconAPI.instance.getCampaign(impressionBeacon = impression)
        }
    }
}