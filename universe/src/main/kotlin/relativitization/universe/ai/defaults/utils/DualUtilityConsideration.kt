package relativitization.universe.ai.defaults.utils

import relativitization.universe.data.PlanDataAtPlayer

interface DualUtilityConsideration {
    fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer, planState: PlanState
    ): DualUtilityData
}

/**
 * Plain consideration, the utility does not depends on the player data
 */
class PlainDualUtilityConsideration(
    val rank: Int,
    val multiplier: Double,
    val bonus: Double,
) : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData = DualUtilityData(
        rank = rank,
        multiplier = multiplier,
        bonus = bonus,
    )
}