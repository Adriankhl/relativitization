package relativitization.universe.ai.defaults.node.self.storage

import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.ChangeResourceTargetProportionCommand
import relativitization.universe.data.components.defaults.economy.MutableResourceTargetProportionData
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.economyData
import relativitization.universe.data.serializer.DataSerializer

class BalanceResourceReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = ResourceType.values().map { resourceType ->
        ResourceQualityClass.values().map { resourceQualityClass ->
            listOf(
                BalanceResourceProportionAINode(
                    resourceType,
                    resourceQualityClass
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
                    fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                    resourceType = resourceType,
                    resourceQualityClass = resourceQualityClass,
                    resourceTargetProportionData = DataSerializer.copy(resourceTargetProportion),
                )
            )
        }
    }
}