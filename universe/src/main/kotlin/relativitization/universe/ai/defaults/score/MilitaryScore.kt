package relativitization.universe.ai.defaults.score

import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.PopSystemData

object MilitaryScore {
    fun compute(
        playerId: Int,
        planDataAtPlayer: PlanDataAtPlayer,
    ): Double {
        // Exclude self and subordinate of self when comparing to the target
        val targetSubordinateAndSelfIdList: List<Int> = planDataAtPlayer.universeData3DAtPlayer.get(
            playerId
        ).getSubordinateAndSelfIdSet().filter {
            planDataAtPlayer.universeData3DAtPlayer.playerDataMap.containsKey(it)
        }

        val allTargetPopSystemData: List<PopSystemData> = targetSubordinateAndSelfIdList.map {
            planDataAtPlayer.universeData3DAtPlayer.get(it).playerInternalData.popSystemData()
        }

        return allTargetPopSystemData.fold(0.0) { totalScore, popSystemData ->
            totalScore + popSystemData.carrierDataMap.values.fold(0.0) { localScore, carrierData ->
                localScore + (carrierData.allPopData.soldierPopData.militaryBaseData.attack * 5.0 +
                        carrierData.allPopData.soldierPopData.militaryBaseData.shield)
            }
        }
    }
}