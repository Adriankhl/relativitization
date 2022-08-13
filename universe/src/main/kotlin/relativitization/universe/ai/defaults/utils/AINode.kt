package relativitization.universe.ai.defaults.utils

import relativitization.universe.data.PlanDataAtPlayer
import kotlin.random.Random

abstract class AINode {
    abstract fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState)
}