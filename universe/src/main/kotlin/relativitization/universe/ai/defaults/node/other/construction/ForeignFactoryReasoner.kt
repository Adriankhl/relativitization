package relativitization.universe.ai.defaults.node.other.construction

import relativitization.universe.ai.defaults.consideration.building.NoSelfFuelFactoryAndNoStarConsideration
import relativitization.universe.ai.defaults.consideration.building.NoSelfResourceFactoryConsideration
import relativitization.universe.ai.defaults.consideration.fuel.SufficientProductionFuelConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.PlayerData
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.physicsData
import relativitization.universe.data.components.playerScienceData
import relativitization.universe.data.components.popSystemData
import relativitization.universe.maths.physics.Intervals
import kotlin.math.pow

class ForeignFactoryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        // Only use 0.1 of production fuel to construct foreign factory
        planState.foreignConstructionFuel = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.physicsData().fuelRestMassData.production * 0.1

        return listOf(
            NewForeignFuelFactoryReasoner()
        )
    }
}

class NewForeignFuelFactoryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val neighborList: List<PlayerData> = planDataAtPlayer.universeData3DAtPlayer.getNeighbour(
            1
        )

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
        // Compute fuel remain fraction from science
        val thisPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer
            .getCurrentPlayerData()
        val otherPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(playerId)

        val distance: Int = Intervals.intDistance(thisPlayerData.int4D, otherPlayerData.int4D)

        val fuelLossFractionPerDistance: Double =
            (thisPlayerData.playerInternalData.playerScienceData().playerScienceApplicationData
                .fuelLogisticsLossFractionPerDistance + otherPlayerData.playerInternalData
                .playerScienceData().playerScienceApplicationData
                .fuelLogisticsLossFractionPerDistance) * 0.5

        val fuelRemainFraction: Double = if (distance <= Intervals.sameCubeIntDistance()) {
            1.0
        } else {
            (1.0 - fuelLossFractionPerDistance).pow(distance)
        }

        return otherPlayerData.playerInternalData.popSystemData().carrierDataMap.keys.map {
            NewForeignFuelFactoryAtCarrierReasoner(playerId, it, fuelRemainFraction)
        }
    }
}

/**
 * Consider building a fuel factory at a foreign carrier
 *
 * @property fuelRemainFraction the estimated remain fraction of fuel after logistic loss
 */
class NewForeignFuelFactoryAtCarrierReasoner(
    private val playerId: Int,
    private val carrierId: Int,
    private val fuelRemainFraction: Double,
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        BuildForeignFuelFactoryOption(playerId, carrierId, fuelRemainFraction),
        DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0),
    )
}

/**
 * Option to build a foreign fuel factory at a carrier
 *
 * @property fuelRemainFraction the estimated remain fraction of fuel after logistic loss
 */
class BuildForeignFuelFactoryOption(
    private val playerId: Int,
    private val carrierId: Int,
    private val fuelRemainFraction: Double,
) : DualUtilityOption() {
    // Max production fuel fraction used to build the factory
    private val maxProductionFuelFraction: Double = 0.01

    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        val noSelfFuelFactoryAndNoStarConsideration = NoSelfFuelFactoryAndNoStarConsideration(
            rankIfTrue = 0,
            multiplierIfTrue = 0.0,
            bonusIfTrue = 0.0,
        )

        val noSelfResourceFactoryConsiderationList: List<DualUtilityConsideration> =
            ResourceType.values().map {
                NoSelfResourceFactoryConsideration(
                    resourceType = it,
                    rankIfTrue = 0,
                    multiplierIfTrue = 0.0,
                    bonusIfTrue = 0.0
                )
            }

        // Make sure all self carriers have sufficient fuel and resource factory

        val minFuelNeeded: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.playerScienceData().playerScienceApplicationData
            .newFuelFactoryFuelNeededByConstruction(1.0 / fuelRemainFraction)
        val sufficientProductionFuelConsideration = SufficientProductionFuelConsideration(
            requiredProductionFuelRestMass = minFuelNeeded / maxProductionFuelFraction,
            rankIfTrue = 0,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 0.0,
            bonusIfFalse = 0.0
        )

        return listOf(noSelfFuelFactoryAndNoStarConsideration) +
                noSelfResourceFactoryConsiderationList +
                sufficientProductionFuelConsideration
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        TODO("Not yet implemented")
    }
}