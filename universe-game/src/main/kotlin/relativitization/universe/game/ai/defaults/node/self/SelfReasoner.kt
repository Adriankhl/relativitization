package relativitization.universe.game.ai.defaults.node.self

import relativitization.universe.game.ai.defaults.node.self.carrier.CarrierReasoner
import relativitization.universe.game.ai.defaults.node.self.carrier.SplitCarrierReasoner
import relativitization.universe.game.ai.defaults.node.self.construction.FactoryReasoner
import relativitization.universe.game.ai.defaults.node.self.construction.InstituteReasoner
import relativitization.universe.game.ai.defaults.node.self.construction.LaboratoryReasoner
import relativitization.universe.game.ai.defaults.node.self.economy.TaxReasoner
import relativitization.universe.game.ai.defaults.node.self.event.EventReasoner
import relativitization.universe.game.ai.defaults.node.self.movement.MovementReasoner
import relativitization.universe.game.ai.defaults.node.self.politics.PolicyReasoner
import relativitization.universe.game.ai.defaults.node.self.pop.SalaryReasoner
import relativitization.universe.game.ai.defaults.node.self.storage.BalanceFuelReasoner
import relativitization.universe.game.ai.defaults.node.self.storage.BalanceResourceReasoner
import relativitization.universe.game.ai.defaults.utils.AINode
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.game.ai.defaults.utils.SequenceReasoner
import relativitization.universe.core.data.PlanDataAtPlayer
import kotlin.random.Random

class SelfReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        EventReasoner(random),
        PolicyReasoner(),
        BalanceFuelReasoner(),
        BalanceResourceReasoner(),
        FactoryReasoner(random),
        InstituteReasoner(random),
        LaboratoryReasoner(random),
        SalaryReasoner(random),
        CarrierReasoner(random),
        TaxReasoner(),
        MovementReasoner(random),
        SplitCarrierReasoner(random),
    )
}