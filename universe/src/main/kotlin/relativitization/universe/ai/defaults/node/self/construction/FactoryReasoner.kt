package relativitization.universe.ai.defaults.node.self.construction

import relativitization.universe.ai.defaults.consideration.building.NoFuelFactoryAndNoStarConsideration
import relativitization.universe.ai.defaults.consideration.building.NoResourceFactoryAndHasStarConsideration
import relativitization.universe.ai.defaults.consideration.building.NoResourceFactoryConsideration
import relativitization.universe.ai.defaults.consideration.building.OneFuelFactoryConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.BuildForeignFuelFactoryCommand
import relativitization.universe.data.commands.BuildForeignResourceFactoryCommand
import relativitization.universe.data.commands.RemoveLocalFuelFactoryCommand
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.MutableFuelFactoryInternalData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.MutableResourceFactoryInternalData
import relativitization.universe.data.serializer.DataSerializer
import kotlin.math.min

class FactoryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val fuelReasonerList: List<AINode> = listOf(
            NewFuelFactoryReasoner(),
            RemoveFuelFactoryReasoner(),
        )

        val resourceReasonerList: List<AINode> =
            (ResourceType.values().toList() - ResourceType.ENTERTAINMENT).map {
                NewResourceFactoryReasoner(it)
            }

        return fuelReasonerList + resourceReasonerList
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
        return listOf(
            BuildNewFuelFactoryOption(carrierId),
            DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0),
        )
    }
}

/**
 * Option to build a new fuel factory
 */
class BuildNewFuelFactoryOption(
    val carrierId: Int
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        // Build fuel factory if no fuel factory and no star
        val noFuelFactoryAndNoStarConsiderationList: List<NoFuelFactoryAndNoStarConsideration> =
            listOf(
                NoFuelFactoryAndNoStarConsideration(
                    rankIfTrue = 5,
                    multiplierIfTrue = 1.0,
                    bonusIfTrue = 1.0
                )
            )

        // Prioritize resource factory if no resource factory and has star
        val resourceFactoryAndHasStarConsiderationList: List<NoResourceFactoryAndHasStarConsideration> =
            ResourceType.values().map {
                NoResourceFactoryAndHasStarConsideration(
                    it,
                    rankIfTrue = 0,
                    multiplierIfTrue = 0.0,
                    bonusIfTrue = 0.0
                )
            }

        return noFuelFactoryAndNoStarConsiderationList + resourceFactoryAndHasStarConsiderationList
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val idealFactory: MutableFuelFactoryInternalData = planDataAtPlayer
            .getCurrentMutablePlayerData()
            .playerInternalData
            .playerScienceData()
            .playerScienceApplicationData
            .idealFuelFactory

        val fuelNeededPerBuilding: Double = planDataAtPlayer
            .getCurrentMutablePlayerData()
            .playerInternalData
            .playerScienceData()
            .playerScienceApplicationData
            .newFuelFactoryFuelNeededByConstruction()

        val fuelAvailable: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.physicsData().fuelRestMassData.production


        // Don't use all the fuel
        val maxUsableFuel: Double = fuelAvailable * 0.1

        val numLabourer: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId).allPopData
            .labourerPopData.commonPopData.adultPopulation

        // Multiply by 5 to consider pop growth
        val targetNumLabourer: Double = numLabourer * 5.0

        // Compute the numBuilding by considering the available fuel and number of labourer
        val fuelFraction: Double = maxUsableFuel / fuelNeededPerBuilding
        val labourerFraction: Double = targetNumLabourer / idealFactory.maxNumEmployee
        val numBuilding: Double = min(fuelFraction, labourerFraction)

        planDataAtPlayer.addCommand(
            BuildForeignFuelFactoryCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                senderTopLeaderId = planDataAtPlayer.getCurrentMutablePlayerData().topLeaderId(),
                targetCarrierId = carrierId,
                ownerId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fuelFactoryInternalData = DataSerializer.copy(idealFactory),
                storedFuelRestMass = 0.0,
                numBuilding = numBuilding,
            )
        )
    }
}

/**
 * Remove fuel factory reasoner
 */
class RemoveFuelFactoryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.popSystemData()
            .carrierDataMap.map { (carrierId, carrierData) ->
                carrierData.allPopData.labourerPopData.fuelFactoryMap.map { (fuelFactoryId, _) ->
                    RemoveSpecificFuelFactoryReasoner(carrierId, fuelFactoryId)
                }
            }.flatten()
    }
}

/**
 * Remove a specific fuel factory
 */
class RemoveSpecificFuelFactoryReasoner(
    val carrierId: Int,
    val fuelFactoryId: Int,
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        return listOf(
            RemoveSpecificFuelFactoryOption(carrierId, fuelFactoryId),
            DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0),
        )
    }
}

/**
 * Dual utility option to remove a specific fuel factory
 */
class RemoveSpecificFuelFactoryOption(
    val carrierId: Int,
    val fuelFactoryId: Int,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> = listOf(
        OneFuelFactoryConsideration(rankIfTrue = 0, multiplierIfTrue = 0.0, bonusIfTrue = 0.0)
    )

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        planDataAtPlayer.addCommand(
            RemoveLocalFuelFactoryCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                targetCarrierId = carrierId,
                targetFuelFactoryId = fuelFactoryId,
            )
        )
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
        NoResourceFactoryConsideration(
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