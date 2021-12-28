package relativitization.universe.ai.defaults.node.self.construction

import relativitization.universe.ai.defaults.consideration.building.NoResourceFactoryAtPlayerConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.BuildForeignResourceFactoryCommand
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.MutableResourceFactoryInternalData
import relativitization.universe.data.serializer.DataSerializer
import kotlin.math.min

class FactoryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val fuelReasoner: AINode = NewFuelFactoryReasoner()

        val resourceReasonerList: List<AINode> =
            (ResourceType.values().toList() - ResourceType.ENTERTAINMENT).map {
                NewResourceFactoryReasoner(it)
            }

        return listOf(
            fuelReasoner
        ) + resourceReasonerList
    }
}

/**
 * Iterate all carrier to consider building new fuel factory
 */
class NewFuelFactoryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
        .popSystemData().carrierDataMap.keys.map {
            NewFuelFactoryAtCarrierReasoner(it)
        }
}

/**
 * Consider building a fuel factory at a carrier
 */
class NewFuelFactoryAtCarrierReasoner(
    val carrierId: Int
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        TODO("Not yet implemented")
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
        val idealFactory: MutableResourceFactoryInternalData = planDataAtPlayer
            .getCurrentMutablePlayerData()
            .playerInternalData.playerScienceData()
            .playerScienceApplicationData.newResourceFactoryInternalData(
                resourceType,
                1.0
            )

        val fuelNeededPerBuilding: Double = planDataAtPlayer
            .getCurrentMutablePlayerData()
            .playerInternalData.playerScienceData()
            .playerScienceApplicationData.newResourceFactoryFuelNeededByConstruction(
                resourceType,
                1.0
            )

        val fuelAvailable: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.physicsData().fuelRestMassData.production

        // Don't use all the fuel
        val maxUsableFuel: Double = fuelAvailable * 0.1

        val numLabourer: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId).allPopData
            .labourerPopData.commonPopData.adultPopulation

        // Multiply by 10 to consider pop growth
        val targetNumLabourerPerResource: Double = numLabourer / ResourceType.values().size * 10.0

        // Compute the numBuilding by considering the available fuel and number of labourer
        val fuelFraction: Double = maxUsableFuel / fuelNeededPerBuilding
        val labourerFraction: Double = targetNumLabourerPerResource / idealFactory.maxNumEmployee
        val numBuilding: Double = min(fuelFraction, labourerFraction)


        planDataAtPlayer.addCommand(
            BuildForeignResourceFactoryCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                senderTopLeaderId = planDataAtPlayer.getCurrentMutablePlayerData().topLeaderId(),
                targetCarrierId = carrierId,
                ownerId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                resourceFactoryInternalData = DataSerializer.copy(idealFactory),
                qualityLevel = 1.0,
                storedFuelRestMass = 0.0,
                numBuilding = numBuilding,
            )
        )
    }
}