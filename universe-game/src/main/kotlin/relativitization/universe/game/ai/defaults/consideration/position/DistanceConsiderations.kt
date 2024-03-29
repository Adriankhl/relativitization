package relativitization.universe.game.ai.defaults.consideration.position

import relativitization.universe.core.data.PlanDataAtPlayer
import relativitization.universe.core.data.PlayerData
import relativitization.universe.core.maths.physics.Int4D
import relativitization.universe.core.maths.physics.Intervals
import relativitization.universe.game.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.game.ai.defaults.utils.DualUtilityData
import relativitization.universe.game.ai.defaults.utils.DualUtilityDataFactory
import relativitization.universe.game.ai.defaults.utils.PlanState
import kotlin.math.pow

/**
 * Consider the distance between this player and the other player to change the multiplier
 *
 * @property otherPlayerId the id of the other player
 * @property minDistance no impact if the distance is smaller than this
 * @property initialMultiplier the multiplier when the distance is zero
 * @property exponent exponentially modify the multiplier as the distance increases
 * @property rank rank of the dual utility data
 * @property bonus bonus of the dual utility data
 */
class DistanceMultiplierConsideration(
    private val otherPlayerId: Int,
    private val minDistance: Int,
    private val initialMultiplier: Double,
    private val exponent: Double,
    private val rank: Int,
    private val bonus: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val hasOtherPlayer: Boolean =
            planDataAtPlayer.universeData3DAtPlayer.playerDataMap.containsKey(otherPlayerId)

        return if (hasOtherPlayer) {
            val otherPlayerData: PlayerData =
                planDataAtPlayer.universeData3DAtPlayer.get(otherPlayerId)

            val otherInt4D: Int4D = otherPlayerData.int4D
            val otherGroup: Int = otherPlayerData.groupId

            val thisInt4D: Int4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D()
            val thisGroup: Int = planDataAtPlayer.getCurrentMutablePlayerData().groupId

            val distance: Int = if ((otherInt4D == thisInt4D) && (otherGroup == thisGroup)) {
                0
            } else {
                Intervals.intDistance(otherInt4D, thisInt4D)
            }

            if (distance < minDistance) {
                DualUtilityDataFactory.noImpact()
            } else {
                DualUtilityData(
                    rank = rank,
                    multiplier = initialMultiplier * exponent.pow(distance),
                    bonus = bonus
                )
            }
        } else {
            DualUtilityDataFactory.noImpact()
        }
    }
}