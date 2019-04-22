package io.locally.engagesdk.datamodels.geofences

import com.google.gson.annotations.SerializedName

class Geofence(@SerializedName("geofence_id") val id: Int,
               @SerializedName("coverage_type") val type: CoverageType,
               val center: Center,
               val points: List<Point>? = null) {

    enum class CoverageType(private val value: String) : CharSequence by value {
        @SerializedName("radius") RADIUS("radius"),
        @SerializedName("polygon") POLYGON("polygon");

        override fun toString() = value
    }

    class Center(val lat: Double, val lng: Double, val radius: Double)
    class Point(val lat: Double, val lng: Double)
}