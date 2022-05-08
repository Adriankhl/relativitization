package relativitization.universe.ai.defaults.node.subordinate

import relativitization.universe.ai.defaults.node.subordinate.direct.DirectSubordinateReasoner
import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer
import kotlin.random.Random

class SubordinateReasoner(random: Random) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return listOf(
            DirectSubordinateReasoner(random),
        )
    }
}