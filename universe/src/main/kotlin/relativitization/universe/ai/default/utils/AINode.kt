package relativitization.universe.ai.default.utils

import relativitization.universe.data.PlanDataAtPlayer

interface AINode {
    fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState)
}