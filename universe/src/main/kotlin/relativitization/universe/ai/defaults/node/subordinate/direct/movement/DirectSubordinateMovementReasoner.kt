package relativitization.universe.ai.defaults.node.subordinate.direct.movement

import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer

class DirectSubordinateMovementReasoner(
    val directSubordinateId: Int
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        MoveDirectSubordinateToEnemyOption(directSubordinateId),
        DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0),
    )
}

class MoveDirectSubordinateToEnemyOption(
    val directSubordinateId: Int
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> = listOf(

    )

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
    }
}