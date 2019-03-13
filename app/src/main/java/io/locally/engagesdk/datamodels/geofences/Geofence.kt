package io.locally.engagesdk.datamodels.geofences

import com.google.gson.annotations.SerializedName

class Geofence(@SerializedName("campaign_id") val id: Int,
               @SerializedName("coverage_type") val type: CoverageType,
               val center: Center,
               val points: List<List<Double>>? = null) {

    enum class CoverageType(private val value: String) : CharSequence by value {
        @SerializedName("radius") RADIUS("radius"),
        @SerializedName("polygon") POLYGON("polygon");

        override fun toString() = value
    }

    class Center(val lat: Double, val lng: Double, val radius: Double)
}