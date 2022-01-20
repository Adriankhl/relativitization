package relativitization.universe.ai.defaults.node.subordinate

import relativitization.universe.ai.defaults.node.subordinate.direct.DirectSubordinateReasoner
import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer

class SubordinateReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return listOf(
            DirectSubordinateReasoner(),
        )
    }
}