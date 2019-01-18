package io.locally.engagesdk.widgets.factories.miscellaneous

import android.content.Context
import android.content.Intent
import io.locally.engagesdk.datamodels.campaign.CampaignContent
import io.locally.engagesdk.widgets.WidgetFactory
import io.locally.engagesdk.widgets.factories.miscellaneous.MiscellaneousFactory.MiscellaneousLayout.B_FULL_SCREEN_IMAGE

object MiscellaneousFactory: WidgetFactory {

    private enum class MiscellaneousLayout{
        B_FULL_SCREEN_IMAGE,
        B_FULL_SCREEN_VIDEO,
        B_VIDEO_WITH_IMAGE;
    }

    override fun widget(context: Context, content: CampaignContent): Intent? {
        return when(MiscellaneousLayout.valueOf(content.subLayout.toUpperCase())){
            B_FULL_SCREEN_IMAGE -> Intent(context, MiscellaneousImageView::class.java)
            else -> null
        }
    }
}