package relativitization.universe.ai.defaults.consideration.diplomacy

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.MutableDiplomacyData
import kotlin.math.exp

/**
 * Consideration of diplomatic relation
 *
 * @property playerId the relation between player with this id and current player
 * @property rank the rank of the DualUtilityData
 * @property multiplier the multiplier of the DualUtilityData
 * @property relationNormalization the normalization to scale the bonus of relation
 */
class RelationConsideration(
    private val playerId: Int,
    private val rank: Int = 1,
    private val multiplier: Double = 1.0,
    private val relationNormalization: Double = 100.0,
) : DualUtilityConsideration {

    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val diplomacyData: MutableDiplomacyData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.diplomacyData()

        return DualUtilityData(
            rank = rank,
            multiplier = multiplier,
            bonus = exp(diplomacyData.getRelation(playerId) / relationNormalization),
        )
    }
}

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
class HierarchyConsideration(
    val playerId: Int,
    private val rankIfSelf: Int = 4,
    private val rankIfDirectLeader: Int = 3,
    private val rankIfOtherLeader: Int = 2,
    private val rankIfDirectSubordinate: Int = 1,
    private val rankIfOtherSubordinate: Int = 1,
    private val rankIfOther: Int = 1,
    private val multiplier: Double = 1.0,
    private val bonus: Double = 1.0,
) : DualUtilityConsideration {

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