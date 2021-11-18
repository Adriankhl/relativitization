package relativitization.universe.ai.default.consideration

import relativitization.universe.ai.default.utils.Consideration
import relativitization.universe.ai.default.utils.DualUtilityData
import relativitization.universe.ai.default.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
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
) : Consideration {

    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val diplomacyData: DiplomacyData = planDataAtPlayer.universeData3DAtPlayer.
        getCurrentPlayerData().playerInternalData.diplomacyData()

        return DualUtilityData(
            rank = rank,
            multiplier = multiplier,
            bonus = exp(diplomacyData.getRelation(playerId).toDouble() / normalizeRelation),
        )
    }
}