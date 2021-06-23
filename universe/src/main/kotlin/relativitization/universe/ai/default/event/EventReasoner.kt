package relativitization.universe.ai.default.event

import relativitization.universe.ai.default.utils.Consideration
import relativitization.universe.ai.default.utils.DecisionData
import relativitization.universe.ai.default.utils.Option
import relativitization.universe.ai.default.utils.SequenceReasoner
import relativitization.universe.data.commands.Command

class EventReasoner(private val decisionData: DecisionData) : SequenceReasoner() {
    override val considerationList: List<Consideration> = listOf()

    override val optionList: List<Option> = listOf()
}