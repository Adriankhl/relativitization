package relativitization.universe.game.ai.defaults.score

import relativitization.universe.game.data.PlanDataAtPlayer
import relativitization.universe.game.data.components.PopSystemData
import relativitization.universe.game.data.components.popSystemData

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

    /**
     * Compute the military score while excluding a player and all its subordinates
     */
    fun computeWithExclusion(
        playerId: Int,
        excludePlayerId: Int,
        planDataAtPlayer: PlanDataAtPlayer,
    ): Double {
        // Exclude self and subordinate of self when comparing to the target
        val targetSubordinateAndSelfIdList: List<Int> = planDataAtPlayer.universeData3DAtPlayer.get(
            playerId
        ).getSubordinateAndSelfIdSet().filter {
            planDataAtPlayer.universeData3DAtPlayer.playerDataMap.containsKey(it)
        }.filter {
            !planDataAtPlayer.universeData3DAtPlayer.get(excludePlayerId).getSubordinateAndSelfIdSet().contains(it)
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