package relativitization.universe.ai.default.event

import relativitization.universe.ai.default.utils.Consideration
import relativitization.universe.ai.default.utils.Option
import relativitization.universe.ai.default.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer

class EventReasoner(private val planDataAtPlayer: PlanDataAtPlayer) : SequenceReasoner() {
    override fun getOptionList(): List<Option> {
        return listOf(
            PickMoveToDouble3DEventReasoner(planDataAtPlayer)
        )
    }

    override fun getConsiderationList(): List<Consideration> = listOf()
}