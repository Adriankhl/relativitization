package relativitization.universe.ai.defaults.consideration.diplomacy

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.MutableDiplomacyData
import kotlin.math.pow

/**
 * Consideration of diplomatic relation
 *
 * @property otherPlayerId the relation between player with this id and current player
 * @property initialMultiplier the multiplier when the relation is 0
 * @property exponent exponentially modify the multiplier as the relation increases
 * @property rank rank of the dual utility data
 * @property bonus bonus of the dual utility data
 */
class RelationConsideration(
    private val otherPlayerId: Int,
    private val initialMultiplier: Double,
    private val exponent: Double,
    private val rank: Int,
    private val bonus: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val diplomacyData: MutableDiplomacyData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.diplomacyData()

        return DualUtilityData(
            rank = rank,
            multiplier = initialMultiplier * exponent.pow(diplomacyData.getRelation(otherPlayerId)),
            bonus = bonus,
        )
    }
}