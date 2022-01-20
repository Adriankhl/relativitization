package relativitization.universe.ai.defaults.consideration.hierarchy

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlanDataAtPlayer

/**
 * Consider the hierarchical relation with this player
 *
 * @property playerId Is this player self, leader, or subordinate
 * @property rankIfSelf the rank of the DualUtilityData if the player is self
 * @property rankIfDirectLeader the rank of the DualUtilityData if the player is direct leader
 * @property rankIfOtherLeader the rank of the DualUtilityData if the player is other leader
 * @property rankIfDirectSubordinate the rank of the DualUtilityData if the player is direct subordinate
 * @property rankIfOtherSubordinate the rank of the DualUtilityData if the player is other subordinate
 * @property rankIfOther the rank of the DualUtilityData if the player has no hierarchical relation
 * @property multiplier the multiplier of the DualUtilityData
 */
class HierarchyRelationConsideration(
    private val playerId: Int,
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
            playerData.playerId == playerId -> DualUtilityData(
                rank = rankIfSelf,
                multiplier = multiplier,
                bonus = bonus
            )
            playerData.playerInternalData.directLeaderId == playerId -> DualUtilityData(
                rank = rankIfDirectLeader,
                multiplier = multiplier,
                bonus = bonus
            )
            playerData.isLeader(playerId) -> DualUtilityData(
                rank = rankIfOtherLeader,
                multiplier = multiplier,
                bonus = bonus
            )
            playerData.isDirectSubOrdinate(playerId) -> DualUtilityData(
                rank = rankIfDirectSubordinate,
                multiplier = multiplier,
                bonus = bonus
            )
            playerData.isSubOrdinate(playerId) -> DualUtilityData(
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