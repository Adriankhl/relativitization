package relativitization.universe.ai.defaults.consideration.diplomacy

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.PlayerData
import relativitization.universe.data.components.DiplomacyData
import kotlin.math.exp

/**
 * Consideration of diplomatic relation
 *
 * @property playerId the relation between player with this id and current player
 * @property rank the rank of the DualUtilityData
 * @property multiplier the multiplier of the DualUtilityData
 * @property normalizeRelation the normalization to scale the bonus of relation
 */
class RelationConsideration(
    val playerId: Int,
    private val rank: Int = 1,
    private val multiplier: Double = 1.0,
    private val normalizeRelation: Double = 100.0,
) : DualUtilityConsideration {

    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val diplomacyData: DiplomacyData =
            planDataAtPlayer.universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.diplomacyData()

        return DualUtilityData(
            rank = rank,
            multiplier = multiplier,
            bonus = exp(diplomacyData.getRelation(playerId).toDouble() / normalizeRelation),
        )
    }
}

/**
 * Consider the hierarchical relation with this player
 *
 * @property playerId Is this player self, leader, or subordinate
 * @property selfRank the rank of the DualUtilityData if the player is self
 * @property directLeaderRank the rank of the DualUtilityData if the player is direct leader
 * @property otherLeaderRank the rank of the DualUtilityData if the player is other leader
 * @property directSubordinateRank the rank of the DualUtilityData if the player is direct subordinate
 * @property otherSubordinateRank the rank of the DualUtilityData if the player is other subordinate
 * @property otherRank the rank of the DualUtilityData if the player has no hierarchical relation
 * @property multiplier the multiplier of the DualUtilityData
 */
class HierarchyConsideration(
    val playerId: Int,
    private val selfRank: Int = 4,
    private val directLeaderRank: Int = 3,
    private val otherLeaderRank: Int = 2,
    private val directSubordinateRank: Int = 1,
    private val otherSubordinateRank: Int = 1,
    private val otherRank: Int = 1,
    private val multiplier: Double = 1.0,
    private val bonus: Double = 1.0,
) : DualUtilityConsideration {

    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val playerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.getCurrentPlayerData()

        return when {
            playerData.playerId == playerId -> DualUtilityData(
                rank = selfRank,
                multiplier = multiplier,
                bonus = bonus
            )
            playerData.playerInternalData.directLeaderId == playerId -> DualUtilityData(
                rank = directLeaderRank,
                multiplier = multiplier,
                bonus = bonus
            )
            playerData.isLeader(playerId) -> DualUtilityData(
                rank = otherLeaderRank,
                multiplier = multiplier,
                bonus = bonus
            )
            playerData.isDirectSubOrdinate(playerId) -> DualUtilityData(
                rank = directSubordinateRank,
                multiplier = multiplier,
                bonus = bonus
            )
            playerData.isSubOrdinate(playerId) -> DualUtilityData(
                rank = otherSubordinateRank,
                multiplier = multiplier,
                bonus = bonus
            )
            else -> DualUtilityData(
                rank = otherRank,
                multiplier = multiplier,
                bonus = bonus
            )
        }
    }
}