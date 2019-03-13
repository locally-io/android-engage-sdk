package io.locally.engagesdk.datamodels.impression

import com.google.gson.annotations.SerializedName

enum class Proximity(private val value: String): CharSequence by value {
    @SerializedName("far") FAR("far"),
    @SerializedName("near") NEAR("near"),
    @SerializedName("touch") TOUCH("touch"),
    @SerializedName("enter") ENTER("enter"),
    @SerializedName("exit") EXIT("exit");

    override fun toString() = value
}