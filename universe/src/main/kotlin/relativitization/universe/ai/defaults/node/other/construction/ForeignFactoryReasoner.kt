package relativitization.universe.ai.defaults.node.other.construction

import relativitization.universe.ai.defaults.consideration.building.*
import relativitization.universe.ai.defaults.consideration.general.BooleanConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.*
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.MutableFuelFactoryInternalData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.MutableResourceFactoryInternalData
import relativitization.universe.data.components.playerScienceData
import relativitization.universe.data.components.politicsData
import relativitization.universe.data.components.popSystemData
import relativitization.universe.data.serializer.DataSerializer
import kotlin.random.Random

class ForeignFactoryReasoner(random: Random) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        // Only build foreign if there are sufficient self factories
        val isSelfFuelFactorySufficient: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.keys.all {
                SufficientSelfFuelFactoryAtCarrierConsideration.isTrue(
                    planDataAtPlayer,
                    it
                )
            }

        val isSelfResourceFactorySufficient: Boolean = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData.popSystemData().carrierDataMap
            .keys.all { carrierId ->
                ResourceType.factoryResourceList.all { resourceType ->
                    SufficientSelfResourceFactoryAtCarrierConsideration.isTrue(
                        planDataAtPlayer,
                        carrierId,
                        resourceType
                    )
                }
            }

        return if (isSelfFuelFactorySufficient && isSelfResourceFactorySufficient) {
            listOf(
                NewForeignFuelFactoryReasoner(random),
                SupplyForeignFuelFactoryReasoner(random),
                RemoveForeignFuelFactoryReasoner(random),
                NewForeignResourceFactoryReasoner(random),
                SupplyForeignResourceFactoryReasoner(random),
                RemoveForeignResourceFactoryReasoner(random),
            )
        } else {
            listOf(
                RemoveForeignFuelFactoryReasoner(random),
                RemoveForeignResourceFactoryReasoner(random),
            )
        }
    }
}

class NewForeignFuelFactoryReasoner(random: Random) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val neighborList: List<PlayerData> = planDataAtPlayer.universeData3DAtPlayer
            .getNeighbourInCube(2).shuffled(random)

        // Only use 0.05 of production fuel to construct foreign factory
        planState.fillForeignFactoryFuel(0.05, planDataAtPlayer)

        return neighborList.map { playerData ->
            NewForeignFuelFactoryAtPlayerReasoner(playerData.playerId, random)
        }
    }
}

class NewForeignFuelFactoryAtPlayerReasoner(
    private val playerId: Int,
    random: Random
) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val thisPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer
            .getCurrentPlayerData()
        val otherPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(playerId)

        // Check if factory can be built on that player
        val isTopLeader: Boolean = otherPlayerData.topLeaderId() == thisPlayerData.playerId

        val isTopLeaderSame: Boolean = otherPlayerData.topLeaderId() == thisPlayerData.topLeaderId()
        val canBuildAsSubordinate: Boolean = isTopLeaderSame && otherPlayerData.playerInternalData
            .politicsData().isSubordinateBuildFactoryAllowed

        val canBuildAsForeignInvestor: Boolean = !isTopLeaderSame && otherPlayerData
            .playerInternalData.politicsData().isForeignInvestorAllowed

        return if (isTopLeader || canBuildAsSubordinate || canBuildAsForeignInvestor) {
            otherPlayerData.playerInternalData.popSystemData().carrierDataMap.keys.shuffled(
                random
            ).map {
                NewForeignFuelFactoryAtCarrierReasoner(
                    playerId = playerId,
                    carrierId = it,
                    random,
                )
            }
        } else {
            listOf()
        }
    }
}

/**
 * Consider building a fuel factory at a foreign carrier
 *
 */
class NewForeignFuelFactoryAtCarrierReasoner(
    private val playerId: Int,
    private val carrierId: Int,
    random: Random,
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        BuildForeignFuelFactoryOption(
            playerId = playerId,
            carrierId = carrierId,
            random = random,
        ),
        DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0, random = random,),
    )
}

/**
 * Option to build a foreign fuel factory at a carrier
 */
