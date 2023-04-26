package relativitization.universe.game.ai.defaults.node.self.storage

import relativitization.universe.game.ai.defaults.utils.AINode
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.game.ai.defaults.utils.SequenceReasoner
import relativitization.universe.core.data.PlanDataAtPlayer
import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.game.data.commands.ChangeResourceTargetProportionCommand
import relativitization.universe.game.data.components.defaults.economy.MutableResourceTargetProportionData
import relativitization.universe.game.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.economy.getResourceTargetProportionData
import relativitization.universe.game.data.components.economyData

class BalanceResourceReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = ResourceType.values().map { resourceType ->
        ResourceQualityClass.values().map { resourceQualityClass ->
            listOf(
                BalanceResourceProportionAINode(
                    resourceType,
                    resourceQualityClass,
                )
            )
        }
    }.flatten().flatten()
}

/**
 * Change the resource target data
 */
class BalanceResourceProportionAINode(
    val resourceType: ResourceType,
    val resourceQualityClass: ResourceQualityClass,
) : AINode() {
    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val resourceTargetProportion: MutableResourceTargetProportionData =
            if (ResourceType.isProductionResource(resourceType)) {
                MutableResourceTargetProportionData(
                    storage = 0.25,
                    production = 0.5,
                    trade = 0.25
                )
            } else {
                MutableResourceTargetProportionData(
                    storage = 0.5,
                    production = 0.0,
                    trade = 0.5
                )
            }

        val currentResourceTargetProportion: MutableResourceTargetProportionData =
            planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.economyData().resourceData
                .getResourceTargetProportionData(resourceType, resourceQualityClass)

        if (currentResourceTargetProportion != resourceTargetProportion) {
            planDataAtPlayer.addCommand(
                ChangeResourceTargetProportionCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    resourceType = resourceType,
                    resourceQualityClass = resourceQualityClass,
                    resourceTargetProportionData = DataSerializer.copy(resourceTargetProportion),
                )
            )
        }
    }
}