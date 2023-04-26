package relativitization.universe.game.ai.defaults.consideration.military

import relativitization.universe.core.data.PlanDataAtPlayer
import relativitization.universe.game.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.game.ai.defaults.utils.DualUtilityData
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.game.data.components.defaults.diplomacy.war.MutableWarData
import relativitization.universe.game.data.components.defaults.diplomacy.war.populationFraction
import relativitization.universe.game.data.components.diplomacyData

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
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val hasEnemy: Boolean = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .diplomacyData().relationData.enemyIdSet.isNotEmpty()

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
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val hasSelfWar: Boolean = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .diplomacyData().relationData.selfWarDataMap.isNotEmpty()
        val hasSubordinateWar: Boolean = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .diplomacyData().relationData.subordinateWarDataMap.isNotEmpty()
        val hasAllyWar: Boolean = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .diplomacyData().relationData.allyWarDataMap.isNotEmpty()


        return if (hasSelfWar || hasSubordinateWar || hasAllyWar) {
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
 * @property otherPlayerId the id of the other player to consider
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
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val atWarWithPlayer: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.diplomacyData().relationData.selfWarDataMap
            .containsKey(otherPlayerId)

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
 * @property otherPlayerId the id of the other player
 * @property minMultiplier minimum of the multiplier
 * @property maxMultiplier maximum of the multiplier
 * @property rank rank of the dual utility data
 * @property bonus bonus of the dual utility data
 */
class WarLossConsideration(
    private val otherPlayerId: Int,
    private val minMultiplier: Double,
    private val maxMultiplier: Double,
    private val rank: Int,
    private val bonus: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {

        val hasWar: Boolean = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .diplomacyData().relationData.selfWarDataMap.containsKey(otherPlayerId)

        val lossFraction: Double = if (hasWar) {
            val warData: MutableWarData = planDataAtPlayer.getCurrentMutablePlayerData()
                .playerInternalData.diplomacyData().relationData.selfWarDataMap.getValue(otherPlayerId)
            1.0 - warData.populationFraction(
                planState.totalAdultPopulation(planDataAtPlayer)
            )
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

/**
 * Check if this player is in defensive war with target
 *
 * @property playerId the id of the player to consider
 * @property warTargetId the id of the war target
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class InDefensiveWarConsideration(
    private val playerId: Int,
    private val warTargetId: Int,
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
        val otherWarDataMap: Map<Int, MutableWarData> = planDataAtPlayer
            .getMutablePlayerData(playerId).playerInternalData
            .diplomacyData().relationData.selfWarDataMap

        val hasWar: Boolean = otherWarDataMap.containsKey(playerId)
        val isDefensive: Boolean = if (hasWar) {
            otherWarDataMap.getValue(playerId).warCoreData.isDefensive
        } else {
            false
        }

        return if (isDefensive) {
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