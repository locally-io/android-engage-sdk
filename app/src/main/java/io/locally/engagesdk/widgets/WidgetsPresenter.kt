package io.locally.engagesdk.widgets

import android.content.Context
import io.locally.engagesdk.notifications.NotificationManager
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.datamodels.campaign.CampaignContent
import io.locally.engagesdk.notifications.NotificationContent
import io.locally.engagesdk.widgets.factories.WidgetsAbstractFactory
import com.google.gson.Gson
import io.locally.engagesdk.EngageSDK

object WidgetsPresenter {
    var isPresentingWidget: Boolean = false
    var campaignListener: EngageSDK.CampaignListener? = null

    fun presentWidget(context: Context, content: CampaignContent, callback: ((Boolean) -> Unit)? = null) {
        val widget = WidgetsAbstractFactory.widget(context, content)

        widget?.let {
            it.putExtra(CAMPAIGN_CONTENT, Gson().toJson(content))
            if(Utils.isForeground(context)) {
                if(isPresentingWidget) {
                    NotificationManager.sendNotification(NotificationContent(campaignContent = content), it)
                    callback?.invoke(false)
                }
                else callback?.invoke(true)
            } else {
                NotificationManager.sendNotification(NotificationContent(campaignContent = content), it)
                callback?.invoke(true)
            }

            campaignListener?.didCampaignArrived(it)
        } ?: run {
            callback?.invoke(false)
        }
    }
}