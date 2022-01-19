package relativitization.universe.ai.defaults.consideration.military

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.MutablePopSystemData
import relativitization.universe.data.components.PopSystemData

/**
 * Compare the military strength of this player to target player, exclude this player and the subordinate of this
 * player if they are subordinates of the target
 *
 * @property targetPlayerId compare with the player with this id
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class LargerMilitaryStrengthConsideration(
    private val targetPlayerId: Int,
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
        val subordinateAndSelfIdList: List<Int> = planDataAtPlayer.universeData3DAtPlayer
            .getCurrentPlayerData().playerInternalData.subordinateIdList + planDataAtPlayer.universeData3DAtPlayer
            .getCurrentPlayerData().playerId

        val allPopSystemData: List<PopSystemData> = subordinateAndSelfIdList.map {
            planDataAtPlayer.universeData3DAtPlayer.get(it).playerInternalData.popSystemData()
        }

        // Exclude self and subordinate of self when comparing to the target
        val targetSubordinateAndSelfIdList: List<Int> = (planDataAtPlayer.universeData3DAtPlayer.get(
            targetPlayerId
        ).playerInternalData.subordinateIdList + targetPlayerId).filter {
            !subordinateAndSelfIdList.contains(it)
        }

        val allTargetPopSystemData: List<PopSystemData> = targetSubordinateAndSelfIdList.map {
            planDataAtPlayer.universeData3DAtPlayer.get(it).playerInternalData.popSystemData()
        }

        val militaryScore: Double = allPopSystemData.fold(0.0) { totalScore, popSystemData ->
            totalScore + popSystemData.carrierDataMap.values.fold(0.0) { localScore, carrierData ->
                localScore + (carrierData.allPopData.soldierPopData.militaryBaseData.attack * 5.0 +
                        carrierData.allPopData.soldierPopData.militaryBaseData.shield)
            }
        }

        val targetMilitaryScore: Double = allTargetPopSystemData.fold(0.0) { totalScore, popSystemData ->
            totalScore + popSystemData.carrierDataMap.values.fold(0.0) { localScore, carrierData ->
                localScore + (carrierData.allPopData.soldierPopData.militaryBaseData.attack * 5.0 +
                        carrierData.allPopData.soldierPopData.militaryBaseData.shield)
            }
        }

        return if (militaryScore >= targetMilitaryScore) {
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