class BuildForeignFuelFactoryOption(
    private val playerId: Int,
    private val carrierId: Int,
    random: Random,
) : DualUtilityOption(random) {
    // maximum fraction of fuel to be used to construct the foreign factory
    private val maxFuelFraction: Double = 0.1

    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        val fuelRemainFraction: Double = planState.fuelRemainFraction(
            playerId,
            planDataAtPlayer,
        )

        // Make sure all self carriers have sufficient fuel and resource factory
        val minFuelNeeded: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.playerScienceData().playerScienceApplicationData
            .newFuelFactoryFuelNeededByConstruction(
                1E4 / fuelRemainFraction
            )
        val sufficientFuelConsideration = BooleanConsideration(
            rankIfTrue = 0,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 0.0,
            bonusIfFalse = 0.0,
        ) { _, _ ->
            planState.foreignFactoryFuel * maxFuelFraction > minFuelNeeded
        }

        // Compare the cost to set up a local fuel factory and a foreign factory
        val newForeignFuelFactoryLowerCostConsideration =
            NewForeignFuelFactoryLowerCostConsideration(
                otherPlayerId = playerId,
                otherCarrierId = carrierId,
                rankIfTrue = 1,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 0.01,
                rankIfFalse = 0,
                multiplierIfFalse = 0.0,
                bonusIfFalse = 0.0

            )

        return listOf(
            sufficientFuelConsideration,
            newForeignFuelFactoryLowerCostConsideration,
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

        val fuelAvailable: Double = planState.foreignFactoryFuel * maxFuelFraction

        // Update plan state
        planState.foreignFactoryFuel -= fuelAvailable

        val maxNumEmployee: Double = fuelAvailable * 0.5 / fuelNeededPerEmployee
        val storedFuelRestMass: Double = fuelAvailable * 0.5

        planDataAtPlayer.addCommand(
            BuildForeignFuelFactoryCommand(
                toId = playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                senderTopLeaderId = planDataAtPlayer.getCurrentMutablePlayerData().topLeaderId(),
                targetCarrierId = carrierId,
                ownerId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fuelFactoryInternalData = DataSerializer.copy(idealFactory),
                maxNumEmployee = maxNumEmployee,
                storedFuelRestMass = storedFuelRestMass,
                senderFuelLossFractionPerDistance = planDataAtPlayer.getCurrentMutablePlayerData()
                    .playerInternalData.playerScienceData().playerScienceApplicationData
                    .fuelLogisticsLossFractionPerDistance,
            )
        )
    }
}

class SupplyForeignFuelFactoryReasoner(random: Random) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val neighborList: List<PlayerData> = planDataAtPlayer.universeData3DAtPlayer
            .getNeighbourInCube(2).shuffled(random)

        // Only use 0.05 of production fuel to supply foreign factory
        planState.fillForeignFactoryFuel(0.05, planDataAtPlayer)

        return neighborList.map { playerData ->
            SupplyForeignFuelFactoryAtPlayerReasoner(playerData.playerId, random)
        }
    }
}

class SupplyForeignFuelFactoryAtPlayerReasoner(
    private val playerId: Int,
    random: Random
) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val otherPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(playerId)

        return otherPlayerData.playerInternalData.popSystemData().carrierDataMap.keys.shuffled(
            random
        ).map {
            SupplyForeignFuelFactoryAtCarrierReasoner(
                playerId = playerId,
                carrierId = it,
                random = random,
            )
        }
    }
}

/**
 * Consider building a fuel factory at a foreign carrier
 *
 */
class SupplyForeignFuelFactoryAtCarrierReasoner(
    private val playerId: Int,
    private val carrierId: Int,
    random: Random,
) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return planDataAtPlayer.universeData3DAtPlayer.get(playerId).playerInternalData
            .popSystemData().carrierDataMap.getValue(carrierId).allPopData.labourerPopData
            .fuelFactoryMap.filter {
                it.value.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
            }.keys.shuffled(random).map {
                SupplyOwnedForeignFuelFactoryReasoner(
                    playerId = playerId,
                    carrierId = carrierId,
                    fuelFactoryId = it,
                    random = random,
                )
            }
    }
}

