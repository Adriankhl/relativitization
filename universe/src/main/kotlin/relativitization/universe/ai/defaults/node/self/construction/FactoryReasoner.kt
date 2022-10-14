package relativitization.universe.ai.defaults.node.self.construction

import relativitization.universe.ai.defaults.consideration.building.NoSelfFuelFactoryAndNoStarConsideration
import relativitization.universe.ai.defaults.consideration.building.NoSelfResourceFactoryAndHasStarConsideration
import relativitization.universe.ai.defaults.consideration.building.NoSelfResourceFactoryConsideration
import relativitization.universe.ai.defaults.consideration.building.OutdatedResourceFactoryConsideration
import relativitization.universe.ai.defaults.consideration.building.OutdatedSelfFuelFactoryConsideration
import relativitization.universe.ai.defaults.consideration.building.SufficientFuelFactoryAtCarrierAfterRemoveConsideration
import relativitization.universe.ai.defaults.consideration.building.SufficientSelfFuelFactoryAtCarrierConsideration
import relativitization.universe.ai.defaults.consideration.building.SufficientSelfResourceFactoryAfterRemoveConsideration
import relativitization.universe.ai.defaults.consideration.building.SufficientSelfResourceFactoryAtCarrierConsideration
import relativitization.universe.ai.defaults.consideration.building.TooManySelfFuelFactoryAtCarrierConsideration
import relativitization.universe.ai.defaults.consideration.building.TooManySelfResourceFactoryAtCarrierConsideration
import relativitization.universe.ai.defaults.consideration.fuel.IncreasingProductionFuelConsideration
import relativitization.universe.ai.defaults.consideration.fuel.SufficientProductionFuelConsideration
import relativitization.universe.ai.defaults.consideration.position.DistanceMultiplierConsideration
import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.DoNothingDualUtilityOption
import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityOption
import relativitization.universe.ai.defaults.utils.DualUtilityReasoner
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
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
import relativitization.universe.data.components.popSystemData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.maths.physics.Int3D
import relativitization.universe.maths.physics.Intervals
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class FactoryReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val fuelReasonerList: List<AINode> = listOf(
            RemoveFuelFactoryReasoner(random),
            NewFuelFactoryReasoner(random),
        )

        val removeResourceFactoryReasoner = RemoveResourceFactoryReasoner(random)

        val newResourceReasonerList: List<AINode> = ResourceType.factoryResourceList.map {
            NewResourceFactoryReasoner(it, random)
        }

        return fuelReasonerList + removeResourceFactoryReasoner + newResourceReasonerList
    }
}

/**
 * Iterate all carrier to consider building new fuel factory
 */
class NewFuelFactoryReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
        .popSystemData().carrierDataMap.keys.map {
            NewFuelFactoryAtCarrierReasoner(it, random)
        }
}

/**
 * Consider building a fuel factory at a carrier
 */
class NewFuelFactoryAtCarrierReasoner(
    private val carrierId: Int,
    random: Random
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        return listOf(
            BuildNewFuelFactoryOption(
                carrierId = carrierId,
            ),
            DoNothingDualUtilityOption(
                rank = 1,
                multiplier = 1.0,
                bonus = 1.0,
            ),
        )
    }
}

/**
 * Option to build a new fuel factory
 */
class BuildNewFuelFactoryOption(
    private val carrierId: Int,
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
                senderTopLeaderId = planDataAtPlayer.getCurrentMutablePlayerData().topLeaderId(),
                targetCarrierId = carrierId,
                ownerId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fuelFactoryInternalData = DataSerializer.copy(idealFactory),
                maxNumEmployee = maxNumEmployee,
                storedFuelRestMass = 0.0,
                senderFuelLossFractionPerDistance = planDataAtPlayer.getCurrentMutablePlayerData()
                    .playerInternalData.playerScienceData().playerScienceApplicationData
                    .fuelLogisticsLossFractionPerDistance,
            )
        )
    }
}

/**
 * Remove fuel factory reasoner
 */
class RemoveFuelFactoryReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val removeSelfFuelFactoryList: List<DualUtilityReasoner> =
            planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.popSystemData()
                .carrierDataMap.map { (carrierId, carrierData) ->
                    // Only consider self fuel factory
                    carrierData.allPopData.labourerPopData.fuelFactoryMap.filter { (_, fuelFactory) ->
                        fuelFactory.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
                    }.keys.shuffled(random).map { fuelFactoryId ->
                        RemoveSpecificSelfFuelFactoryReasoner(
                            carrierId = carrierId,
                            fuelFactoryId = fuelFactoryId,
                            random = random
                        )
                    }
                }.flatten()

        val removeOtherFuelFactoryList: List<DualUtilityReasoner> =
            planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.popSystemData()
                .carrierDataMap.map { (carrierId, carrierData) ->
                    // Only consider other fuel factory
                    carrierData.allPopData.labourerPopData.fuelFactoryMap.filter { (_, fuelFactory) ->
                        fuelFactory.ownerPlayerId != planDataAtPlayer.getCurrentMutablePlayerData().playerId
                    }.map { (fuelFactoryId, fuelFactory) ->
                        RemoveOtherFuelFactoryReasoner(
                            carrierId = carrierId,
                            fuelFactoryId = fuelFactoryId,
                            ownerId = fuelFactory.ownerPlayerId,
                            random = random,
                        )
                    }
                }.flatten()

        return removeSelfFuelFactoryList + removeOtherFuelFactoryList
    }
}

/**
 * Remove a specific fuel factory
 */
class RemoveSpecificSelfFuelFactoryReasoner(
    private val carrierId: Int,
    private val fuelFactoryId: Int,
    random: Random,
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        return listOf(
            RemoveSpecificSelfFuelFactoryOption(
                carrierId = carrierId,
                fuelFactoryId = fuelFactoryId,
            ),
            DoNothingDualUtilityOption(
                rank = 1,
                multiplier = 1.0,
                bonus = 1.0,
            ),
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
            rankIfTrue = 0,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 0.05,
            rankIfFalse = 0,
            multiplierIfFalse = 0.0,
            bonusIfFalse = 0.0
        ),
        OutdatedSelfFuelFactoryConsideration(
            carrierId = carrierId,
            fuelFactoryId = fuelFactoryId,
            rankIfTrue = 1,
            multiplierIfTrue = 1.0
        ),
        IncreasingProductionFuelConsideration(
            rankIfTrue = 0,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 0.05,
            rankIfFalse = 0,
            multiplierIfFalse = 0.1,
            bonusIfFalse = 0.0
        )
    )

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        planDataAtPlayer.addCommand(
            RemoveLocalFuelFactoryCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                targetCarrierId = carrierId,
                targetFuelFactoryId = fuelFactoryId,
            )
        )
    }
}


/**
 * Consider removing fuel factory of other players
 */
class RemoveOtherFuelFactoryReasoner(
    private val carrierId: Int,
    private val fuelFactoryId: Int,
    private val ownerId: Int,
    random: Random,
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        RemoveSpecificOtherFuelFactoryOption(
            carrierId = carrierId,
            fuelFactoryId = fuelFactoryId,
            ownerId = ownerId,
        ),
        DoNothingDualUtilityOption(
            rank = 1,
            multiplier = 1.0,
            bonus = 1.0,
        ),
    )
}

/**
 * Dual utility option to remove a specific fuel factory
 */
class RemoveSpecificOtherFuelFactoryOption(
    private val carrierId: Int,
    private val fuelFactoryId: Int,
    private val ownerId: Int,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        val distanceMultiplierConsideration = DistanceMultiplierConsideration(
            otherPlayerId = ownerId,
            minDistance = Intervals.intDistance(Int3D(0, 0, 0), Int3D(2, 2, 2)),
            initialMultiplier = 0.5,
            exponent = 1.1,
            rank = 1,
            bonus = 1.0,
        )

        return listOf(
            distanceMultiplierConsideration
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        planDataAtPlayer.addCommand(
            RemoveLocalFuelFactoryCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
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
    private val resourceType: ResourceType,
    private val random: Random
) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
        .popSystemData().carrierDataMap.keys.map {
            NewResourceFactoryAtCarrierReasoner(
                resourceType = resourceType,
                carrierId = it,
                random = random,
            )
        }
}

/**
 * Consider building a resource factory at a carrier
 */
class NewResourceFactoryAtCarrierReasoner(
    private val resourceType: ResourceType,
    private val carrierId: Int,
    random: Random
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        BuildNewResourceFactoryOption(
            resourceType = resourceType,
            carrierId = carrierId,
        ),
        DoNothingDualUtilityOption(
            rank = 1,
            multiplier = 1.0,
            bonus = 1.0,
        ),
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
                senderTopLeaderId = planDataAtPlayer.getCurrentMutablePlayerData().topLeaderId(),
                targetCarrierId = carrierId,
                ownerId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                resourceFactoryInternalData = DataSerializer.copy(idealFactory),
                qualityLevel = 1.0,
                maxNumEmployee = maxNumEmployee,
                storedFuelRestMass = 0.0,
                senderFuelLossFractionPerDistance = planDataAtPlayer.getCurrentMutablePlayerData()
                    .playerInternalData.playerScienceData().playerScienceApplicationData
                    .fuelLogisticsLossFractionPerDistance,
            )
        )
    }
}

