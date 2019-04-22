package io.locally.engagesdk.widgets.factories.coupons

import android.content.Context
import android.content.Intent
import io.locally.engagesdk.datamodels.campaign.CampaignContent
import io.locally.engagesdk.widgets.WidgetFactory
import io.locally.engagesdk.widgets.factories.coupons.CouponsFactory.CouponsLayout.C_BELOW_QR

object CouponsFactory: WidgetFactory {

    private enum class CouponsLayout {
        C_BELOW_QR
    }

    override fun widget(context: Context, content: CampaignContent): Intent? {
        return when(CouponsLayout.valueOf(content.subLayout.toUpperCase())) {
            C_BELOW_QR -> Intent(context, CouponView::class.java)
        }
    }
}