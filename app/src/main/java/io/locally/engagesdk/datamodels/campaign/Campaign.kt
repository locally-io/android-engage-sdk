package io.locally.engagesdk.datamodels.campaign

class Campaign(val data: Data? = null) {
    class Data(val id: Int,
               val campaignContentTouchId: Int,
               val campaignContentNearId: Int,
               val campaignContentFarId: Int,
               val campaignContent: CampaignContent?,
               val mediaCdn: String,
               val impressionId: Int)
}