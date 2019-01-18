package io.locally.engagesdk.datamodels.impression

import com.google.gson.annotations.SerializedName

data class Demographics(val age: Int, val gender: Gender) {
    enum class Gender(private val value: String) : CharSequence by value {
        @SerializedName("male") MALE("male"),
        @SerializedName("female") FEMALE("female"),
        @SerializedName("other") OTHER("other");

        override fun toString() = value
    }
}