package relativitization.universe.ai.defaults.node.other.construction

import relativitization.universe.ai.defaults.consideration.building.ForeignFuelFactoryLowerCostConsideration
import relativitization.universe.ai.defaults.consideration.building.NewForeignFuelFactoryLowerCostConsideration
import relativitization.universe.ai.defaults.consideration.building.SufficientSelfFuelFactoryAtCarrierConsideration
import relativitization.universe.ai.defaults.consideration.building.SufficientSelfResourceFactoryAtCarrierConsideration
import relativitization.universe.ai.defaults.consideration.general.BooleanConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.BuildForeignFuelFactoryCommand
import relativitization.universe.data.commands.RemoveForeignFuelFactoryCommand
import relativitization.universe.data.commands.SupplyForeignFuelFactoryCommand
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.MutableFuelFactoryInternalData
import relativitization.universe.data.components.playerScienceData
import relativitization.universe.data.components.politicsData
import relativitization.universe.data.components.popSystemData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.maths.random.Rand

class ForeignFactoryReasoner : SequenceReasoner() {
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
                NewForeignFuelFactoryReasoner(),
                SupplyForeignFuelFactoryReasoner(),
                RemoveForeignFuelFactoryReasoner(),
            )
        } else {
            listOf(
                RemoveForeignFuelFactoryReasoner(),
            )
        }
    }
}

class NewForeignFuelFactoryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val neighborList: List<PlayerData> = planDataAtPlayer.universeData3DAtPlayer.getNeighbour(
            1
        ).shuffled(Rand.rand())

        // Only use 0.05 of production fuel to construct foreign factory
        planState.fillForeignFactoryFuel(0.05, planDataAtPlayer)

        return neighborList.map { playerData ->
            NewForeignFuelFactoryAtPlayerReasoner(playerData.playerId)
        }
    }
}

class NewForeignFuelFactoryAtPlayerReasoner(
    private val playerId: Int,
) : SequenceReasoner() {
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

        val canForeignInvestorBuild: Boolean = !isTopLeaderSame && otherPlayerData
            .playerInternalData.politicsData().isForeignInvestorAllowed

        return if (isTopLeader || canBuildAsSubordinate || canForeignInvestorBuild) {
            otherPlayerData.playerInternalData.popSystemData().carrierDataMap.keys.shuffled(
                Rand.rand()
            ).map {
                NewForeignFuelFactoryAtCarrierReasoner(
                    playerId = playerId,
                    carrierId = it,
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
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        BuildForeignFuelFactoryOption(
            playerId = playerId,
            carrierId = carrierId,
        ),
        DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0),
    )
}

/**
 * Option to build a foreign fuel factory at a carrier
 */
class BuildForeignFuelFactoryOption(
    private val playerId: Int,
    private val carrierId: Int,
) : DualUtilityOption() {
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

class SupplyForeignFuelFactoryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val neighborList: List<PlayerData> = planDataAtPlayer.universeData3DAtPlayer.getNeighbour(
            1
        ).shuffled(Rand.rand())

        // Only use 0.05 of production fuel to supply foreign factory
        planState.fillForeignFactoryFuel(0.05, planDataAtPlayer)

        return neighborList.map { playerData ->
            SupplyForeignFuelFactoryAtPlayerReasoner(playerData.playerId)
        }
    }
}

class SupplyForeignFuelFactoryAtPlayerReasoner(
    private val playerId: Int,
) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val otherPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(playerId)

        return otherPlayerData.playerInternalData.popSystemData().carrierDataMap.keys.shuffled(
            Rand.rand()
        ).map {
            SupplyForeignFuelFactoryAtCarrierReasoner(
                playerId = playerId,
                carrierId = it,
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
) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return planDataAtPlayer.universeData3DAtPlayer.get(playerId).playerInternalData
            .popSystemData().carrierDataMap.getValue(carrierId).allPopData.labourerPopData
            .fuelFactoryMap.filter {
                it.value.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
            }.keys.shuffled(Rand.rand()).map {
                SupplyOwnedForeignFuelFactoryReasoner(
                    playerId = playerId,
                    carrierId = carrierId,
                    fuelFactoryId = it,
                )
            }
    }
}

class SupplyOwnedForeignFuelFactoryReasoner(
    private val playerId: Int,
    private val carrierId: Int,
    private val fuelFactoryId: Int,
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        SupplyOwnedForeignFuelFactoryOption(
            playerId = playerId,
            carrierId = carrierId,
            fuelFactoryId = fuelFactoryId,
        ),
        DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0)
    )
}

class SupplyOwnedForeignFuelFactoryOption(
    private val playerId: Int,
    private val carrierId: Int,
    private val fuelFactoryId: Int,
) : DualUtilityOption() {
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

class RemoveForeignFuelFactoryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val neighborList: List<PlayerData> = planDataAtPlayer.universeData3DAtPlayer.getNeighbour(
            1
        ).shuffled(Rand.rand())

        return neighborList.map { playerData ->
            RemoveForeignFuelFactoryAtPlayerReasoner(playerData.playerId)
        }
    }
}

class RemoveForeignFuelFactoryAtPlayerReasoner(
    private val playerId: Int,
) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val otherPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(playerId)

        return otherPlayerData.playerInternalData.popSystemData().carrierDataMap.keys.shuffled(
            Rand.rand()
        ).map {
            RemoveForeignFuelFactoryAtCarrierReasoner(
                playerId = playerId,
                carrierId = it,
            )
        }
    }
}

/**
 * Consider building a fuel factory at a foreign carrier
 *
 */
class RemoveForeignFuelFactoryAtCarrierReasoner(
    private val playerId: Int,
    private val carrierId: Int,
) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return planDataAtPlayer.universeData3DAtPlayer.get(playerId).playerInternalData
            .popSystemData().carrierDataMap.getValue(carrierId).allPopData.labourerPopData
            .fuelFactoryMap.filter {
                it.value.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
            }.keys.shuffled(Rand.rand()).map {
                RemoveOwnedForeignFuelFactoryReasoner(
                    playerId = playerId,
                    carrierId = carrierId,
                    fuelFactoryId = it,
                )
            }
    }
}

class RemoveOwnedForeignFuelFactoryReasoner(
    private val playerId: Int,
    private val carrierId: Int,
    private val fuelFactoryId: Int,
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        RemoveOwnedForeignFuelFactoryOption(
            playerId = playerId,
            carrierId = carrierId,
            fuelFactoryId = fuelFactoryId,
        ),
        DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0)
    )
}

class RemoveOwnedForeignFuelFactoryOption(
    private val playerId: Int,
    private val carrierId: Int,
    private val fuelFactoryId: Int,
) : DualUtilityOption() {
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
            bonusIfFalse = 0.05
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