class SupplyOwnedForeignFuelFactoryReasoner(
    private val playerId: Int,
    private val carrierId: Int,
    private val fuelFactoryId: Int,
    random: Random,
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        SupplyOwnedForeignFuelFactoryOption(
            playerId = playerId,
            carrierId = carrierId,
            fuelFactoryId = fuelFactoryId,
            random = random,
        ),
        DoNothingDualUtilityOption(
            rank = 1,
            multiplier = 1.0,
            bonus = 1.0,
            random = random,
        )
    )
}

class SupplyOwnedForeignFuelFactoryOption(
    private val playerId: Int,
    private val carrierId: Int,
    private val fuelFactoryId: Int,
    random: Random,
) : DualUtilityOption(random) {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        val foreignFuelFactoryLowerCostConsideration = ForeignFuelFactoryLowerCostConsideration(
            otherPlayerId = playerId,
            otherCarrierId = carrierId,
            fuelFactoryId = fuelFactoryId,
            rankIfTrue = 1,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 0.1,
            rankIfFalse = 0,
            multiplierIfFalse = 0.0,
            bonusIfFalse = 0.0
        )
        return listOf(
            foreignFuelFactoryLowerCostConsideration
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val fuelAvailable: Double = planState.foreignFactoryFuel * 0.1
        planState.foreignFactoryFuel -= fuelAvailable

        planDataAtPlayer.addCommand(
            SupplyForeignFuelFactoryCommand(
                toId = playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                targetCarrierId = carrierId,
                targetFuelFactoryId = fuelFactoryId,
                amount = fuelAvailable,
                senderFuelLossFractionPerDistance = planDataAtPlayer.getCurrentMutablePlayerData()
                    .playerInternalData.playerScienceData().playerScienceApplicationData
                    .fuelLogisticsLossFractionPerDistance,
            )
        )
    }
}

class RemoveForeignFuelFactoryReasoner(random: Random) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val neighborList: List<PlayerData> = planDataAtPlayer.universeData3DAtPlayer
            .getNeighbourInCube(2).shuffled(random)

        return neighborList.map { playerData ->
            RemoveForeignFuelFactoryAtPlayerReasoner(playerData.playerId, random)
        }
    }
}

class RemoveForeignFuelFactoryAtPlayerReasoner(
    private val playerId: Int,
    random: Random
) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val otherPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(playerId)

        return otherPlayerData.playerInternalData.popSystemData().carrierDataMap.keys.shuffled(
            random
        ).map {
            RemoveForeignFuelFactoryAtCarrierReasoner(
                playerId = playerId,
                carrierId = it,
                random = random,
            )
        }
    }
}

/**
 * Consider removing a fuel factory at a foreign carrier
 *
 */
class RemoveForeignFuelFactoryAtCarrierReasoner(
    private val playerId: Int,
    private val carrierId: Int,
    random: Random,
) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return planDataAtPlayer.universeData3DAtPlayer.get(playerId).playerInternalData
            .popSystemData().carrierDataMap.getValue(carrierId).allPopData.labourerPopData
            .fuelFactoryMap.filter {
                it.value.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
            }.keys.shuffled(random).map {
                RemoveOwnedForeignFuelFactoryReasoner(
                    playerId = playerId,
                    carrierId = carrierId,
                    fuelFactoryId = it,
                    random = random,
                )
            }
    }
}

class RemoveOwnedForeignFuelFactoryReasoner(
    private val playerId: Int,
    private val carrierId: Int,
    private val fuelFactoryId: Int,
    random: Random,
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        RemoveOwnedForeignFuelFactoryOption(
            playerId = playerId,
            carrierId = carrierId,
            fuelFactoryId = fuelFactoryId,
            random = random,
        ),
        DoNothingDualUtilityOption(
            rank = 1,
            multiplier = 1.0,
            bonus = 1.0,
            random = random,
        )
    )
}

class RemoveOwnedForeignFuelFactoryOption(
    private val playerId: Int,
    private val carrierId: Int,
    private val fuelFactoryId: Int,
    random: Random,
) : DualUtilityOption(random) {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        val foreignFuelFactoryLowerCostConsideration = ForeignFuelFactoryLowerCostConsideration(
            otherPlayerId = playerId,
            otherCarrierId = carrierId,
            fuelFactoryId = fuelFactoryId,
            rankIfTrue = 0,
            multiplierIfTrue = 0.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 1,
            multiplierIfFalse = 1.0,
            bonusIfFalse = 0.01,
        )
        return listOf(
            foreignFuelFactoryLowerCostConsideration
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        planDataAtPlayer.addCommand(
            RemoveForeignFuelFactoryCommand(
                toId = playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                targetCarrierId = carrierId,
                targetFuelFactoryId = fuelFactoryId,
            )
        )
    }
}

