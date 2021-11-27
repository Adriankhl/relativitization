package relativitization.universe.ai.defaults.utils

import relativitization.universe.data.PlanDataAtPlayer

interface AINode {
    fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState)
}