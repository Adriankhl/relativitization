package relativitization.universe.ai.defaults.consideration.carrier

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.DualUtilityDataFactory
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.defaults.popsystem.CarrierType
import kotlin.math.pow

/**
 * Check if there is no spaceship
 *
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 */
class NoSpaceShipConsideration(
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val hasSpaceship: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.any {
                it.carrierType == CarrierType.SPACESHIP
            }

        return if (hasSpaceship) {
            DualUtilityDataFactory.noImpact()
        } else {
            DualUtilityData(rank = rankIfTrue, multiplier = multiplierIfTrue, bonus = bonusIfTrue)
        }
    }
}

/**
 * Change the multiplier exponentially as the number of spaceship increases
 *
 * @property initialMultiplier the multiplier when there is 0 spaceship
 * @property exponent exponentially modify the multiplier as the number of spaceship increases
 * @property rank rank of dual utility
 * @property bonus bonus of dual utility
 */
class NumberOfSpaceShipConsideration(
    private val initialMultiplier: Double,
    private val exponent: Double,
    private val rank: Int,
    private val bonus: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val numSpaceShip: Int = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.filter {
                it.carrierType == CarrierType.SPACESHIP
            }.size

        return DualUtilityData(
            rank = rank,
            multiplier = initialMultiplier * exponent.pow(numSpaceShip),
            bonus = bonus,
        )
    }
}