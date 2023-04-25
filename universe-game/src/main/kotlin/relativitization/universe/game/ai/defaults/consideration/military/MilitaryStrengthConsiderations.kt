package relativitization.universe.game.ai.defaults.consideration.military

import relativitization.universe.game.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.game.ai.defaults.utils.DualUtilityData
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.core.data.PlanDataAtPlayer

/**
 * Compare the military strength of this player to target player, exclude this player and the subordinate of this
 * player if they are subordinates of the target
 *
 * @property selfMilitaryScore the military score of self
 * @property selfMilitaryScore the military score of self
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class LargerMilitaryStrengthConsideration(
    private val selfMilitaryScore: Double,
    private val targetMilitaryScore: Double,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        return if (selfMilitaryScore >= targetMilitaryScore) {
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