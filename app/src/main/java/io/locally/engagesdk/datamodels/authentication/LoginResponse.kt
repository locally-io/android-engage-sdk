package io.locally.engagesdk.datamodels.authentication

import com.google.gson.annotations.SerializedName

class LoginResponse(val data: Data? = null) {
    class Data(val token: String = "", val refresh: String = "", @SerializedName("app_guid") val guid: String, @SerializedName("kontakt_api_key") val kontaktKey: String, val aws: AWS)
    class AWS(val sns: String = "", @SerializedName("android_arn") val arn: String = "", @SerializedName("identity_pool_id") val pool: String)
}