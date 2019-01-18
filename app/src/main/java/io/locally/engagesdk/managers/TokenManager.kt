package io.locally.engagesdk.managers

import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.datamodels.authentication.LoginResponse
import java.util.*

object TokenManager {

    private var calendar: Calendar = Calendar.getInstance()

    fun accessToken(data: LoginResponse.Data) {
        Utils.token = data.token
        Utils.refresh = data.refresh
        Utils.guid = data.guid
        Utils.kontaktKey = data.kontaktKey
        Utils.poolId = data.aws.pool
        Utils.sns = data.aws.sns
        Utils.arn = data.aws.arn

        calendar.apply {
            time = Date()
            add(Calendar.DATE, 7)
        }

        Utils.refreshDate = calendar.time
    }

    val isTokenValid: Boolean
        get() {
            return Utils.refreshDate?.let {
                Date().before(it)
            } ?: false
        }

    fun invalidateToken() {
        Utils.token = null
        Utils.refresh = null
        Utils.refreshDate = null
    }
}