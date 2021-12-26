package relativitization.universe.ai.defaults.node.self.construction

import relativitization.universe.ai.defaults.consideration.building.NoResourceFactoryAtPlayerConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.BuildForeignResourceFactoryCommand
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.ResourceFactoryInternalData

class FactoryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = ResourceType.values().map {
        NewResourceFactoryReasoner(it)
    }
}

/**
 * Iterate all carrier to consider building new factory
 */
class NewResourceFactoryReasoner(
    val resourceType: ResourceType
) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
        .popSystemData().carrierDataMap.keys.map {
            NewResourceFactoryAtCarrierReasoner(
                resourceType,
                it
            )
        }
}

/**
 * Consider building a resource factory at a carrier
 */
class NewResourceFactoryAtCarrierReasoner(
    val resourceType: ResourceType,
    val carrierId: Int,
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        BuildNewResourceFactoryOption(resourceType, carrierId),
        DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0),
    )
}

class BuildNewResourceFactoryOption(
    val resourceType: ResourceType,
    val carrierId: Int,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> = listOf(
        NoResourceFactoryAtPlayerConsideration(
            resourceType = resourceType,
            rankIfTrue = 5,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 1.0
        )
    )

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val buildForeignResourceFactoryCommand = BuildForeignResourceFactoryCommand(
            toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
            fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
            fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
            senderTopLeaderId = planDataAtPlayer.getCurrentMutablePlayerData().topLeaderId(),
            targetCarrierId = carrierId,
            ownerId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
            resourceFactoryInternalData = ResourceFactoryInternalData(),
            qualityLevel = 0.0,
            storedFuelRestMass = 0.0,
            numBuilding = 1.0,
        )
    }
}