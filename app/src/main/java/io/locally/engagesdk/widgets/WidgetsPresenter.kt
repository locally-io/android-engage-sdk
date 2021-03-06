package io.locally.engagesdk.widgets

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.datamodels.campaign.CampaignContent
import io.locally.engagesdk.datamodels.campaign.CampaignContent.Layout.PUSH_NOTIFICATION
import io.locally.engagesdk.notifications.NotificationContent
import io.locally.engagesdk.notifications.NotificationManager
import io.locally.engagesdk.widgets.factories.WidgetsAbstractFactory

object WidgetsPresenter {
    var isPresentingWidget: Boolean = false

    fun presentWidget(context: Context, content: CampaignContent?, callback: ((Boolean) -> Unit)? = null) {
        content?.let { campaign ->
            when(campaign.layout) {
                PUSH_NOTIFICATION -> {
                    NotificationManager.sendCampaignNotification(context, content)
                    callback?.invoke(true)
                }
                else -> {
                    val widget = WidgetsAbstractFactory.widget(context, campaign)

                    widget?.let {
                        it.putExtra(CAMPAIGN_CONTENT, Gson().toJson(campaign))
                        if(Utils.isForeground(context)) {
                            if(isPresentingWidget) {
                                NotificationManager.sendNotification(context, NotificationContent(campaign), it)
                                callback?.invoke(true)
                            } else {
                                context.startActivity(it)
                                isPresentingWidget = true
                                callback?.invoke(true)
                            }
                        } else {
                            NotificationManager.sendNotification(context, NotificationContent(campaign), it)
                            callback?.invoke(true)
                        }
                    } ?: run {
                        callback?.invoke(false)
                    }
                }
            }
        } ?: callback?.invoke(false)
    }

    fun fromContent(context: Context, content: CampaignContent?): Intent? {
        return WidgetsAbstractFactory.widget(context, content!!).apply {
            this?.putExtra(CAMPAIGN_CONTENT, Gson().toJson(content))
        }
    }
}