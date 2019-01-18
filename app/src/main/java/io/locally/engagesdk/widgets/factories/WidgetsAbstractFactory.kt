package io.locally.engagesdk.widgets.factories

import android.content.Context
import android.content.Intent
import io.locally.engagesdk.datamodels.campaign.CampaignContent
import io.locally.engagesdk.datamodels.campaign.CampaignContent.Layout.*
import io.locally.engagesdk.widgets.factories.miscellaneous.MiscellaneousFactory

class WidgetsAbstractFactory {

    companion object {
        fun widget(context: Context, content: CampaignContent): Intent? {
            return when(content.layout){
                MISC -> MiscellaneousFactory.widget(context, content)
                COUPON -> null
                SURVEY -> null
                RETAIL -> null
            }
        }
    }
}