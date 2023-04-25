package relativitization.universe.game.ai.defaults.utils

import relativitization.universe.game.data.PlanDataAtPlayer
import relativitization.universe.game.data.PlayerData
import relativitization.universe.game.data.components.aiData
import relativitization.universe.game.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.components.playerScienceData
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.game.maths.physics.Int3D
import relativitization.universe.game.maths.physics.Intervals
import kotlin.math.pow

/**
 * For caching variables to share between AI node
 *
 * @property foreignFactoryFuel fuel for foreign factories
 * @property totalAdultPopulation total adult population of self
 * @property averageSelfLabourerSalary the average salary of self labourer
 * @property fuelRemainFractionMap map from other player id to the remain fraction if fuel is sent
 *  from the current player to that player
 * @property resourceRemainFractionMap map from other player id to the remain fraction if resource
 *  is sent from the current player to that player
 * @property recentlySentCommandIdSet a set of player id where this player has recently sent
 *  commands to, and the effect of the command has not been observed yet
 */
class PlanState(
    var foreignFactoryFuel: Double = 0.0,
    private var totalAdultPopulation: Double = -1.0,
    private var averageSelfLabourerSalary: Double = -1.0,
    private val fuelRemainFractionMap: MutableMap<Int, Double> = mutableMapOf(),
    private val resourceRemainFractionMap: MutableMap<Int, Double> = mutableMapOf(),
    private val recentlySentCommandIdSet: MutableSet<Int> = mutableSetOf(),
) {
    fun fillForeignFactoryFuel(
        fraction: Double,
        planDataAtPlayer: PlanDataAtPlayer,
    ) {
        foreignFactoryFuel = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .physicsData().fuelRestMassData.production * fraction
    }

    private fun updateTotalPopulation(
        planDataAtPlayer: PlanDataAtPlayer
    ) {
        totalAdultPopulation = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .popSystemData().totalAdultPopulation()
    }

    fun totalAdultPopulation(
        planDataAtPlayer: PlanDataAtPlayer
    ): Double {
        if (totalAdultPopulation == -1.0) {
            updateTotalPopulation(planDataAtPlayer)
        }
        return totalAdultPopulation
    }

    private fun updateAverageSelfLabourerSalary(
        planDataAtPlayer: PlanDataAtPlayer
    ) {
        averageSelfLabourerSalary = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().averageSalary(PopType.LABOURER)
    }

    fun averageSelfLabourerSalary(
        planDataAtPlayer: PlanDataAtPlayer
    ): Double {
        if (averageSelfLabourerSalary == -1.0) {
            updateAverageSelfLabourerSalary(planDataAtPlayer)
        }

        return averageSelfLabourerSalary
    }

    private fun updateFuelRemainFraction(
        otherPlayerId: Int,
        planDataAtPlayer: PlanDataAtPlayer,
    ) {
        val thisPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer
            .getCurrentPlayerData()
        val otherPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(otherPlayerId)

        // Compute fuel remain fraction
        val distance: Int = Intervals.intDistance(thisPlayerData.int4D, otherPlayerData.int4D)

        val fuelLossFractionPerDistance: Double =
            (thisPlayerData.playerInternalData.playerScienceData().playerScienceApplicationData
                .fuelLogisticsLossFractionPerDistance + otherPlayerData.playerInternalData
                .playerScienceData().playerScienceApplicationData
                .fuelLogisticsLossFractionPerDistance) * 0.5

        fuelRemainFractionMap[otherPlayerId] = if (distance <= Intervals.sameCubeIntDistance()) {
            1.0
        } else {
            (1.0 - fuelLossFractionPerDistance).pow(distance)
        }
    }

    fun fuelRemainFraction(
        otherPlayerId: Int,
        planDataAtPlayer: PlanDataAtPlayer,
    ): Double {
        if (!fuelRemainFractionMap.containsKey(otherPlayerId)) {
            updateFuelRemainFraction(otherPlayerId, planDataAtPlayer)
        }

        return fuelRemainFractionMap.getValue(otherPlayerId)
    }

    private fun updateResourceRemainFraction(
        otherPlayerId: Int,
        planDataAtPlayer: PlanDataAtPlayer,
    ) {
        val thisPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer
            .getCurrentPlayerData()
        val otherPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(otherPlayerId)

        // Compute fuel remain fraction
        val distance: Int = Intervals.intDistance(thisPlayerData.int4D, otherPlayerData.int4D)

        val resourceLossFractionPerDistance: Double =
            (thisPlayerData.playerInternalData.playerScienceData().playerScienceApplicationData
                .resourceLogisticsLossFractionPerDistance + otherPlayerData.playerInternalData
                .playerScienceData().playerScienceApplicationData
                .resourceLogisticsLossFractionPerDistance) * 0.5

        resourceRemainFractionMap[otherPlayerId] = if (distance <= Intervals.sameCubeIntDistance()) {
            1.0
        } else {
            (1.0 - resourceLossFractionPerDistance).pow(distance)
        }
    }

    fun resourceRemainFraction(
        otherPlayerId: Int,
        planDataAtPlayer: PlanDataAtPlayer,
    ): Double {
        if (!resourceRemainFractionMap.containsKey(otherPlayerId)) {
            updateResourceRemainFraction(otherPlayerId, planDataAtPlayer)
        }

        return resourceRemainFractionMap.getValue(otherPlayerId)
    }

    /**
     * Check if this player has interacted with another player via one or more commands recently
     *
     * @param otherPlayerId the id of the other player
     * @param planDataAtPlayer the plan data of the current player
     */
    fun isCommandSentRecently(
        otherPlayerId: Int,
        planDataAtPlayer: PlanDataAtPlayer,
    ): Boolean {
        if (recentlySentCommandIdSet.isEmpty()) {
            val recentCommandTimeMap: Map<Int, Int> =
                planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.aiData().recentCommandTimeMap

            val currentTime: Int = planDataAtPlayer.universeData3DAtPlayer.center.t
            val currentInt3D: Int3D = planDataAtPlayer.universeData3DAtPlayer.center.toInt3D()

            // Only consider direct subordinates that this player has not recently sent command to
            val recentCommandTooOldSet: Set<Int> =  recentCommandTimeMap.filterKeys {
                planDataAtPlayer.universeData3DAtPlayer.playerDataMap.containsKey(it)
            }.filter { (id, time) ->
                val otherPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(id)

                // Time needed for information to travel to the player and back
                val timeRequired: Int = Intervals.intDelay(
                    currentInt3D,
                    otherPlayerData.int4D.toInt3D(),
                    planDataAtPlayer.universeData3DAtPlayer.universeSettings.speedOfLight
                ) * 2

                timeRequired <= currentTime - time
            }.keys

            recentlySentCommandIdSet.addAll(
                recentCommandTimeMap.keys - recentCommandTooOldSet
            )
        }

        return recentlySentCommandIdSet.contains(otherPlayerId)
    }
}