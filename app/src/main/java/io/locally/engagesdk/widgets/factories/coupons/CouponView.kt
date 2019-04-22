package io.locally.engagesdk.widgets.factories.coupons

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Button
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import io.locally.engagesdk.R
import io.locally.engagesdk.datamodels.campaign.CampaignContent
import io.locally.engagesdk.network.services.interactions.InteractionService
import io.locally.engagesdk.widgets.CAMPAIGN_CONTENT
import io.locally.engagesdk.widgets.Widget
import io.locally.engagesdk.widgets.WidgetsPresenter
import kotlinx.android.synthetic.main.c_below_qr.*
import java.lang.Exception

class CouponView: Widget() {

    lateinit var content: CampaignContent
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_below_qr)

        val json = intent.getStringExtra(CAMPAIGN_CONTENT)
        content = Gson().fromJson(json, CampaignContent::class.java)
        context = applicationContext

        /** Placing Header title **/
        header.text = content.headerTitle

        /** Loading background image **/
        Picasso.get().load(content.mediaImage.url).into(image)

        /** Loading qr code image **/
        Picasso.get().load(content.qrImage).into(qr)

        /** Setting actions **/
        content.campaignContentButtons.isNotEmpty().apply {
            if(this){
                action.content(content.campaignContentButtons.first())
                action2.content(content.campaignContentButtons.last())
            }else {
                action.visibility = View.GONE
                action2.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        WidgetsPresenter.isPresentingWidget = true
    }

    override fun onPause() {
        super.onPause()
        WidgetsPresenter.isPresentingWidget = false
    }

    private fun Button.content(contentButton: CampaignContent.Content){
        if(contentButton.label.isNotEmpty()){
            this.text = contentButton.label
            val color = this.background as GradientDrawable
            contentButton.color.isNotEmpty().apply {
                try {
                    if(this) color.setColor(Color.parseColor(contentButton.color))
                }catch(e: Exception) { e.printStackTrace() }
            }

            when(contentButton.action) {
                "OPEN_URL" -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(contentButton.data)
                    this.setOnClickListener {
                        InteractionService.registerInteraction(content.campaign, content.impression, "OPEN_URL")

                        context.startActivity(intent)
                    }
                }
                "PHONE_CALL" -> {
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = Uri.parse("tel:${contentButton.data}")
                    this.setOnClickListener {
                        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            InteractionService.registerInteraction(content.campaign, content.impression, "PHONE_CALL")

                            context.startActivity(intent)
                        } else println("CALL_PHONE Permission Needed")
                    }
                }
            }
        }else this.visibility = View.GONE
    }
}