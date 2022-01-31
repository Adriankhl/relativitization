package relativitization.universe.ai.defaults.node.self.storage

import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.*
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.physics.MutableTargetFuelRestMassProportionData
import relativitization.universe.data.components.defaults.physics.TargetFuelRestMassProportionData
import relativitization.universe.data.components.defaults.popsystem.CarrierType
import relativitization.universe.data.serializer.DataSerializer

class BalanceFuelReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        BalanceFuelTargetDataAINode(),
    )
}

/**
 * Change the fuel target data
 */
class BalanceFuelTargetDataAINode : AINode() {
    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        // If there is stellar system in player, movement should be zero
        val hasStellarSystem: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.any {
                it.carrierType == CarrierType.STELLAR
            }

        val targetFuelRestMassProportionData: MutableTargetFuelRestMassProportionData = if (hasStellarSystem) {
            MutableTargetFuelRestMassProportionData()
        } else {
            MutableTargetFuelRestMassProportionData(
                storage = 0.2,
                movement = 0.5,
                production = 0.2,
                trade = 0.1
            )
        }

        val currentTargetFuelRestMassProportionData: MutableTargetFuelRestMassProportionData = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData.physicsData().targetFuelRestMassProportionData

        if (currentTargetFuelRestMassProportionData != targetFuelRestMassProportionData) {
            planDataAtPlayer.addCommand(
                ChangeTargetFuelRestMassProportionCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                    targetFuelRestMassProportionData = DataSerializer.copy(targetFuelRestMassProportionData),
                )
            )
        }
    }
}