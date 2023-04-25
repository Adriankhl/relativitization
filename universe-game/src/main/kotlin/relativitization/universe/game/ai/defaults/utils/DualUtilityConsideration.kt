package relativitization.universe.game.ai.defaults.utils

import relativitization.universe.game.data.PlanDataAtPlayer

abstract class DualUtilityConsideration {
    abstract fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer, planState: PlanState
    ): DualUtilityData
}

/**
 * Plain consideration, the utility does not depend on the player data
 */
class PlainDualUtilityConsideration(
    val rank: Int,
    val multiplier: Double,
    val bonus: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData = DualUtilityData(
        rank = rank,
        multiplier = multiplier,
        bonus = bonus,
    )
}