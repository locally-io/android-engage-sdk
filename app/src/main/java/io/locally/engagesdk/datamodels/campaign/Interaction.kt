package io.locally.engagesdk.datamodels.campaign

import com.google.gson.annotations.SerializedName
import io.locally.engagesdk.common.Utils

class Interaction(@SerializedName("campaign_id") val campaign: String?,
                  @SerializedName("impression_id") val impression: String?,
                  val action: String) {
    @SerializedName("device_id") val device = Utils.deviceId?.replace("-", "")
    val timestamp = Utils.formatedDate()
}