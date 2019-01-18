package io.locally.engagesdk.notifications

import io.locally.engagesdk.datamodels.campaign.CampaignContent

data class NotificationContent(val campaignContent: CampaignContent? = null, val remoteContent: RemoteContent? = null){
    class RemoteContent(val title: String? = "", val link: String? = "")
}