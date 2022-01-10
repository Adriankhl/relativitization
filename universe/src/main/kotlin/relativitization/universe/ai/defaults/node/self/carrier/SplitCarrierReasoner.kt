package relativitization.universe.ai.defaults.node.self.carrier

import relativitization.universe.ai.defaults.consideration.carrier.NoSpaceShipConsideration
import relativitization.universe.ai.defaults.consideration.carrier.NumberOfSpaceShipConsideration
import relativitization.universe.ai.defaults.consideration.carrier.SufficientPopulationRatioConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer

class SplitCarrierReasoner : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        SplitCarrierOption(),
        DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0)
    )
}

class SplitCarrierOption : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf(
            NoSpaceShipConsideration(
                rankIfTrue = 0,
                multiplierIfTrue = 0.0,
                bonusIfTrue = 0.0,
            ),
            SufficientPopulationRatioConsideration(
                ratio = 0.001,
                rankIfTrue = 1,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 1.0,
                rankIfFalse = 0,
                multiplierIfFalse = 0.0,
                bonusIfFalse = 0.0
            ),
            NumberOfSpaceShipConsideration(
                initialMultiplier = 0.001,
                exponent = 2.0,
                rank = 0,
                bonus = 0.0
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        TODO("Not yet implemented")
    }
}