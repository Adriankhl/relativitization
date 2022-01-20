package relativitization.universe.ai.defaults.consideration.position

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.PlayerData
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.maths.physics.Intervals
import kotlin.math.pow

/**
 * Consider the distance of direct leader
 *
 * @property initialMultiplier the multiplier when the distance is zero
 * @property exponent exponentially modify the multiplier as the distance increases
 * @property rank rank of the dual utility data
 * @property bonus bonus of the dual utility data
 */
class DirectLeaderDistanceConsideration(
    private val initialMultiplier: Double,
    private val exponent: Double,
    private val rank: Int,
    private val bonus: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState): DualUtilityData {
        val leaderPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(
            planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.directLeaderId
        )

        val leaderInt4D: Int4D = leaderPlayerData.int4D
        val leaderGroup: Int = leaderPlayerData.groupId

        val thisInt4D: Int4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D()
        val thisGroup: Int = planDataAtPlayer.getCurrentMutablePlayerData().groupId

        val distance: Int = if ((leaderInt4D == thisInt4D) && (leaderGroup == thisGroup)) {
            0
        } else {
            Intervals.intDistance(leaderInt4D, thisInt4D)
        }

        return DualUtilityData(
            rank = rank,
            multiplier = initialMultiplier * exponent.pow(distance),
            bonus = bonus
        )
    }
}