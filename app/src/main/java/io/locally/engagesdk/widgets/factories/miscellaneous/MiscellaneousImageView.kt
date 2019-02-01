package io.locally.engagesdk.widgets.factories.miscellaneous

import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.widget.Button
import io.locally.engagesdk.R
import io.locally.engagesdk.datamodels.campaign.CampaignContent
import io.locally.engagesdk.datamodels.campaign.CampaignContent.Content
import io.locally.engagesdk.widgets.CAMPAIGN_CONTENT
import io.locally.engagesdk.widgets.Widget
import io.locally.engagesdk.widgets.WidgetsPresenter
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.b_full_screen_image.*

class MiscellaneousImageView: Widget(){

    lateinit var content: CampaignContent
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.b_full_screen_image)
        val json = intent.getStringExtra(CAMPAIGN_CONTENT)
        content = Gson().fromJson(json, CampaignContent::class.java)
        context = applicationContext

        header.text = content.headerTitle
        Picasso.get().load(content.mediaImage.url).into(image)

        content.campaignContentButtons.isNotEmpty().apply {
            if(this){
                action.content(content.campaignContentButtons.first())
                action2.content(content.campaignContentButtons.last())
            }else {
                action.visibility = GONE
                action2.visibility = GONE
            }
        }

        image.setOnTouchListener(OnSwipeTouchListener(this))
    }

    override fun onResume() {
        super.onResume()
        WidgetsPresenter.isPresentingWidget = true
    }

    override fun onPause() {
        super.onPause()
        WidgetsPresenter.isPresentingWidget = false
    }

    private fun Button.content(contentButton: Content){
        if(contentButton.label.isNotEmpty()){
            this.text = contentButton.label

            when(contentButton.action) {
                "OPEN_URL" -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(contentButton.data)
                    this.setOnClickListener { context.startActivity(intent) }
                }
                "PHONE_CALL" -> {
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = Uri.parse("tel:${contentButton.data}")
                    this.setOnClickListener {
                        if(ActivityCompat.checkSelfPermission(context, permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            context.startActivity(intent)
                        } else println("CALL_PHONE Permission Needed")
                    }
                }
            }
        }else this.visibility = GONE
    }

    private class OnSwipeTouchListener(context: Activity): View.OnTouchListener {
        private var gestureDetector: GestureDetector

        init {
            gestureDetector = GestureDetector(context, GestureListener(context))
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            return gestureDetector.onTouchEvent(event)
        }

        private class GestureListener(val context: Activity): GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onDown(e: MotionEvent?): Boolean = true

            override fun onFling(start: MotionEvent?, end: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                val diffX: Float? = end?.x?.let { start?.x?.minus(it) }

                diffX?.let {
                    if(Math.abs(it) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                        if(it < 0) context.finish()
                    }

                    return true
                }

                return false
            }
        }
    }
}