class NewForeignResourceFactoryReasoner(random: Random) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val neighborList: List<PlayerData> = planDataAtPlayer.universeData3DAtPlayer
            .getNeighbourInCube(2).shuffled(random)

        // Only use 0.05 of production fuel to construct foreign factory
        planState.fillForeignFactoryFuel(0.05, planDataAtPlayer)

        return neighborList.map { playerData ->
            NewForeignResourceFactoryAtPlayerReasoner(playerData.playerId, random)
        }
    }
}

class NewForeignResourceFactoryAtPlayerReasoner(
    private val playerId: Int,
    random: Random,
) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val thisPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer
            .getCurrentPlayerData()
        val otherPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(playerId)

        // Check if factory can be built on that player
        val isTopLeader: Boolean = otherPlayerData.topLeaderId() == thisPlayerData.playerId

        val isTopLeaderSame: Boolean = otherPlayerData.topLeaderId() == thisPlayerData.topLeaderId()
        val canBuildAsSubordinate: Boolean = isTopLeaderSame && otherPlayerData.playerInternalData
            .politicsData().isSubordinateBuildFactoryAllowed

        val canBuildAsForeignInvestor: Boolean = !isTopLeaderSame && otherPlayerData
            .playerInternalData.politicsData().isForeignInvestorAllowed

        return if (isTopLeader || canBuildAsSubordinate || canBuildAsForeignInvestor) {
            otherPlayerData.playerInternalData.popSystemData().carrierDataMap.keys.shuffled(
                random
            ).map { carrierId ->
                ResourceType.factoryResourceList.map { resourceType ->
                    NewForeignResourceFactoryAtCarrierReasoner(
                        playerId = playerId,
                        carrierId = carrierId,
                        resourceType = resourceType,
                        random = random,
                    )
                }
            }.flatten()
        } else {
            listOf()
        }
    }
}

/**
 * Consider building a resource factory at a foreign carrier
 *
 */
class NewForeignResourceFactoryAtCarrierReasoner(
    private val playerId: Int,
    private val carrierId: Int,
    private val resourceType: ResourceType,
    random: Random,
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        BuildForeignResourceFactoryOption(
            playerId = playerId,
            carrierId = carrierId,
            resourceType = resourceType,
            random = random,
        ),
        DoNothingDualUtilityOption(
            rank = 1,
            multiplier = 1.0,
            bonus = 1.0,
            random = random,
        ),
    )
}

/**
 * Option to build a foreign resource factory at a carrier
 */
