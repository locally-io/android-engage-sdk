package io.locally.engagesdk.datamodels.campaign

import java.io.Serializable
import java.util.Date

class CampaignContent(var id: Int,
                      val name: String,
                      val description: String,
                      val notificationMessage: String,
                      val layout: Layout,
                      val subLayout: String,
                      val checkVideo: String,
                      val checkImage: String,
                      val headerTitle: String,
                      val videoDescriptionText: String,
                      val productName: String,
                      val productDescription: String,
                      val productPrice: Double,
                      val interactionMethod: String,
                      val attributes: Attributes,
                      val campaignContentActions: Array<Content>,
                      val campaignContentButtons: Array<Content>,
                      val mediaVideo: MediaVideo,
                      val mediaImage: MediaImage): Serializable {

    enum class Layout(private val value: String): CharSequence by value {
        RETAIL("retail"),
        COUPON("coupon"),
        MISC("misc"),
        SURVEY("survey");

        override fun toString() = value
    }

    class Attributes(val message: String,
                     val submit: String,
                     val backgroundGradientTop: String,
                     val backgroundGradientBottom: String,
                     val textColor: String)

    class Content(val id: Int,
                  val campaignContentId: Int,
                  val isPrimary: Boolean,
                  val videoId: Int,
                  val action: String,
                  val data: String,
                  val color: String,
                  val label: String,
                  val created: Date,
                  val modified: Date)
}