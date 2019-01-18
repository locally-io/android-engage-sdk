package io.locally.engagesdk.network.services.campaign

import android.location.Location
import io.locally.engagesdk.datamodels.impression.Beacon
import io.locally.engagesdk.datamodels.impression.ImpressionBeacon
import io.locally.engagesdk.datamodels.campaign.Campaign
import io.reactivex.Observable

class CampaignServices {

    companion object {

        fun getCampaign(bluetoothEnabled: Boolean, location: Location, beacon: Beacon): Observable<Campaign> {
            val impression =
                    ImpressionBeacon(
                            bluetooth  = bluetoothEnabled,
                            impressionType = beacon.type,
                            impressionProximity = beacon.proximity,
                            major = beacon.major,
                            minorDec = beacon.minorDec,
                            location = location)

            return CampaignAPI.instance.getCampaign(impressionBeacon = impression)
        }
    }
}