class BuildForeignResourceFactoryOption(
    private val playerId: Int,
    private val carrierId: Int,
    private val resourceType: ResourceType,
    random: Random,
) : DualUtilityOption(random) {
    // maximum fraction of fuel to be used to construct the foreign factory
    private val maxFuelFraction: Double = 0.1

    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        val fuelRemainFraction: Double = planState.fuelRemainFraction(
            playerId,
            planDataAtPlayer,
        )

        // Make sure all self carriers have sufficient fuel and resource factory
        val minFuelNeeded: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.playerScienceData().playerScienceApplicationData
            .newResourceFactoryFuelNeededByConstruction(
                outputResourceType = resourceType,
                maxNumEmployee = 1E4 / fuelRemainFraction,
                qualityLevel = 1.0,
            )
        val sufficientFuelConsideration = BooleanConsideration(
            rankIfTrue = 0,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 0.0,
            bonusIfFalse = 0.0,
        ) { _, _ ->
            planState.foreignFactoryFuel * maxFuelFraction > minFuelNeeded
        }

        // Compare the cost to set up a local fuel factory and a foreign factory
        val newForeignResourceFactoryLowerCostConsideration =
            NewForeignResourceFactoryLowerCostConsideration(
                otherPlayerId = playerId,
                otherCarrierId = carrierId,
                resourceType = resourceType,
                rankIfTrue = 1,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 0.01,
                rankIfFalse = 0,
                multiplierIfFalse = 0.0,
                bonusIfFalse = 0.0

            )

        return listOf(
            sufficientFuelConsideration,
            newForeignResourceFactoryLowerCostConsideration,
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val factoryInternalData: MutableResourceFactoryInternalData = planDataAtPlayer
            .getCurrentMutablePlayerData()
            .playerInternalData
            .playerScienceData()
            .playerScienceApplicationData
            .newResourceFactoryInternalData(resourceType, 1.0)

        val fuelNeededPerEmployee: Double = planDataAtPlayer
            .getCurrentMutablePlayerData()
            .playerInternalData
            .playerScienceData()
            .playerScienceApplicationData
            .newFuelFactoryFuelNeededByConstruction(1.0)

        val fuelAvailable: Double = planState.foreignFactoryFuel * maxFuelFraction

        // Update plan state
        planState.foreignFactoryFuel -= fuelAvailable

        val maxNumEmployee: Double = fuelAvailable * 0.5 / fuelNeededPerEmployee
        val storedFuelRestMass: Double = fuelAvailable * 0.5

        planDataAtPlayer.addCommand(
            BuildForeignResourceFactoryCommand(
                toId = playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                senderTopLeaderId = planDataAtPlayer.getCurrentMutablePlayerData().topLeaderId(),
                targetCarrierId = carrierId,
                ownerId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                resourceFactoryInternalData = DataSerializer.copy(factoryInternalData),
                qualityLevel = 1.0,
                maxNumEmployee = maxNumEmployee,
                storedFuelRestMass = storedFuelRestMass,
                senderFuelLossFractionPerDistance = planDataAtPlayer.getCurrentMutablePlayerData()
                    .playerInternalData.playerScienceData().playerScienceApplicationData
                    .fuelLogisticsLossFractionPerDistance,
            )
        )
    }
}

class SupplyForeignResourceFactoryReasoner(random: Random) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val neighborList: List<PlayerData> = planDataAtPlayer.universeData3DAtPlayer
            .getNeighbourInCube(2).shuffled(random)

        // Only use 0.05 of production fuel to supply foreign factory
        planState.fillForeignFactoryFuel(0.05, planDataAtPlayer)

        return neighborList.map { playerData ->
            SupplyForeignResourceFactoryAtPlayerReasoner(playerData.playerId, random)
        }
    }
}

class SupplyForeignResourceFactoryAtPlayerReasoner(
    private val playerId: Int,
    random: Random,
) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val otherPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(playerId)

        return otherPlayerData.playerInternalData.popSystemData().carrierDataMap.keys.shuffled(
            random
        ).map {
            SupplyForeignResourceFactoryAtCarrierReasoner(
                playerId = playerId,
                carrierId = it,
                random = random,
            )
        }
    }
}

/**
 * Consider building a fuel factory at a foreign carrier
 *
 */
class SupplyForeignResourceFactoryAtCarrierReasoner(
    private val playerId: Int,
    private val carrierId: Int,
    random: Random,
) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return planDataAtPlayer.universeData3DAtPlayer.get(playerId).playerInternalData
            .popSystemData().carrierDataMap.getValue(carrierId).allPopData.labourerPopData
            .resourceFactoryMap.filter {
                it.value.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
            }.keys.shuffled(random).map {
                SupplyOwnedForeignResourceFactoryReasoner(
                    playerId = playerId,
                    carrierId = carrierId,
                    resourceFactoryId = it,
                    random = random,
                )
            }
    }
}

class SupplyOwnedForeignResourceFactoryReasoner(
    private val playerId: Int,
    private val carrierId: Int,
    private val resourceFactoryId: Int,
    random: Random,
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        SupplyOwnedForeignResourceFactoryOption(
            playerId = playerId,
            carrierId = carrierId,
            resourceFactoryId = resourceFactoryId,
            random = random,
        ),
        DoNothingDualUtilityOption(
            rank = 1,
            multiplier = 1.0,
            bonus = 1.0,
            random = random,
        )
    )
}

