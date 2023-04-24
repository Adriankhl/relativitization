package relativitization.universe.ai.defaults.consideration.hierarchy

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlanDataAtPlayer

/**
 * Consider the hierarchical relation with this player
 *
 * @property otherPlayerId Is this player self, leader, or subordinate
 * @property rankIfSelf the rank of the DualUtilityData if the player is self
 * @property rankIfDirectLeader the rank of the DualUtilityData if the player is direct leader
 * @property rankIfOtherLeader the rank of the DualUtilityData if the player is other leader
 * @property rankIfDirectSubordinate the rank of the DualUtilityData if the player is
 *  direct subordinate
 * @property rankIfOtherSubordinate the rank of the DualUtilityData if the player is
 *  other subordinate
 * @property rankIfOther the rank of the DualUtilityData if the player has no hierarchical relation
 * @property multiplier the multiplier of the DualUtilityData
 */
class HierarchyRelationConsideration(
    private val otherPlayerId: Int,
    private val rankIfSelf: Int,
    private val rankIfDirectLeader: Int,
    private val rankIfOtherLeader: Int,
    private val rankIfDirectSubordinate: Int,
    private val rankIfOtherSubordinate: Int,
    private val rankIfOther: Int,
    private val multiplier: Double,
    private val bonus: Double,
) : DualUtilityConsideration() {

    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val playerData: MutablePlayerData = planDataAtPlayer.getCurrentMutablePlayerData()

        return when {
            playerData.playerId == otherPlayerId -> DualUtilityData(
                rank = rankIfSelf,
                multiplier = multiplier,
                bonus = bonus
            )
            playerData.playerInternalData.directLeaderId == otherPlayerId -> DualUtilityData(
                rank = rankIfDirectLeader,
                multiplier = multiplier,
                bonus = bonus
            )
            playerData.isLeader(otherPlayerId) -> DualUtilityData(
                rank = rankIfOtherLeader,
                multiplier = multiplier,
                bonus = bonus
            )
            playerData.isDirectSubOrdinate(otherPlayerId) -> DualUtilityData(
                rank = rankIfDirectSubordinate,
                multiplier = multiplier,
                bonus = bonus
            )
            playerData.isSubOrdinate(otherPlayerId) -> DualUtilityData(
                rank = rankIfOtherSubordinate,
                multiplier = multiplier,
                bonus = bonus
            )
            else -> DualUtilityData(
                rank = rankIfOther,
                multiplier = multiplier,
                bonus = bonus
            )
        }
    }
}

/**
 * Check whether this player is a top leader
 *
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class IsTopLeaderConsideration(
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState): DualUtilityData {
        return if (planDataAtPlayer.getCurrentMutablePlayerData().isTopLeader()) {
            DualUtilityData(
                rank = rankIfTrue,
                multiplier = multiplierIfTrue,
                bonus = bonusIfTrue
            )
        } else {
            DualUtilityData(
                rank = rankIfFalse,
                multiplier = multiplierIfFalse,
                bonus = bonusIfFalse
            )
        }
    }
}