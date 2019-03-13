package io.locally.engagesdk.network.services.interactions

import io.locally.engagesdk.datamodels.campaign.Interaction

class InteractionService {

    companion object {
        fun registerInteraction(campaign: String?, impression: String?, action: String){
            val interaction = Interaction(campaign, impression, action)

            InteractionsAPI.instance.registerInteraction(interaction)
        }
    }
}