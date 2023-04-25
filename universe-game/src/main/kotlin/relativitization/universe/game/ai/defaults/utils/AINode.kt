package relativitization.universe.game.ai.defaults.utils

import relativitization.universe.game.data.PlanDataAtPlayer

abstract class AINode {
    abstract fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState)
}