package relativitization.universe.ai.defaults.consideration.event

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.DualUtilityDataFactory
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.events.MoveToDouble3DEvent

/**
 * Check if there are any movement event
 *
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 */
class HasMovementEventConsideration(
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
) : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val hasMovementEvent: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.eventDataMap.values.any {
                it.event is MoveToDouble3DEvent
            }

        return if (hasMovementEvent) {
            DualUtilityData(rank = rankIfTrue, multiplier = multiplierIfTrue, bonus = bonusIfTrue)
        } else {
            DualUtilityDataFactory.noImpact()
        }
    }
}