package relativitization.universe.ai.defaults.node.subordinate.direct

import relativitization.universe.ai.defaults.node.subordinate.direct.movement.DirectSubordinateMovementReasoner
import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.PlayerData
import relativitization.universe.data.components.defaults.physics.Int3D
import relativitization.universe.maths.physics.Intervals

class DirectSubordinateReasoner : SequenceReasoner() {
    override fun getSubNodeList(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState): List<AINode> {
        val directSubordinateList: List<Int> = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .directSubordinateIdList

        val recentCommandTimeMap: Map<Int, Int> =
            planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.aiData().recentCommandTimeMap

        val currentTime: Int = planDataAtPlayer.universeData3DAtPlayer.center.t
        val currentInt3D: Int3D = planDataAtPlayer.universeData3DAtPlayer.center.toInt3D()

        // Only consider direct subordinates that this player has not recently sent command to
        val recentCommandTooOldSet: Set<Int> =  recentCommandTimeMap.filterKeys {
            directSubordinateList.contains(it)
        }.filter { (id, time) ->
            val subordinateData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(id)

            // Time needed for information to travel to the player and back
            val timeRequired: Int = Intervals.intDelay(
                currentInt3D,
                subordinateData.int4D.toInt3D(),
                planDataAtPlayer.universeData3DAtPlayer.universeSettings.speedOfLight
            ) * 2

            timeRequired <= currentTime - time
        }.keys

        val directSubordinateToConsiderList: List<Int> = directSubordinateList.filter {
            !recentCommandTimeMap.containsKey(it) || recentCommandTooOldSet.contains(it)
        }

        return directSubordinateToConsiderList.flatMap {
            listOf(
                DirectSubordinateMovementReasoner(it),
            )
        }
    }
}