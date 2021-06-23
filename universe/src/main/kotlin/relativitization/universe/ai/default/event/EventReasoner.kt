package relativitization.universe.ai.default.event

import relativitization.universe.ai.default.utils.Consideration
import relativitization.universe.ai.default.utils.DecisionData
import relativitization.universe.ai.default.utils.Option
import relativitization.universe.ai.default.utils.SequenceReasoner

class EventReasoner(private val decisionData: DecisionData) : SequenceReasoner() {
    override fun getOptionList(): List<Option> {
        return listOf(
            PickMoveToDouble3DEventReasoner(decisionData)
        )
    }

    override fun getConsiderationList(): List<Consideration> = listOf()
}