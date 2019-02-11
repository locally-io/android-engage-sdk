package io.locally.engagesdk.datamodels.campaign

class BeaconCampaign(val data: Data? = null) {
    class Data(val id: Int = 0,
               val campaignContent: CampaignContent? = null,
               val mediaCdn: String? = null,
               val impressionId: Int = 0)
}