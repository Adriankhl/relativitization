package relativitization.universe.ai.default.consideration

import relativitization.universe.ai.default.utils.Consideration
import relativitization.universe.ai.default.utils.DualUtilityData
import relativitization.universe.ai.default.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.component.DiplomacyData
import kotlin.math.exp

/**
 * Consideration of diplomatic relation
 *
 * @property playerId the relation between player with this id and current player
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