/**
 * Remove resource factory reasoner
 */
class RemoveResourceFactoryReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val removeSelfResourceFactoryList: List<AINode> =
            planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.popSystemData()
                .carrierDataMap.map { (carrierId, carrierData) ->
                    // Only consider self resource factory
                    carrierData.allPopData.labourerPopData.resourceFactoryMap.filter { (_, resourceFactory) ->
                        resourceFactory.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
                    }.keys.shuffled(random).map { resourceFactoryId ->
                        RemoveSpecificSelfResourceFactoryReasoner(
                            carrierId = carrierId,
                            resourceFactoryId = resourceFactoryId,
                            random = random
                        )
                    }
                }.flatten()


        val removeOtherResourceFactoryList: List<DualUtilityReasoner> =
            planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.popSystemData()
                .carrierDataMap.map { (carrierId, carrierData) ->
                    // Only consider other resource factory
                    carrierData.allPopData.labourerPopData.resourceFactoryMap.filter { (_, resourceFactory) ->
                        resourceFactory.ownerPlayerId != planDataAtPlayer.getCurrentMutablePlayerData().playerId
                    }.map { (resourceFactoryId, resourceFactory) ->
                        RemoveOtherResourceFactoryReasoner(
                            carrierId = carrierId,
                            resourceFactoryId = resourceFactoryId,
                            ownerId = resourceFactory.ownerPlayerId,
                            random = random,
                        )
                    }
                }.flatten()


        return removeSelfResourceFactoryList + removeOtherResourceFactoryList
    }
}

/**
 * Remove a specific resource factory
 */
class RemoveSpecificSelfResourceFactoryReasoner(
    private val carrierId: Int,
    private val resourceFactoryId: Int,
    random: Random,
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        return listOf(
            RemoveSpecificSelfResourceFactoryOption(
                carrierId = carrierId,
                resourceFactoryId = resourceFactoryId,
            ),
            DoNothingDualUtilityOption(
                rank = 1,
                multiplier = 1.0,
                bonus = 1.0,
            ),
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
                bonusIfTrue = 0.05,
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
                bonusIfTrue = 0.05,
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
                targetCarrierId = carrierId,
                targetResourceFactoryId = resourceFactoryId,
            )
        )
    }
}

/**
 * Consider removing resource factory of other players
 */
class RemoveOtherResourceFactoryReasoner(
    private val carrierId: Int,
    private val resourceFactoryId: Int,
    private val ownerId: Int,
    random: Random,
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        RemoveSpecificOtherResourceFactoryOption(
            carrierId = carrierId,
            resourceFactoryId = resourceFactoryId,
            ownerId = ownerId,
        ),
        DoNothingDualUtilityOption(
            rank = 1,
            multiplier = 1.0,
            bonus = 1.0,
        ),
    )
}

/**
 * Dual utility option to remove a specific resource factory
 */
class RemoveSpecificOtherResourceFactoryOption(
    private val carrierId: Int,
    private val resourceFactoryId: Int,
    private val ownerId: Int,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        val distanceMultiplierConsideration = DistanceMultiplierConsideration(
            otherPlayerId = ownerId,
            minDistance = Intervals.intDistance(Int3D(0, 0, 0), Int3D(2, 2, 2)),
            initialMultiplier = 0.5,
            exponent = 1.1,
            rank = 1,
            bonus = 1.0,
        )

        return listOf(
            distanceMultiplierConsideration
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        planDataAtPlayer.addCommand(
            RemoveLocalResourceFactoryCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                targetCarrierId = carrierId,
                targetResourceFactoryId = resourceFactoryId,
            )
        )
    }
}
