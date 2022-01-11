package relativitization.universe.ai.defaults.node.self

import relativitization.universe.ai.defaults.node.self.construction.FactoryReasoner
import relativitization.universe.ai.defaults.node.self.construction.InstituteReasoner
import relativitization.universe.ai.defaults.node.self.construction.LaboratoryReasoner
import relativitization.universe.ai.defaults.node.self.event.EventReasoner
import relativitization.universe.ai.defaults.node.self.carrier.CarrierReasoner
import relativitization.universe.ai.defaults.node.self.carrier.SplitCarrierReasoner
import relativitization.universe.ai.defaults.node.self.movement.MovementReasoner
import relativitization.universe.ai.defaults.node.self.politics.PolicyReasoner
import relativitization.universe.ai.defaults.node.self.pop.SalaryReasoner
import relativitization.universe.ai.defaults.node.self.storage.BalanceFuelReasoner
import relativitization.universe.ai.defaults.node.self.storage.BalanceResourceReasoner
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
        PolicyReasoner(),
        BalanceFuelReasoner(),
        BalanceResourceReasoner(),
        FactoryReasoner(),
        InstituteReasoner(),
        LaboratoryReasoner(),
        SalaryReasoner(),
        CarrierReasoner(),
        MovementReasoner(),
        SplitCarrierReasoner(),
    )
}