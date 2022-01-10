package relativitization.universe.ai.defaults.node.self.carrier

import relativitization.universe.ai.defaults.utils.DoNothingDualUtilityOption
import relativitization.universe.ai.defaults.utils.DualUtilityOption
import relativitization.universe.ai.defaults.utils.DualUtilityReasoner
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer

class SplitCarrierReasoner : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0)
    )
}