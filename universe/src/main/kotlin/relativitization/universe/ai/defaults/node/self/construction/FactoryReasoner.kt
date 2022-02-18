package relativitization.universe.ai.defaults.node.self.construction

import relativitization.universe.ai.defaults.consideration.building.*
import relativitization.universe.ai.defaults.consideration.fuel.IncreasingProductionFuelConsideration
import relativitization.universe.ai.defaults.consideration.fuel.SufficientProductionFuelConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.BuildForeignFuelFactoryCommand
import relativitization.universe.data.commands.BuildForeignResourceFactoryCommand
import relativitization.universe.data.commands.RemoveLocalFuelFactoryCommand
import relativitization.universe.data.commands.RemoveLocalResourceFactoryCommand
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.MutableFuelFactoryInternalData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.MutableResourceFactoryInternalData
import relativitization.universe.data.components.physicsData
import relativitization.universe.data.components.playerScienceData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.maths.random.Rand
import kotlin.math.max
import kotlin.math.min

class FactoryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val fuelReasonerList: List<AINode> = listOf(
            RemoveFuelFactoryReasoner(),
            NewFuelFactoryReasoner(),
        )

        val resourceReasonerList: List<AINode> =
            ResourceType.factoryResourceList.map {
                listOf(
                    RemoveResourceFactoryReasoner(it),
                    NewResourceFactoryReasoner(it),
                )
            }.flatten()

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
    private val carrierId: Int
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
    private val carrierId: Int
) : DualUtilityOption() {
    // Max production fuel fraction used to build the factory
    private val maxProductionFuelFraction: Double = 0.1

    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        // Build fuel factory if no fuel factory and no star
        val noSelfFuelFactoryAndNoStarConsideration = NoSelfFuelFactoryAndNoStarConsideration(
            rankIfTrue = 5,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 1.0
        )

        // Prioritize resource factory if no resource factory and has star
        val noSelfResourceFactoryAndHasStarConsiderationList: List<NoSelfResourceFactoryAndHasStarConsideration> =
            ResourceType.factoryResourceList.map {
                NoSelfResourceFactoryAndHasStarConsideration(
                    it,
                    rankIfTrue = 0,
                    multiplierIfTrue = 0.0,
                    bonusIfTrue = 0.0
                )
            }

        val sufficientSelfFuelFactoryAtCarrierConsideration =
            SufficientSelfFuelFactoryAtCarrierConsideration(
                carrierId = carrierId,
                rankIfTrue = 0,
                multiplierIfTrue = 0.1,
                bonusIfTrue = 1.0,
                rankIfFalse = 1,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 1.0
            )

        val minFuelNeeded: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.playerScienceData().playerScienceApplicationData
            .newFuelFactoryFuelNeededByConstruction(1.0)
        val sufficientProductionFuelConsideration = SufficientProductionFuelConsideration(
            requiredProductionFuelRestMass = minFuelNeeded / maxProductionFuelFraction,
            rankIfTrue = 0,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 0.0,
            bonusIfFalse = 0.0
        )

        val tooManySelfFuelFactoryAtCarrierConsideration =
            TooManySelfFuelFactoryAtCarrierConsideration(
                carrierId = carrierId,
                rankIfTrue = 0,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 0.0,
                rankIfFalse = 0,
                multiplierIfFalse = 0.1,
                bonusIfFalse = 0.0
            )

        return listOf(noSelfFuelFactoryAndNoStarConsideration) +
                noSelfResourceFactoryAndHasStarConsiderationList +
                listOf(
                    sufficientSelfFuelFactoryAtCarrierConsideration,
                    sufficientProductionFuelConsideration,
                    tooManySelfFuelFactoryAtCarrierConsideration,
                )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val idealFactory: MutableFuelFactoryInternalData = planDataAtPlayer
            .getCurrentMutablePlayerData()
            .playerInternalData
            .playerScienceData()
            .playerScienceApplicationData
            .idealFuelFactory

        val fuelNeededPerEmployee: Double = planDataAtPlayer
            .getCurrentMutablePlayerData()
            .playerInternalData
            .playerScienceData()
            .playerScienceApplicationData
            .newFuelFactoryFuelNeededByConstruction(1.0)

        val fuelAvailable: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.physicsData().fuelRestMassData.production

        // Don't use all the fuel
        val maxUsableFuel: Double = fuelAvailable * maxProductionFuelFraction

        val numLabourer: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId).allPopData
            .labourerPopData.commonPopData.adultPopulation

        val maxNumEmployee: Double = max(
            min(
                maxUsableFuel / fuelNeededPerEmployee,
                numLabourer * 0.5,
            ),
            1.0
        )

        planDataAtPlayer.addCommand(
            BuildForeignFuelFactoryCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                senderTopLeaderId = planDataAtPlayer.getCurrentMutablePlayerData().topLeaderId(),
                targetCarrierId = carrierId,
                ownerId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fuelFactoryInternalData = DataSerializer.copy(idealFactory),
                maxNumEmployee = maxNumEmployee,
                storedFuelRestMass = 0.0,
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
        val removeSelfFuelFactoryList: List<AINode> =
            planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.popSystemData()
                .carrierDataMap.map { (carrierId, carrierData) ->
                    // Only consider self fuel factory
                    carrierData.allPopData.labourerPopData.fuelFactoryMap.filter { (_, fuelFactory) ->
                        fuelFactory.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
                    }.keys.shuffled(Rand.rand()).map { fuelFactoryId ->
                        RemoveSpecificSelfFuelFactoryReasoner(carrierId, fuelFactoryId)
                    }
                }.flatten()

        return removeSelfFuelFactoryList
    }
}

