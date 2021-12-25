package relativitization.universe.ai.defaults.node.resource

import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.defaults.popsystem.CarrierType

class BalanceFuelAndResourceReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        BalanceFuelDataAINode()
    )
}

/**
 * Transfer from storage to other categories
 */
class BalanceFuelDataAINode : AINode {
    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val storage: Double = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .physicsData().fuelRestMassData.storage
        val movement: Double = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .physicsData().fuelRestMassData.movement
        val production: Double = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .physicsData().fuelRestMassData.production
        val trade: Double = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .physicsData().fuelRestMassData.trade
        val total: Double = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .physicsData().fuelRestMassData.total()

        // Try to distribute resource evenly
        // If there is stellar system in player, don't transfer to movement
        val hasStellarSystem: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.any {
                it.carrierType == CarrierType.STELLAR
            }

        // To compute the fraction target
        val storageWeight: Double = 1.0
        val movementWeight: Double = if (hasStellarSystem) {
            0.0
        } else {
            1.0
        }
        val productionWeight: Double = 1.0
        val tradeWeight: Double = 1.0
        val totalWeight: Double = storageWeight + movementWeight + productionWeight + tradeWeight

        val storageFraction: Double = storageWeight / totalWeight
        val movementFraction: Double = movementWeight / totalWeight
        val productionFraction: Double = productionWeight / totalWeight
        val tradeFraction: Double = tradeWeight / totalWeight


    }
}