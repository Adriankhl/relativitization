package relativitization.universe.ai.default.event

import relativitization.universe.ai.default.utils.Consideration
import relativitization.universe.ai.default.utils.DecisionData
import relativitization.universe.ai.default.utils.Option
import relativitization.universe.ai.default.utils.SequenceReasoner
import relativitization.universe.data.commands.Command

class EventReasoner(private val decisionData: DecisionData) : SequenceReasoner() {
    override fun getOptionList(): List<Option> {
        return listOf()
    }

    override fun getConsiderationList(): List<Consideration> = listOf()
}