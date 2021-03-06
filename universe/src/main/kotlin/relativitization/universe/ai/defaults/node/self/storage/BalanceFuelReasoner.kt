package relativitization.universe.ai.defaults.node.self.storage

import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.*
import relativitization.universe.data.components.defaults.physics.MutableFuelRestMassTargetProportionData
import relativitization.universe.data.components.defaults.popsystem.CarrierType
import relativitization.universe.data.components.physicsData
import relativitization.universe.data.components.popSystemData
import relativitization.universe.data.serializer.DataSerializer
import kotlin.random.Random

class BalanceFuelReasoner(random: Random) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        BalanceFuelProportionAINode(random),
    )
}

/**
 * Change the fuel target data
 */
class BalanceFuelProportionAINode(random: Random) : AINode(random) {
    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        // If there is stellar system in player, movement should be zero
        val hasStellarSystem: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.any {
                it.carrierType == CarrierType.STELLAR
            }

        val fuelRestMassTargetProportionData: MutableFuelRestMassTargetProportionData = if (hasStellarSystem) {
            MutableFuelRestMassTargetProportionData()
        } else {
            MutableFuelRestMassTargetProportionData(
                storage = 0.2,
                movement = 0.5,
                production = 0.2,
                trade = 0.1
            )
        }

        val currentFuelRestMassTargetProportionData: MutableFuelRestMassTargetProportionData = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData.physicsData().fuelRestMassTargetProportionData

        if (currentFuelRestMassTargetProportionData != fuelRestMassTargetProportionData) {
            planDataAtPlayer.addCommand(
                ChangeFuelRestMassTargetProportionCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                    fuelRestMassTargetProportionData = DataSerializer.copy(fuelRestMassTargetProportionData),
                )
            )
        }
    }
}