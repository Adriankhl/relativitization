package relativitization.universe.game.ai.defaults.consideration.subordinate

import relativitization.universe.core.data.PlanDataAtPlayer
import relativitization.universe.game.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.game.ai.defaults.utils.DualUtilityData
import relativitization.universe.game.ai.defaults.utils.PlanState
import kotlin.math.pow

/**
 * Change the multiplier exponentially as the number of direct subordinate increases
 *
 * @property initialMultiplier the multiplier when there is 0 spaceship
 * @property exponent exponentially modify the multiplier as the number of spaceship increases
 * @property rank rank of dual utility
 * @property bonus bonus of dual utility
 */
class NumberOfDirectSubordinateConsideration(
    private val initialMultiplier: Double,
    private val exponent: Double,
    private val rank: Int,
    private val bonus: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val numDirectSubordinate: Int = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.directSubordinateIdSet.size

        return DualUtilityData(
            rank = rank,
            multiplier = initialMultiplier * exponent.pow(numDirectSubordinate),
            bonus = bonus,
        )
    }
}