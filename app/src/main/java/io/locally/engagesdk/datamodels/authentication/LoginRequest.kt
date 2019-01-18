package io.locally.engagesdk.datamodels.authentication

import com.google.gson.annotations.SerializedName

class LoginRequest(val username: String, val password: String, @SerializedName("device_id") val deviceId: String)