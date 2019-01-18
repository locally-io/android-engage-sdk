package io.locally.engagesdk.datamodels.impression

import com.google.gson.annotations.SerializedName

enum class ImpressionType(private val value: String): CharSequence by value {
    @SerializedName("beacon") BEACON("beacon"),
    @SerializedName("geofence") GEOFENCE("geofence");

    override fun toString() = value
}