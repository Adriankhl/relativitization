package relativitization.universe.ai.defaults.utils

import relativitization.universe.data.PlanDataAtPlayer

interface DualUtilityConsideration {
    fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer, planState: PlanState
    ): DualUtilityData
}
