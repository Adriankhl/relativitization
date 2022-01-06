package relativitization.universe.ai.defaults.node.self.storage

import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.ChangeStorageResourceTargetCommand
import relativitization.universe.data.commands.TransferResourceToProductionCommand
import relativitization.universe.data.commands.TransferResourceToTradeCommand
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.economy.ResourceType

class BalanceResourceReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = ResourceType.values().map { resourceType ->
        ResourceQualityClass.values().map { resourceQualityClass ->
            listOf(
                BalanceResourceDataAINode(
                    resourceType,
                    resourceQualityClass
                ),
                BalanceResourceTargetDataAINode(
                    resourceType,
                    resourceQualityClass
                )
            )
        }
    }.flatten().flatten()
}

/**
 * Transfer from storage to other resource usage categories
 *
 * @param resourceType the type of the resource
 * @param resourceQualityClass the quality class of the resource
 */
class BalanceResourceDataAINode(
    private val resourceType: ResourceType,
    private val resourceQualityClass: ResourceQualityClass,
) : AINode {
    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val storage: Double = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .economyData().resourceData.getStorageResourceAmount(
                resourceType,
                resourceQualityClass
            )
        val production: Double = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .economyData().resourceData.getProductionResourceAmount(
                resourceType,
                resourceQualityClass
            )
        val trade: Double = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .economyData().resourceData.getTradeResourceAmount(
                resourceType,
                resourceQualityClass
            )
        val totalResource: Double = storage + production + trade


        // To compute the fraction target
        val storageWeight: Double = 1.0
        val productionWeight: Double = 2.0
        val tradeWeight: Double = 1.0
        val totalWeight: Double = storageWeight + productionWeight + tradeWeight

        val storageFraction: Double = storageWeight / totalWeight
        val productionFraction: Double = productionWeight / totalWeight
        val tradeFraction: Double = tradeWeight / totalWeight

        // Only balance the resource if storage is sufficient
        val isStorageEnough: Boolean = if (totalResource > 0.0) {
            (storage / totalResource) >= storageFraction
        } else {
            false
        }

        if (isStorageEnough) {
            val availableStorage: Double = storage - totalResource * storageFraction

            // Calculate which category is lacking
            val productionLack: Double = if ((production / totalResource) < productionFraction) {
                productionFraction * totalResource - production
            } else {
                0.0
            }
            val tradeLack: Double = if ((trade / totalResource) < tradeFraction) {
                tradeFraction * totalResource - trade
            } else {
                0.0
            }
            val totalLack: Double = productionLack + tradeLack

            if (productionLack > 0.0) {
                planDataAtPlayer.addCommand(
                    TransferResourceToProductionCommand(
                        toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                        fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                        fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                        resourceType = resourceType,
                        resourceQualityClass = resourceQualityClass,
                        amount = availableStorage * productionLack / totalLack,
                    )
                )
            }

            if (tradeLack > 0.0) {
                planDataAtPlayer.addCommand(
                    TransferResourceToTradeCommand(
                        toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                        fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                        fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                        resourceType = resourceType,
                        resourceQualityClass = resourceQualityClass,
                        amount = availableStorage * tradeLack / totalLack,
                    )
                )
            }
        }
    }
}

/**
 * Change the resource target data
 */
class BalanceResourceTargetDataAINode(
    val resourceType: ResourceType,
    val resourceQualityClass: ResourceQualityClass,
) : AINode {
    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        // Set the target storage to a high value, which means everything is put into the storage
        // Do the transfer "manually" by AI
        val currentTargetStorage: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.economyData().resourceData.getResourceTargetAmountData(
                resourceType,
                resourceQualityClass
            ).storage

        if (currentTargetStorage < 1E100) {
            planDataAtPlayer.addCommand(
                ChangeStorageResourceTargetCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                    resourceType = resourceType,
                    resourceQualityClass = resourceQualityClass,
                    targetAmount = 1E100,
                )
            )
        }
    }
}