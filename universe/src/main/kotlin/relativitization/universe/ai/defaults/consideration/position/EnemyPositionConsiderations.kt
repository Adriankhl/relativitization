package relativitization.universe.ai.defaults.consideration.position

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.PlayerData
import relativitization.universe.data.components.diplomacyData

/**
 * Check if there is any enemy in neighbouring cube
 *
 * @property range the range to check for enemy
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class EnemyNeighbourConsideration(
    private val range: Int,
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
        val neighbour: List<PlayerData> =
            planDataAtPlayer.universeData3DAtPlayer.getNeighbourInCube(range)

        val hasEnemy: Boolean = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .diplomacyData().relationData.enemyIdSet.any { enemyId ->
                neighbour.any {
                    it.playerId == enemyId
                }
            }

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
 * Check if the player is fighting enemy, i.e., enemy in the same cube
 *
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class FightingEnemyConsideration(
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
        val sameCubeNeighbour: List<PlayerData> =
            planDataAtPlayer.universeData3DAtPlayer.getNeighbourInCube(1)

        val hasEnemy: Boolean = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .diplomacyData().relationData.enemyIdSet.any { enemyId ->
                    sameCubeNeighbour.any {
                        it.playerId == enemyId
                    }
                }

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