/**
 * Remove a specific fuel factory
 */
class RemoveSpecificSelfFuelFactoryReasoner(
    private val carrierId: Int,
    private val fuelFactoryId: Int,
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        return listOf(
            RemoveSpecificSelfFuelFactoryOption(carrierId, fuelFactoryId),
            DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0),
        )
    }
}

/**
 * Dual utility option to remove a specific fuel factory
 */
class RemoveSpecificSelfFuelFactoryOption(
    private val carrierId: Int,
    private val fuelFactoryId: Int,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> = listOf(
        SufficientFuelFactoryAtCarrierAfterRemoveConsideration(
            carrierId = carrierId,
            fuelFactoryId = fuelFactoryId,
            rankIfTrue = 1,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 1.0,
            rankIfFalse = 0,
            multiplierIfFalse = 0.0,
            bonusIfFalse = 0.0
        ),
        OutdatedFuelFactoryConsideration(
            carrierId = carrierId,
            fuelFactoryId = fuelFactoryId,
            rankIfTrue = 1,
            multiplierIfTrue = 1.0
        ),
        IncreasingProductionFuelConsideration(
            rankIfTrue = 0,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 0.1,
            rankIfFalse = 0,
            multiplierIfFalse = 0.1,
            bonusIfFalse = 0.0
        )
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
    private val resourceType: ResourceType
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
    private val resourceType: ResourceType,
    private val carrierId: Int,
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
    private val resourceType: ResourceType,
    private val carrierId: Int,
) : DualUtilityOption() {
    // Max production fuel fraction used to build the factory
    private val maxProductionFuelFraction: Double = 0.1

    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        val noSelfResourceFactoryConsideration = NoSelfResourceFactoryConsideration(
            resourceType = resourceType,
            rankIfTrue = 5,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 1.0
        )

        val sufficientSelfResourceFactoryAtCarrierConsideration =
            SufficientSelfResourceFactoryAtCarrierConsideration(
                carrierId = carrierId,
                resourceType = resourceType,
                rankIfTrue = 0,
                multiplierIfTrue = 0.1,
                bonusIfTrue = 0.0,
                rankIfFalse = 1,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 1.0
            )

        val increasingProductionFuelConsideration = IncreasingProductionFuelConsideration(
            rankIfTrue = 0,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 0.1,
            rankIfFalse = 0,
            multiplierIfFalse = 0.1,
            bonusIfFalse = 0.0
        )

        val minFuelNeeded: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.playerScienceData().playerScienceApplicationData
            .newResourceFactoryFuelNeededByConstruction(
                outputResourceType = resourceType,
                maxNumEmployee = 1.0,
                qualityLevel = 1.0
            )
        val sufficientProductionFuelConsideration = SufficientProductionFuelConsideration(
            requiredProductionFuelRestMass = minFuelNeeded / maxProductionFuelFraction,
            rankIfTrue = 0,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 0.0,
            bonusIfFalse = 0.0
        )

        val tooManyResourceFuelFactoryAtCarrierConsideration =
            TooManySelfResourceFactoryAtCarrierConsideration(
                carrierId = carrierId,
                resourceType = resourceType,
                rankIfTrue = 0,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 0.0,
                rankIfFalse = 0,
                multiplierIfFalse = 0.1,
                bonusIfFalse = 0.0
            )

        return listOf(
            noSelfResourceFactoryConsideration,
            sufficientSelfResourceFactoryAtCarrierConsideration,
            increasingProductionFuelConsideration,
            sufficientProductionFuelConsideration,
            tooManyResourceFuelFactoryAtCarrierConsideration,
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val idealFactory: MutableResourceFactoryInternalData = planDataAtPlayer
            .getCurrentMutablePlayerData()
            .playerInternalData.playerScienceData()
            .playerScienceApplicationData.newResourceFactoryInternalData(
                resourceType,
                1.0
            )

        val fuelNeededPerEmployee: Double = planDataAtPlayer
            .getCurrentMutablePlayerData()
            .playerInternalData.playerScienceData()
            .playerScienceApplicationData.newResourceFactoryFuelNeededByConstruction(
                outputResourceType = resourceType,
                maxNumEmployee = 1.0,
                qualityLevel = 1.0
            )

        val fuelAvailable: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.physicsData().fuelRestMassData.production

        // Don't use all the fuel
        val maxUsableFuel: Double = fuelAvailable * maxProductionFuelFraction

        val numLabourer: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId).allPopData
            .labourerPopData.commonPopData.adultPopulation

        val maxNumEmployee: Double = max(
            min(
                maxUsableFuel / fuelNeededPerEmployee,
                numLabourer / ResourceType.values().size * 0.5,
            ),
            1.0
        )

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
                maxNumEmployee = maxNumEmployee,
                storedFuelRestMass = 0.0,
            )
        )
    }
}

