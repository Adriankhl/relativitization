package relativitization.universe.game.ai.defaults.node.self.storage

import relativitization.universe.game.ai.defaults.utils.AINode
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.game.ai.defaults.utils.SequenceReasoner
import relativitization.universe.game.data.PlanDataAtPlayer
import relativitization.universe.game.data.commands.ChangeFuelRestMassTargetProportionCommand
import relativitization.universe.game.data.components.defaults.physics.MutableFuelRestMassTargetProportionData
import relativitization.universe.game.data.components.defaults.popsystem.CarrierType
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.game.data.serializer.DataSerializer

class BalanceFuelReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        BalanceFuelProportionAINode(),
    )
}

/**
 * Change the fuel target data
 */
class BalanceFuelProportionAINode : AINode() {
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
                    fuelRestMassTargetProportionData = DataSerializer.copy(fuelRestMassTargetProportionData),
                )
            )
        }
    }
}