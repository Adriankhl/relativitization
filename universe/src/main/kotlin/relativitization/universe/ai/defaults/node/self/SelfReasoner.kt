package relativitization.universe.ai.defaults.node.self

import relativitization.universe.ai.defaults.node.self.construction.FactoryReasoner
import relativitization.universe.ai.defaults.node.self.event.EventReasoner
import relativitization.universe.ai.defaults.node.self.pop.SalaryReasoner
import relativitization.universe.ai.defaults.node.self.resource.BalanceFuelAndResourceReasoner
import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer

class SelfReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        EventReasoner(),
        BalanceFuelAndResourceReasoner(),
        FactoryReasoner(),
        SalaryReasoner(),
    )
}