/**
 * Remove resource factory reasoner
 */
class RemoveResourceFactoryReasoner(
    private val resourceType: ResourceType,
) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val removeSelfResourceFactoryList: List<AINode> =
            planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.popSystemData()
                .carrierDataMap.map { (carrierId, carrierData) ->
                    // Only consider self fuel factory
                    carrierData.allPopData.labourerPopData.resourceFactoryMap.filter { (_, resourceFactory) ->
                        val isThisResource: Boolean =
                            resourceFactory.resourceFactoryInternalData.outputResource == resourceType
                        val isSelf: Boolean =
                            resourceFactory.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
                        isThisResource && isSelf
                    }.keys.shuffled(Rand.rand()).map { resourceFactoryId ->
                        RemoveSpecificSelfResourceFactoryReasoner(carrierId, resourceFactoryId)
                    }
                }.flatten()

        return removeSelfResourceFactoryList
    }
}

/**
 * Remove a specific fuel factory
 */
class RemoveSpecificSelfResourceFactoryReasoner(
    private val carrierId: Int,
    private val fuelFactoryId: Int,
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        return listOf(
            RemoveSpecificSelfResourceFactoryOption(carrierId, fuelFactoryId),
            DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0),
        )
    }
}


/**
 * Dual utility option to remove a specific resource factory
 */
class RemoveSpecificSelfResourceFactoryOption(
    private val carrierId: Int,
    private val resourceFactoryId: Int,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf(
            SufficientSelfResourceFactoryAfterRemoveConsideration(
                carrierId = carrierId,
                resourceFactoryId = resourceFactoryId,
                rankIfTrue = 0,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 0.0,
                rankIfFalse = 0,
                multiplierIfFalse = 0.0,
                bonusIfFalse = 0.0
            ),
            OutdatedResourceFactoryConsideration(
                carrierId = carrierId,
                resourceFactoryId = resourceFactoryId,
                rankIfTrue = 1,
                multiplierIfTrue = 1.0
            ),
            IncreasingProductionFuelConsideration(
                rankIfTrue = 0,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 0.1,
                rankIfFalse = 0,
                multiplierIfFalse = 0.1,
                bonusIfFalse = 0.0
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        planDataAtPlayer.addCommand(
            RemoveLocalResourceFactoryCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                targetCarrierId = carrierId,
                targetResourceFactoryId = resourceFactoryId,
            )
        )
    }
}
