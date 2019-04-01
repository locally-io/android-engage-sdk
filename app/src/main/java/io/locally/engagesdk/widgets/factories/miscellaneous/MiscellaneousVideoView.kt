package io.locally.engagesdk.widgets.factories.miscellaneous

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import com.google.gson.Gson
import io.locally.engagesdk.R
import io.locally.engagesdk.datamodels.campaign.CampaignContent
import io.locally.engagesdk.widgets.CAMPAIGN_CONTENT
import io.locally.engagesdk.widgets.Widget
import io.locally.engagesdk.widgets.WidgetsPresenter
import kotlinx.android.synthetic.main.b_full_screen_video.*

class MiscellaneousVideoView: Widget() {
    lateinit var content: CampaignContent
    lateinit var context: Context
    lateinit var mediaController: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.b_full_screen_video)

        val json = intent.getStringExtra(CAMPAIGN_CONTENT)
        content = Gson().fromJson(json, CampaignContent::class.java)
        context = applicationContext

        mediaController = MediaController(this)
        mediaController.setAnchorView(video)

        video.setMediaController(mediaController)
        video.setVideoPath(content.mediaVideo.encodedFile.toString())
        video.setOnPreparedListener { progress.visibility = View.GONE }
        video.start()
    }

    override fun onResume() {
        super.onResume()
        WidgetsPresenter.isPresentingWidget = true
    }

    override fun onPause() {
        super.onPause()
        WidgetsPresenter.isPresentingWidget = false
    }
}