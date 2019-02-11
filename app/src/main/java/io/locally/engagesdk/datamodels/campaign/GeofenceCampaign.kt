package io.locally.engagesdk.datamodels.campaign

class GeofenceCampaign(val data: Data? = null) {
    class Data(val impressionId: Int = 0, val campaigns: List<Campaign> = arrayListOf())
    class Campaign(val id: Int,
                   val campaignContent: CampaignContent?,
                   val mediaCdn: String,
                   val impressionId: Int)
}