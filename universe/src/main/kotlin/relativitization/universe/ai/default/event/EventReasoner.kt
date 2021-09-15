package relativitization.universe.ai.default.event

import relativitization.universe.ai.default.utils.*
import relativitization.universe.data.PlanDataAtPlayer

class EventReasoner() : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planStatus: PlanStatus
    ): List<AINode> {
        return listOf(
            PickMoveToDouble3DEventReasoner()
        )
    }
}