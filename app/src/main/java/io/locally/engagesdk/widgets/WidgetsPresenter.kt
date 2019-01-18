package io.locally.engagesdk.widgets

import android.content.Context
import io.locally.engagesdk.notifications.NotificationManager
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.datamodels.campaign.CampaignContent
import io.locally.engagesdk.notifications.NotificationContent
import io.locally.engagesdk.widgets.factories.WidgetsAbstractFactory
import com.google.gson.Gson

object WidgetsPresenter {
    var isPresentingWidget: Boolean = false

    fun presentWidget(context: Context, content: CampaignContent, callback: ((Boolean) -> Unit)? = null) {
        val widget = WidgetsAbstractFactory.widget(context, content)

        widget?.let {
            it.putExtra(CAMPAIGN_CONTENT, Gson().toJson(content))
            if(Utils.isForeground(context)) {
                if(isPresentingWidget) {
                    NotificationManager.sendNotification(NotificationContent(campaignContent = content), it)
                    callback?.invoke(false)
                }
                else {
                    context.startActivity(widget)
                    callback?.invoke(true)
                }
            } else {
                NotificationManager.sendNotification(NotificationContent(campaignContent = content), it)
                callback?.invoke(true)
            }
        } ?: run {
            callback?.invoke(false)
        }
    }
}