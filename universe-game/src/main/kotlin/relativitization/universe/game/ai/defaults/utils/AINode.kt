package relativitization.universe.game.ai.defaults.utils

import relativitization.universe.core.data.PlanDataAtPlayer

abstract class AINode {
    abstract fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState)
}