class SupplyOwnedForeignResourceFactoryOption(
    private val playerId: Int,
    private val carrierId: Int,
    private val resourceFactoryId: Int,
    random: Random,
) : DualUtilityOption(random) {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        val foreignResourceFactoryLowerCostConsideration =
            ForeignResourceFactoryLowerCostConsideration(
                otherPlayerId = playerId,
                otherCarrierId = carrierId,
                resourceFactoryId = resourceFactoryId,
                rankIfTrue = 1,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 0.1,
                rankIfFalse = 0,
                multiplierIfFalse = 0.0,
                bonusIfFalse = 0.0
            )
        return listOf(
            foreignResourceFactoryLowerCostConsideration
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val fuelAvailable: Double = planState.foreignFactoryFuel * 0.1
        planState.foreignFactoryFuel -= fuelAvailable

        planDataAtPlayer.addCommand(
            SupplyForeignResourceFactoryCommand(
                toId = playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                targetCarrierId = carrierId,
                targetResourceFactoryId = resourceFactoryId,
                amount = fuelAvailable,
                senderFuelLossFractionPerDistance = planDataAtPlayer.getCurrentMutablePlayerData()
                    .playerInternalData.playerScienceData().playerScienceApplicationData
                    .fuelLogisticsLossFractionPerDistance,
            )
        )
    }
}


class RemoveForeignResourceFactoryReasoner(random: Random) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val neighborList: List<PlayerData> = planDataAtPlayer.universeData3DAtPlayer
            .getNeighbourInCube(2).shuffled(random)

        return neighborList.map { playerData ->
            RemoveForeignResourceFactoryAtPlayerReasoner(playerData.playerId, random)
        }
    }
}

class RemoveForeignResourceFactoryAtPlayerReasoner(
    private val playerId: Int,
    random: Random,
) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val otherPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(playerId)

        return otherPlayerData.playerInternalData.popSystemData().carrierDataMap.keys.shuffled(
            random
        ).map {
            RemoveForeignResourceFactoryAtCarrierReasoner(
                playerId = playerId,
                carrierId = it,
                random = random,
            )
        }
    }
}

/**
 * Consider removing a resource factory at a foreign carrier
 */
class RemoveForeignResourceFactoryAtCarrierReasoner(
    private val playerId: Int,
    private val carrierId: Int,
    random: Random
) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return planDataAtPlayer.universeData3DAtPlayer.get(playerId).playerInternalData
            .popSystemData().carrierDataMap.getValue(carrierId).allPopData.labourerPopData
            .resourceFactoryMap.filter {
                it.value.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
            }.keys.shuffled(random).map {
                RemoveOwnedForeignResourceFactoryReasoner(
                    playerId = playerId,
                    carrierId = carrierId,
                    resourceFactoryId = it,
                    random = random,
                )
            }
    }
}

class RemoveOwnedForeignResourceFactoryReasoner(
    private val playerId: Int,
    private val carrierId: Int,
    private val resourceFactoryId: Int,
    random: Random
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        RemoveOwnedForeignResourceFactoryOption(
            playerId = playerId,
            carrierId = carrierId,
            resourceFactoryId = resourceFactoryId,
            random = random,
        ),
        DoNothingDualUtilityOption(
            rank = 1,
            multiplier = 1.0,
            bonus = 1.0,
            random = random,
        )
    )
}

class RemoveOwnedForeignResourceFactoryOption(
    private val playerId: Int,
    private val carrierId: Int,
    private val resourceFactoryId: Int,
    random: Random,
) : DualUtilityOption(random) {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        val foreignResourceFactoryLowerCostConsideration =
            ForeignResourceFactoryLowerCostConsideration(
                otherPlayerId = playerId,
                otherCarrierId = carrierId,
                resourceFactoryId = resourceFactoryId,
                rankIfTrue = 0,
                multiplierIfTrue = 0.0,
                bonusIfTrue = 0.0,
                rankIfFalse = 1,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 0.01,
            )
        return listOf(
            foreignResourceFactoryLowerCostConsideration
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        planDataAtPlayer.addCommand(
            RemoveForeignResourceFactoryCommand(
                toId = playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                targetCarrierId = carrierId,
                targetResourceFactoryId = resourceFactoryId,
            )
        )
    }
}
