package io.locally.engagesdk.widgets

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import io.locally.engagesdk.datamodels.campaign.CampaignContent

const val CAMPAIGN_CONTENT = "campaignContent"

typealias Widget = AppCompatActivity

interface WidgetFactory {
    fun widget(context: Context, content: CampaignContent): Intent?
}