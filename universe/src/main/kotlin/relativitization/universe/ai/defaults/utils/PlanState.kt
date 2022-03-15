package relativitization.universe.ai.defaults.utils

import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.PlayerData
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.data.components.physicsData
import relativitization.universe.data.components.playerScienceData
import relativitization.universe.data.components.popSystemData
import relativitization.universe.maths.physics.Intervals
import kotlin.math.pow

/**
 * For caching variables to share between AI node
 *
 * @property foreignFactoryFuel fuel for foreign factories
 * @property averageSelfLabourerSalary the average salary of self labourer
 * @property fuelRemainFractionMap map from other player id to the remain fraction if fuel is sent
 * from the current player to that player
 */
class PlanState(
    var foreignFactoryFuel: Double = 0.0,
    private var averageSelfLabourerSalary: Double = -1.0,
    private val fuelRemainFractionMap: MutableMap<Int, Double> = mutableMapOf(),
) {
    fun fillForeignFactoryFuel(
        fraction: Double,
        planDataAtPlayer: PlanDataAtPlayer,
    ) {
        foreignFactoryFuel = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .physicsData().fuelRestMassData.production * fraction
    }

    fun updateAverageSelfLabourerSalary(
        planDataAtPlayer: PlanDataAtPlayer
    ) {
        // Compute average labourer salary of self
        val totalSelfLabourerSalary: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.sumOf {
                val commonPopData: MutableCommonPopData = it.allPopData.getCommonPopData(
                    PopType.LABOURER
                )
                commonPopData.salaryPerEmployee * commonPopData.adultPopulation *
                        commonPopData.employmentRate
            }
        val totalSelfLabourer: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.sumOf {
                it.allPopData.getCommonPopData(
                    PopType.LABOURER
                ).adultPopulation
            }
        averageSelfLabourerSalary = if (totalSelfLabourer > 0.0) {
            totalSelfLabourerSalary / totalSelfLabourer
        } else {
            0.0
        }
    }

    fun averageSelfLabourerSalary(
        planDataAtPlayer: PlanDataAtPlayer
    ): Double {
        if (averageSelfLabourerSalary == -1.0) {
            updateAverageSelfLabourerSalary(planDataAtPlayer)
        }

        return averageSelfLabourerSalary
    }

    fun updateFuelRemainFraction(
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
}