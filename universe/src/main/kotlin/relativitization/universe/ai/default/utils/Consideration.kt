package relativitization.universe.ai.default.utils

import relativitization.universe.data.PlanDataAtPlayer

interface Consideration {
    fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer, planStatus: PlanStatus
    ): DualUtilityData
}
