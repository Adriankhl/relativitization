package relativitization.universe.ai.defaults.consideration.fuel

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.DualUtilityDataFactory
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.defaults.ai.MutableFuelRestMassHistoryData

/**
 * Whether production fuel is increasing comparing to historical average
 *
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 */
class IncreasingProductionFuelConsideration(
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
) : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val fuelHistory: MutableFuelRestMassHistoryData = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData.aiData().fuelRestMassHistoryData

        val isIncreasing: Boolean = fuelHistory.isLastProductionFuelGreaterThanAverage(
            turn = fuelHistory.maxStoredTurn,
            compareFactor = 1.0,
        )

        return if (isIncreasing) {
            DualUtilityData(rank = rankIfTrue, multiplier = multiplierIfTrue, bonus = bonusIfTrue)
        } else {
            DualUtilityDataFactory.noImpact()
        }
    }
}