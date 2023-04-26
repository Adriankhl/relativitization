package relativitization.universe.game.ai.defaults.node.subordinate.direct

import relativitization.universe.core.data.PlanDataAtPlayer
import relativitization.universe.game.ai.defaults.node.subordinate.direct.movement.DirectSubordinateMovementReasoner
import relativitization.universe.game.ai.defaults.utils.AINode
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.game.ai.defaults.utils.SequenceReasoner
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