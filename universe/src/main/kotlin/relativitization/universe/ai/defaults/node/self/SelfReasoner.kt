package relativitization.universe.ai.defaults.node.self

import relativitization.universe.ai.defaults.node.self.construction.FactoryReasoner
import relativitization.universe.ai.defaults.node.self.construction.InstituteReasoner
import relativitization.universe.ai.defaults.node.self.construction.LaboratoryReasoner
import relativitization.universe.ai.defaults.node.self.event.EventReasoner
import relativitization.universe.ai.defaults.node.self.carrier.CarrierReasoner
import relativitization.universe.ai.defaults.node.self.carrier.SplitCarrierReasoner
import relativitization.universe.ai.defaults.node.self.economy.TaxReasoner
import relativitization.universe.ai.defaults.node.self.movement.MovementReasoner
import relativitization.universe.ai.defaults.node.self.politics.PolicyReasoner
import relativitization.universe.ai.defaults.node.self.pop.SalaryReasoner
import relativitization.universe.ai.defaults.node.self.storage.BalanceFuelReasoner
import relativitization.universe.ai.defaults.node.self.storage.BalanceResourceReasoner
import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer
import kotlin.random.Random

class SelfReasoner(random: Random) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        EventReasoner(random),
        PolicyReasoner(random),
        BalanceFuelReasoner(random),
        BalanceResourceReasoner(random),
        FactoryReasoner(random),
        InstituteReasoner(random),
        LaboratoryReasoner(random),
        SalaryReasoner(random),
        CarrierReasoner(random),
        TaxReasoner(random),
        MovementReasoner(random),
        SplitCarrierReasoner(random),
    )
}