package relativitization.universe.ai.defaults.node.self.carrier

import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer

class SplitCarrierReasoner : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0)
    )
}

class SplitCarrierOption : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf()
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        TODO("Not yet implemented")
    }
}