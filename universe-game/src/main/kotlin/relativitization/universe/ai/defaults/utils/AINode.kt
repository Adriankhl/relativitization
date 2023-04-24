package relativitization.universe.ai.defaults.utils

import relativitization.universe.data.PlanDataAtPlayer

abstract class AINode {
    abstract fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState)
}