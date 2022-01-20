package relativitization.universe.ai.defaults.consideration.military

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.defaults.diplomacy.DiplomaticRelationState
import relativitization.universe.data.components.defaults.diplomacy.MutableWarStateData

/**
 * Check if this player has any enemy
 *
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class HasEnemyConsideration(
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState): DualUtilityData {
        val hasEnemy: Boolean = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.diplomacyData()
            .relationMap.values.any { it.diplomaticRelationState == DiplomaticRelationState.ENEMY }

        return if (hasEnemy) {
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

/**
 * Check if this player is in any war
 *
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class InWarConsideration(
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState): DualUtilityData {
        val isInWar: Boolean = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.diplomacyData()
            .warData.warStateMap.isNotEmpty()

        return if (isInWar) {
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

/**
 * Check if this player is already in war with a player
 *
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class InWarWithPlayerConsideration(
    private val otherPlayerId: Int,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState): DualUtilityData {
        val atWarWithPlayer: Boolean = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.diplomacyData()
            .warData.warStateMap.containsKey(otherPlayerId)

        return if (atWarWithPlayer) {
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

/**
 * Consider loss of player compare to the start of the war
 *
 * @property playerId the id of the other player
 * @property minMultiplier minimum of the multiplier
 * @property maxMultiplier maximum of the multiplier
 * @property rank rank of the dual utility data
 * @property bonus bonus of the dual utility data
 */
class WarLossConsideration(
    private val playerId: Int,
    private val minMultiplier: Double,
    private val maxMultiplier: Double,
    private val rank: Int,
    private val bonus: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {

        val warState: MutableWarStateData = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .diplomacyData().warData.warStateMap.getValue(playerId)

        val numOriginalSubordinate: Int = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .subordinateIdList.filter { warState.initialSubordinateList.contains(it) }.size

        val lossFraction: Double = if (warState.initialSubordinateList.isNotEmpty()) {
            1.0 - numOriginalSubordinate.toDouble() /  warState.initialSubordinateList.size.toDouble()
        } else {
            0.0
        }

        return DualUtilityData(
            rank = rank,
            multiplier = (maxMultiplier - minMultiplier) * lossFraction + minMultiplier,
            bonus = bonus
        )
    }
}