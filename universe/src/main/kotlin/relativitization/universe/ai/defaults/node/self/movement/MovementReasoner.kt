package relativitization.universe.ai.defaults.node.self.movement

import relativitization.universe.ai.defaults.consideration.event.HasMovementEventConsideration
import relativitization.universe.ai.defaults.consideration.population.HigherPopulationDensityThenNeighborCubeConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer

class MovementReasoner : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        MoveToLowerDensityCubeOption(),
        DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0)
    )
}

/**
 * Move to a neighbouring cube with lower density
 */
class MoveToLowerDensityCubeOption : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> = listOf(
        HasMovementEventConsideration(rankIfTrue = 0, multiplierIfTrue = 0.0, bonusIfTrue = 0.0),
        HigherPopulationDensityThenNeighborCubeConsideration(
            rankIfTrue = 1,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 1.0,
            rankIfFalse = 0,
            multiplierIfFalse = 0.0,
            bonusIfFalse = 0.0
        )
    )

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        TODO("Not yet implemented")
    }
}