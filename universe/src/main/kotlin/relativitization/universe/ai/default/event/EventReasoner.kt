package relativitization.universe.ai.default.event

import relativitization.universe.ai.default.utils.AINode
import relativitization.universe.ai.default.utils.PlanState
import relativitization.universe.ai.default.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer

class EventReasoner() : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return listOf(
            PickMoveToDouble3DEventReasoner()
        )
    }
}