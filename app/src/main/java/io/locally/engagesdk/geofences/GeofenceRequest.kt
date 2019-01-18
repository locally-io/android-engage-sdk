package io.locally.engagesdk.geofences

import android.location.Location
import io.locally.engagesdk.datamodels.impression.ImpressionType
import io.locally.engagesdk.datamodels.impression.Proximity

class GeofenceRequest(val type: ImpressionType,
                      val proximity: Proximity,
                      val location: Location)