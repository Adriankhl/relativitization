package relativitization.universe.game.ai.defaults.consideration.diplomacy

import relativitization.universe.core.data.PlanDataAtPlayer
import relativitization.universe.game.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.game.ai.defaults.utils.DualUtilityData
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.game.data.components.MutableDiplomacyData
import relativitization.universe.game.data.components.defaults.diplomacy.getRelation
import relativitization.universe.game.data.components.diplomacyData
import relativitization.universe.game.data.components.modifierData
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
            multiplier = initialMultiplier * exponent.pow(
                diplomacyData.relationData.getRelation(otherPlayerId)
            ),
            bonus = bonus,
        )
    }
}

/**
 * Whether this player has a peace treaty with the target player
 *
 * @property otherPlayerId the target player
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class HasPeaceTreatyConsideration(
    private val otherPlayerId: Int,
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

        val hasPeaceTreaty: Boolean = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .modifierData().diplomacyModifierData.hasPeaceTreaty(otherPlayerId)

        return if (hasPeaceTreaty) {
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
 * Whether this player has plenty of allies
 *
 * @property playerId the id of the player to check
 * @property targetNumAlly see if the current number of allies is bigger than or
 *  equal to this number
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class TooManyAllyConsideration(
    private val playerId: Int,
    private val targetNumAlly: Int,
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

        val numAlly: Int = planDataAtPlayer.getMutablePlayerData(playerId).playerInternalData
            .diplomacyData().relationData.allyMap.size

        return if (numAlly >= targetNumAlly) {
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