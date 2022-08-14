package relativitization.universe.ai.defaults.node.subordinate.direct

import relativitization.universe.ai.defaults.node.subordinate.direct.movement.DirectSubordinateMovementReasoner
import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer
import kotlin.random.Random

class DirectSubordinateReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState): List<AINode> {
        val directSubordinateSet: Set<Int> = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .directSubordinateIdSet

        val directSubordinateToConsiderList: List<Int> = directSubordinateSet.filter {
            planDataAtPlayer.universeData3DAtPlayer.playerDataMap.containsKey(it)
        }.filter {
            !planState.isCommandSentRecently(it, planDataAtPlayer)
        }

        return directSubordinateToConsiderList.flatMap {
            listOf(
                DirectSubordinateMovementReasoner(it, random),
            )
        }
    }
}