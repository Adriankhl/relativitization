package relativitization.universe.ai.defaults.utils

import relativitization.universe.data.PlanDataAtPlayer

interface DualUtilityConsideration {
    fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer, planState: PlanState
    ): DualUtilityData
}

/**
 * Basic consideration, all rank, multiplier and bonus equals to one, so it place the option to
 * lowest priority if only with this consideration
 */
class AllOneDualUtilityConsideration : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData = DualUtilityData(
        rank = 1,
        multiplier = 1.0,
        bonus = 1.0
    )
}